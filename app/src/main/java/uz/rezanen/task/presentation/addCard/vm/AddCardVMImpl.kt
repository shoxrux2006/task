package uz.rezanen.task.presentation.addCard.vm

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import uz.rezanen.task.domain.CardRepository
import uz.rezanen.task.navigation.AppNavigation
import uz.rezanen.task.presentation.wallet.WalletScreen
import uz.rezanen.task.remote.request.AddCardRequest
import uz.rezanen.task.utils.NetworkResponse


class AddCardVMImpl(
    private val navigation: AppNavigation,
    private val cardRepository: CardRepository,
) : AddCardVM, ViewModel() {
    private var cardNumber: String = ""
    private var expireDate: String = ""
    override fun onEventDispatcher(intent: AddCardIntent) = intent {
        when (intent) {
            is AddCardIntent.SubmitButton -> {
                viewModelScope.launch {
                    cardRepository.addCard(
                        AddCardRequest(
                            number = cardNumber, expireDate = expireDate
                        )
                    ).collectLatest {
                        when (it) {
                            is NetworkResponse.Error -> {
                                postSideEffect(
                                    AddCardSideEffect.Message(
                                        it.message,
                                        color = Color(0xFFFF0000),
                                        icon = Icons.Filled.Warning
                                    )
                                )
                            }

                            is NetworkResponse.Failure -> {
                                postSideEffect(AddCardSideEffect.Message("Check your code!"))
                            }

                            is NetworkResponse.Loading -> {
                                reduce {
                                    AddCardUIState.Loading(
                                        isLoading = it.isLoading
                                    )
                                }
                            }

                            is NetworkResponse.NoConnection -> {
                                postSideEffect(
                                    AddCardSideEffect.Message(
                                        "No connection!",
                                        color = Color(0xFFFF0000),
                                        icon = Icons.Outlined.Refresh
                                    )
                                )
                            }

                            is NetworkResponse.Success -> {
                                it.data?.let { item ->
                                    reduce {
                                        AddCardUIState.Loading(
                                            isLoading = false, enableSaveButton = false
                                        )
                                    }


                                    postSideEffect(
                                        AddCardSideEffect.Message(
                                            "Saved!",
                                            color = Color(0xFF31AA24),
                                            icon = Icons.Outlined.Done
                                        )
                                    )

                                    delay(1000)
                                    navigation.replaceWith(WalletScreen(addedCard = true))
                                }

                            }
                        }
                    }
                }
            }

            AddCardIntent.BackButton -> {
                navigation.back()
            }

            is AddCardIntent.EditingCardNumber -> {
                cardNumber = intent.cardNumber
                cardNumber = cardNumber.replace("-", "")
                reduce {
                    AddCardUIState.DataFilled(enableSaveButton = cardNumber.length == 16 && expireDate.length == 5)
                }
            }

            is AddCardIntent.EditingExpireDate -> {
                expireDate = intent.expireDate
                reduce {
                    AddCardUIState.DataFilled(enableSaveButton = cardNumber.length == 16 && expireDate.length == 5)
                }
            }
        }


    }


    override val container: Container<AddCardUIState, AddCardSideEffect> =
        container(AddCardUIState.Success)


}