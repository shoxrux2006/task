package uz.rezanen.task.presentation.wallet.vm

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import uz.rezanen.task.domain.data.CardItem
import uz.rezanen.task.utils.AppViewModel

interface WalletVM : AppViewModel<WalletIntent, WalletUIState, WalletSideEffect> {}

sealed class WalletIntent {
    data object Refresh : WalletIntent()
    data object AddCard : WalletIntent()
    data object AddPromoCode : WalletIntent()
    data class CheckPromoCode(val code: String) : WalletIntent()
    data class ChangePaymentMethod(
        val activeMethod: String, val cardId: Int
    ) : WalletIntent()
}

sealed class WalletUIState {
    data object Initial : WalletUIState()

    data class Loading(val balanceLoading: Boolean? = null, val cardsLoading: Boolean? = null) :
        WalletUIState()

    data class Error(val message: String) : WalletUIState()
    data class Success(
        val activeMethod: Int?,
        val balance: String?,
        val cards: List<CardItem>?
    ) :
        WalletUIState()
}

sealed class WalletSideEffect {
    data class ShowPromoCodeBottomSheet(val showSheet: Boolean, val errorMessage: String? = null) :
        WalletSideEffect()

    data class Message(
        val message: String,
        val color: Color? = null,
        val icon: ImageVector? = null
    ) :
        WalletSideEffect()
}