package uz.rezanen.task.presentation.addCard.vm

import uz.rezanen.task.utils.AppViewModel

interface AddCardVM : AppViewModel<AddCardIntent, AddCardUIState, AddCardSideEffect> {
}

sealed class AddCardIntent {
    data class SubmitButton(val login: String, val pass: String) : AddCardIntent()
}

sealed class AddCardUIState {
    data class Loading(val isLoading: Boolean) : AddCardUIState()
    data class Error(val message: String) : AddCardUIState()
    data object Success : AddCardUIState()
}

sealed class AddCardSideEffect {
    data class Message(val message: String) : AddCardSideEffect()
}