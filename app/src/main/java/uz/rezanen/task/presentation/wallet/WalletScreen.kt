package uz.rezanen.task.presentation.wallet

import android.content.Context
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.SecureFlagPolicy
import cafe.adriel.voyager.androidx.AndroidScreen
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay
import org.koin.androidx.compose.getViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.rezanen.task.R
import uz.rezanen.task.domain.data.CardItem
import uz.rezanen.task.localization.LocalStrings
import uz.rezanen.task.presentation.AppButton
import uz.rezanen.task.presentation.FloatingTopToast
import uz.rezanen.task.presentation.IosSwitch
import uz.rezanen.task.presentation.wallet.vm.WalletIntent
import uz.rezanen.task.presentation.wallet.vm.WalletSideEffect
import uz.rezanen.task.presentation.wallet.vm.WalletUIState
import uz.rezanen.task.presentation.wallet.vm.WalletVM
import uz.rezanen.task.presentation.wallet.vm.WalletVMImpl

class WalletScreen : AndroidScreen() {
    @Composable
    override fun Content() {
        val viewModel: WalletVM = getViewModel<WalletVMImpl>()
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
                is WalletSideEffect.Message -> {
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
                show = showToast,){

            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreenContent(uiState: WalletUIState, onEventDispatcher: (WalletIntent) -> Unit) {
        var showSheet by remember { mutableStateOf(false) }
        var balance by remember {
            mutableStateOf("")
        }
        var balanceLoading by remember {
            mutableStateOf(false)
        }
        var cardsList: List<CardItem> by remember {
            mutableStateOf(listOf())
        }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false,
            confirmValueChange = { true })
        val focusRequester = remember { FocusRequester() }
        val coroutineScope = rememberCoroutineScope()
        var promoCodeText by remember {
            mutableStateOf("")
        }
        when (uiState) {
            is WalletUIState.Error -> {

            }

            is WalletUIState.Loading -> {
                balanceLoading = uiState.balanceLoading
            }

            WalletUIState.ShowPromoCodeBottomSheet -> {
                showSheet = true
            }

            is WalletUIState.Success -> {
                if (uiState.balance != null) {
                    balance = uiState.balance
                }
                if (uiState.cards != null) {
                    cardsList = uiState.cards
                }
            }

            WalletUIState.Initial -> {

            }
        }
        if (showSheet) {
            ModalBottomSheet(
                containerColor = MaterialTheme.colorScheme.background,
                dragHandle = null,
                properties = ModalBottomSheetProperties(
                    securePolicy = SecureFlagPolicy.SecureOff,
                    isFocusable = true,
                    shouldDismissOnBackPress = true
                ),
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .imePadding()
                        .padding(top = 10.dp)
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .size(48.dp)
                                .shadow(5.dp, shape = CircleShape)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary),

                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "")
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = LocalStrings.current.addPromoCode,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )

                    }
                    TextField(modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        ),
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                        ),
                        value = promoCodeText,
                        onValueChange = {
                            promoCodeText = it
                        })

                    Spacer(modifier = Modifier.height(10.dp))
                    AppButton(text = LocalStrings.current.addPromoCode) {}
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(
                    horizontal = 15.dp
                )
        ) {
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = LocalStrings.current.wallet,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(25.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .fillMaxWidth()
                    .aspectRatio(2.2f)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
                            )
                        )
                    )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = LocalStrings.current.balance,
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.secondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (balanceLoading) {
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmer()
                                .background(Color.Gray)
                        )
                    } else {
                        Text(
                            text = balance,
                            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                }
            }
            LazyColumn {
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(10.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = "")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = LocalStrings.current.identification,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_up_right),
                            contentDescription = ""
                        )

                    }
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    CardItem(
                        R.drawable.promokod,
                        hasRowIcon = true,
                        text = LocalStrings.current.addPromoCode
                    ) {

                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    CardItem(
                        R.drawable.cash, hasRowIcon = false, text = LocalStrings.current.cash
                    ) {

                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                items(cardsList) { item ->
                    CardItem(
                        R.drawable.card, hasRowIcon = false, text = item.number ?: ""
                    ) {

                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    CardItem(
                        R.drawable.add_card,
                        hasRowIcon = true,
                        text = LocalStrings.current.addNewCard
                    ) {

                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }


        }


    }


    @Composable
    private fun CardItem(
        @DrawableRes id: Int, text: String, hasRowIcon: Boolean, onClick: () -> Unit
    ) {
        var switcher by remember {
            mutableStateOf(false)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = id),
                contentDescription = "",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text, style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            if (hasRowIcon) {
                Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = "")
            } else {
                IosSwitch(checked = switcher, onCheckedChange = {
                    switcher = !switcher
                })
            }
        }
    }


}