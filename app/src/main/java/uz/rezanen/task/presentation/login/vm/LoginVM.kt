package uz.rezanen.task.presentation.login.vm

import uz.rezanen.task.utils.AppViewModel

interface LoginVM : AppViewModel<LoginIntent, LoginUIState, LoginSideEffect> {
}

sealed class LoginIntent {
    data class RegisterButton(val phone: String) : LoginIntent()
}

sealed class LoginUIState {
    data class Loading(val buttonLoading: Boolean) : LoginUIState()
    data class Error(val phoneError: String) : LoginUIState()
    data object NoConnection : LoginUIState()
    data object Success : LoginUIState()
}

sealed class LoginSideEffect {
    data class Message(val message: String) : LoginSideEffect()
}