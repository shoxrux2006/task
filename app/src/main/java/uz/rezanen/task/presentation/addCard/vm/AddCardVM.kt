package uz.rezanen.task.presentation.addCard.vm

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import uz.rezanen.task.utils.AppViewModel

interface AddCardVM : AppViewModel<AddCardIntent, AddCardUIState, AddCardSideEffect> {}

sealed class AddCardIntent {
    data class EditingCardNumber(val cardNumber: String) : AddCardIntent()
    data class EditingExpireDate(val expireDate: String) : AddCardIntent()
    data object SubmitButton : AddCardIntent()
    data object BackButton : AddCardIntent()
}

sealed class AddCardUIState {
    data class DataFilled(val enableSaveButton: Boolean) : AddCardUIState()
    data class Loading(val isLoading: Boolean,val enableSaveButton: Boolean?=null) : AddCardUIState()
    data class Error(val message: String) : AddCardUIState()
    data object Success : AddCardUIState()
}

sealed class AddCardSideEffect {
    data class Message(
        val message: String,
        val color: Color? = null,
        val icon: ImageVector? = null
    ) : AddCardSideEffect()
}