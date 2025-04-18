package uz.rezanen.task.presentation.login

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import kotlinx.coroutines.delay
import org.koin.androidx.compose.getViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.rezanen.task.localization.LocalStrings
import uz.rezanen.task.presentation.AppButton
import uz.rezanen.task.presentation.FloatingTopToast
import uz.rezanen.task.presentation.PhoneNumberField
import uz.rezanen.task.presentation.login.vm.LoginIntent
import uz.rezanen.task.presentation.login.vm.LoginSideEffect
import uz.rezanen.task.presentation.login.vm.LoginUIState
import uz.rezanen.task.presentation.login.vm.LoginVM
import uz.rezanen.task.presentation.login.vm.LoginVMImpl

class LoginScreen : AndroidScreen() {
    @Composable
    override fun Content() {
        val viewModel: LoginVM = getViewModel<LoginVMImpl>()
        val uiState = viewModel.collectAsState().value
        var sideEffectMessage by remember {
            mutableStateOf("")
        }
        var showToast by remember { mutableStateOf(false) }
        var context: Context? by remember {
            mutableStateOf(null)
        }
        context = LocalContext.current
        viewModel.collectSideEffect {
            when (it) {
                is LoginSideEffect.Message -> {
                    sideEffectMessage = it.message
                    showToast = true
                    delay(2000)
                    showToast = false
                }
            }
        }

        Box {
            MainScreenContent(uiState, viewModel::onEventDispatcher)
            FloatingTopToast(
                message = sideEffectMessage,
                show = showToast,
                ) {

            }
        }

    }

    @Composable
    fun MainScreenContent(uiState: LoginUIState, onEventDispatcher: (LoginIntent) -> Unit) {
        var errorPhone by remember {
            mutableStateOf("")
        }
        var phoneNumber by remember {
            mutableStateOf("")
        }
        var buttonLoading by remember {
            mutableStateOf(false)
        }


        when (uiState) {
            LoginUIState.Success -> {
            }

            is LoginUIState.Error -> {
                buttonLoading = false
                errorPhone = uiState.phoneError
            }

            is LoginUIState.Loading -> {
                buttonLoading = uiState.buttonLoading
            }

            LoginUIState.NoConnection -> {

            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(
                    horizontal = 15.dp
                ),
        ) {

            Spacer(modifier = Modifier.weight(1f))
            PhoneNumberField(hint = LocalStrings.current.phoneNumber, errorMessage = errorPhone) {
                phoneNumber = it
            }
            Spacer(modifier = Modifier.height(20.dp))
            AppButton(loading = buttonLoading, text = LocalStrings.current.register) {
                onEventDispatcher(LoginIntent.RegisterButton(phoneNumber))
            }
            Spacer(modifier = Modifier.weight(1f))


        }


    }


}