package uz.rezanen.task.presentation.wallet.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import uz.rezanen.task.domain.CardRepository
import uz.rezanen.task.domain.WalletRepository
import uz.rezanen.task.domain.data.CardItem
import uz.rezanen.task.navigation.AppNavigation
import uz.rezanen.task.presentation.addCard.AddCardScreen
import uz.rezanen.task.shp.Shp
import uz.rezanen.task.utils.NetworkResponse


class WalletVMImpl(
    private val shp: Shp,
    private val walletRepository: WalletRepository,
    private val cardRepository: CardRepository,
    private val navigation: AppNavigation
) : WalletVM, ViewModel() {
    override fun onEventDispatcher(intent: WalletIntent) = intent {
        when (intent) {
            WalletIntent.AddCard -> {
                navigation.navigateTo(AddCardScreen())
            }

            WalletIntent.AddPromoCode -> {
                reduce {
                    WalletUIState.ShowPromoCodeBottomSheet
                }
            }

            is WalletIntent.ChangePaymentMethod -> {

            }
        }


    }


    override val container: Container<WalletUIState, WalletSideEffect> =
        container(WalletUIState.Initial)


    private fun refreshCard() = intent {
        viewModelScope.launch {
            walletRepository.getWallet().collectLatest {
                when (it) {
                    is NetworkResponse.Error -> {
                        reduce {
                            WalletUIState.Error(
                                it.message
                            )
                        }
                    }

                    is NetworkResponse.Failure -> {
                        postSideEffect(WalletSideEffect.Message("Check your code!"))
                    }

                    is NetworkResponse.Loading -> {
                        reduce {
                            WalletUIState.Loading(
                                balanceLoading = false, cardsLoading = it.isLoading
                            )
                        }
                    }

                    is NetworkResponse.NoConnection -> {
                        postSideEffect(WalletSideEffect.Message("No connection!"))
                    }

                    is NetworkResponse.Success -> {
                        reduce {
                            WalletUIState.Success(
                                balance = "",
                                cards = null
                            )
                        }
                    }
                }
            }
        }
    }

    init {
        intent {
            viewModelScope.launch {
                cardRepository.getCards().collectLatest {
                    when (it) {
                        is NetworkResponse.Error -> {
                            reduce {
                                WalletUIState.Error(
                                    it.message
                                )
                            }
                        }

                        is NetworkResponse.Failure -> {
                            postSideEffect(WalletSideEffect.Message("Check your code!"))
                        }

                        is NetworkResponse.Loading -> {
                            reduce {
                                WalletUIState.Loading(
                                    balanceLoading = false, cardsLoading = it.isLoading
                                )
                            }
                        }

                        is NetworkResponse.NoConnection -> {
                            postSideEffect(WalletSideEffect.Message("No connection!"))
                        }

                        is NetworkResponse.Success -> {
                            it.data?.let { data ->

                                reduce {
                                    WalletUIState.Success(
                                        balance = null,
                                        cards = data.map { item ->
                                            CardItem(
                                                item.id,
                                                item.number,
                                                item.expireDate,
                                                item.userId
                                            )
                                        }.toList()
                                    )
                                }
                            }
                        }
                    }
                }
                refreshCard()
            }
        }
    }

}