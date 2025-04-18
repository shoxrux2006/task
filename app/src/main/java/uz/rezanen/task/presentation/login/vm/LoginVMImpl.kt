package uz.rezanen.task.presentation.login.vm

import android.util.Log
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
import uz.rezanen.task.domain.LoginRepository
import uz.rezanen.task.navigation.AppNavigation
import uz.rezanen.task.presentation.wallet.WalletScreen
import uz.rezanen.task.remote.request.AddUserRequest
import uz.rezanen.task.utils.NetworkResponse


class LoginVMImpl(
    private val navigation: AppNavigation,
    private val loginRepository: LoginRepository,

) : LoginVM, ViewModel() {


    override fun onEventDispatcher(intent: LoginIntent) = intent {
        when (intent) {
            is LoginIntent.RegisterButton -> {
                reduce {
                    LoginUIState.Error(
                        if (intent.phone.length != 13) "phone number isn't correct" else "",
                    )
                }
                if (intent.phone.length == 13) {
                    viewModelScope.launch {
                        loginRepository.addUser(AddUserRequest(intent.phone)).collectLatest {
                            when (it) {
                                is NetworkResponse.Error -> {
                                    reduce {
                                        LoginUIState.Error(
                                            it.message
                                        )
                                    }
                                    if (it.statusCode != null && it.statusCode == 409) {
                                        loginRepository.setPhoneNumber(intent.phone)
                                        navigation.replaceWith(WalletScreen())
                                    }
                                }

                                is NetworkResponse.Failure -> {
                                    reduce {
                                        LoginUIState.Loading(buttonLoading = false)
                                    }
                                    postSideEffect(LoginSideEffect.Message("Check your code!"))
                                }

                                is NetworkResponse.Loading -> {
                                    reduce {
                                        LoginUIState.Loading(buttonLoading = it.isLoading)
                                    }
                                }

                                is NetworkResponse.NoConnection -> {
                                    reduce {
                                        LoginUIState.Loading(buttonLoading = false)
                                    }
                                    postSideEffect(LoginSideEffect.Message("No connection!"))
                                }

                                is NetworkResponse.Success -> {
                                    reduce {
                                        LoginUIState.Loading(buttonLoading = false)
                                    }
                                    navigation.replaceWith(WalletScreen())
                                }
                            }
                        }
                    }
                }
            }
        }


    }


    override val container: Container<LoginUIState, LoginSideEffect> =
        container(LoginUIState.Success)

    init {
        if (loginRepository.getPhoneNumber() != null) {
            viewModelScope.launch {
                delay(100)
                navigation.replaceWith(WalletScreen())
            }
        }
    }
}