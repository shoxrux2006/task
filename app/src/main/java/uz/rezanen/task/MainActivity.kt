package uz.rezanen.task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.lyricist.Lyricist
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.rememberStrings
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.get
import uz.rezanen.task.localization.AppStrings
import uz.rezanen.task.localization.LocalStrings
import uz.rezanen.task.localization.Locales
import uz.rezanen.task.localization.strings
import uz.rezanen.task.navigation.NavigationHandler
import uz.rezanen.task.presentation.addCard.AddCardScreen
import uz.rezanen.task.presentation.wallet.WalletScreen
import uz.rezanen.task.ui.theme.TaskTheme


class MainActivity : ComponentActivity() {
    private val navigationHandler: NavigationHandler = get()
    lateinit var lyricist: Lyricist<AppStrings>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            lyricist = rememberStrings(strings)
            ProvideStrings(lyricist, LocalStrings) {

                TaskTheme {
                    Navigator(screen = AddCardScreen(), onBackPressed = {
                        true
                    }) { navigator ->
                        LaunchedEffect(navigator) {
                            navigationHandler.navStack.onEach { it.invoke(navigator) }
                                .launchIn(this)

                        }
                        val currentScreen = navigator.lastItemOrNull ?: WalletScreen()
                        currentScreen.Content()

                    }
                }

            }
        }
    }
}

