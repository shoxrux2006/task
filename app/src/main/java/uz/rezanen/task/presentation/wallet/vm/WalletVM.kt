package uz.rezanen.task.presentation.wallet.vm

import uz.rezanen.task.domain.data.CardItem
import uz.rezanen.task.utils.AppViewModel

interface WalletVM : AppViewModel<WalletIntent, WalletUIState, WalletSideEffect> {}

sealed class WalletIntent {
    data object AddCard : WalletIntent()
    data object AddPromoCode : WalletIntent()
    data class ChangePaymentMethod(
        val isCash: Boolean, val cardNumber: String, val cardId: String = ""
    ) : WalletIntent()
}

sealed class WalletUIState {
    data object Initial : WalletUIState()
    data object ShowPromoCodeBottomSheet : WalletUIState()
    data class Loading(val balanceLoading: Boolean, val cardsLoading: Boolean) : WalletUIState()
    data class Error(val message: String) : WalletUIState()
    data class Success(val balance: String?, val cards: List<CardItem>?) : WalletUIState()
}

sealed class WalletSideEffect {
    data class Message(val message: String) : WalletSideEffect()
}