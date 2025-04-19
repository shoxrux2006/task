package uz.rezanen.task.presentation.wallet.vm

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
import uz.rezanen.task.domain.WalletRepository
import uz.rezanen.task.domain.data.CardItem
import uz.rezanen.task.navigation.AppNavigation
import uz.rezanen.task.presentation.addCard.AddCardScreen
import uz.rezanen.task.remote.request.AddPromoCodeRequest
import uz.rezanen.task.remote.request.SetActiveCardRequest
import uz.rezanen.task.utils.NetworkResponse


class WalletVMImpl(
    private val walletRepository: WalletRepository,
    private val cardRepository: CardRepository,
    private val navigation: AppNavigation
) : WalletVM, ViewModel() {
    private var balance: String? = ""
    private var cards: List<CardItem>? = listOf()
    override fun onEventDispatcher(intent: WalletIntent) = intent {
        when (intent) {
            WalletIntent.AddCard -> {
                navigation.navigateTo(AddCardScreen())
            }

            WalletIntent.AddPromoCode -> {
                postSideEffect(
                    WalletSideEffect.ShowPromoCodeBottomSheet(showSheet = true)
                )
            }

            is WalletIntent.ChangePaymentMethod -> {
                viewModelScope.launch {
                    cardRepository.setActiveCard(
                        SetActiveCardRequest(
                            activeMethod = intent.activeMethod, activeCardId = intent.cardId
                        )
                    ).collectLatest {
                        when (it) {
                            is NetworkResponse.Error -> {
                                postSideEffect(
                                    WalletSideEffect.Message(
                                        it.message,
                                        color = Color(0xFFFF0000),
                                        icon = Icons.Filled.Warning
                                    )
                                )
                            }

                            is NetworkResponse.Failure -> {
                                postSideEffect(WalletSideEffect.Message("Check your code!"))
                            }

                            is NetworkResponse.Loading -> {
                                reduce {
                                    WalletUIState.Loading(
                                        balanceLoading = it.isLoading,
                                    )
                                }
                            }

                            is NetworkResponse.NoConnection -> {
                                postSideEffect(
                                    WalletSideEffect.Message(
                                        "No connection!",
                                        color = Color(0xFFFF0000),
                                        icon = Icons.Outlined.Refresh
                                    )
                                )
                            }

                            is NetworkResponse.Success -> {
                                it.data?.let { item ->
                                    reduce {
                                        WalletUIState.Success(
                                            activeMethod = item.activeCardId,
                                            balance = item.balance.toString(),
                                            cards = cards
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            }

            is WalletIntent.CheckPromoCode -> {
                postSideEffect(
                    WalletSideEffect.ShowPromoCodeBottomSheet(
                        showSheet = intent.code.length < 3,
                        errorMessage = if (intent.code.length < 3) "Please provide promoCode" else null,
                    )
                )
                if (intent.code.length >= 3) {
                    viewModelScope.launch {
                        cardRepository.setPromoCode(
                            AddPromoCodeRequest(code = intent.code)
                        ).collectLatest {
                            when (it) {
                                is NetworkResponse.Error -> {
                                    postSideEffect(
                                        WalletSideEffect.Message(
                                            it.message,
                                            color = Color.Red,
                                            icon = Icons.Filled.Warning
                                        )
                                    )
                                }

                                is NetworkResponse.Failure -> {
                                    postSideEffect(WalletSideEffect.Message("Check your code!"))
                                }

                                is NetworkResponse.Loading -> {
                                    postSideEffect(
                                        WalletSideEffect.Message(
                                            "Loading",
                                            color = Color(0xFF423939),
                                            icon = Icons.Outlined.Refresh
                                        )
                                    )
                                }

                                is NetworkResponse.NoConnection -> {
                                    postSideEffect(
                                        WalletSideEffect.Message(
                                            "No connection!",
                                            color = Color(0xFFFF0000),
                                            icon = Icons.Outlined.Refresh
                                        )
                                    )
                                }

                                is NetworkResponse.Success -> {
                                    it.data?.let { item ->
                                        postSideEffect(
                                            WalletSideEffect.Message(
                                                "Added!",
                                                color = Color(0xFF31AA24),
                                                icon = Icons.Outlined.Done
                                            )
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }

            WalletIntent.Refresh -> {
                viewModelScope.launch {
                    refreshCards()
                    delay(500)
                    refreshBalance()
                }
            }
        }


    }


    override val container: Container<WalletUIState, WalletSideEffect> =
        container(WalletUIState.Initial)


    private fun refreshBalance() = intent {
        viewModelScope.launch {
            walletRepository.getWallet().collectLatest {
                when (it) {
                    is NetworkResponse.Error -> {
                        postSideEffect(
                            WalletSideEffect.Message(
                                it.message, color = Color(0xFFFF0000), icon = Icons.Filled.Warning
                            )
                        )
                    }

                    is NetworkResponse.Failure -> {
                        postSideEffect(WalletSideEffect.Message("Check your code!"))
                    }

                    is NetworkResponse.Loading -> {
                        reduce {
                            WalletUIState.Loading(
                                balanceLoading = it.isLoading,
                            )
                        }
                    }

                    is NetworkResponse.NoConnection -> {
                        postSideEffect(
                            WalletSideEffect.Message(
                                "No connection!", color = Color.Red, icon = Icons.Outlined.Refresh
                            )
                        )
                    }

                    is NetworkResponse.Success -> {
                        it.data?.let { item ->
                            balance = item.balance.toString()
                            reduce {
                                WalletUIState.Success(
                                    activeMethod = item.activeCardId,
                                    balance = balance,
                                    cards = cards
                                )
                            }
                        }

                    }
                }
            }
        }
    }

    private fun refreshCards() = intent {
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
                                cardsLoading = it.isLoading
                            )
                        }
                    }

                    is NetworkResponse.NoConnection -> {
                        postSideEffect(
                            WalletSideEffect.Message(
                                "No connection!",
                                color = Color(0xFFFF0000),
                                icon = Icons.Outlined.Refresh
                            )
                        )
                    }

                    is NetworkResponse.Success -> {
                        it.data?.let { data ->
                            cards = data.map { item ->
                                CardItem(
                                    item.id, item.number, item.expireDate, item.userId
                                )
                            }.toList()
                            reduce {
                                WalletUIState.Success(
                                    activeMethod = -1, balance = balance, cards = cards
                                )
                            }
                        }
                    }
                }
            }

        }
    }

    init {
        viewModelScope.launch {
            refreshCards()
            delay(500)
            refreshBalance()
        }


    }


}