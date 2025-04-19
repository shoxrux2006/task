package uz.rezanen.task.presentation.wallet

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.rezanen.task.MainActivity
import uz.rezanen.task.R
import uz.rezanen.task.domain.data.CardItem
import uz.rezanen.task.localization.LocalStrings
import uz.rezanen.task.localization.Locales
import uz.rezanen.task.presentation.AppButton
import uz.rezanen.task.presentation.FloatingTopToast
import uz.rezanen.task.presentation.IosSwitch
import uz.rezanen.task.presentation.wallet.vm.WalletIntent
import uz.rezanen.task.presentation.wallet.vm.WalletSideEffect
import uz.rezanen.task.presentation.wallet.vm.WalletUIState
import uz.rezanen.task.presentation.wallet.vm.WalletVM
import uz.rezanen.task.presentation.wallet.vm.WalletVMImpl
import uz.rezanen.task.utils.Const

class WalletScreen(var addedCard: Boolean) : AndroidScreen() {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: WalletVM = getViewModel<WalletVMImpl>()
        val uiState = viewModel.collectAsState().value

       LaunchedEffect(addedCard) {
           if(addedCard){
               viewModel.onEventDispatcher(WalletIntent.Refresh)
               addedCard=false
           }
       }
        var sideEffectMessage by remember {
            mutableStateOf("")
        }
        var sideEffectColor: Color? by remember {
            mutableStateOf(null)
        }
        var sideEffectIcon: ImageVector? by remember {
            mutableStateOf(null)
        }
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false,
            confirmValueChange = { true })
        val focusRequester = remember { FocusRequester() }
        val coroutineScope = rememberCoroutineScope()
        var promoCodeText by remember {
            mutableStateOf("")
        }
        var promoCodeError by remember {
            mutableStateOf("")
        }
        var showSheet by remember { mutableStateOf(false) }
        var showToast by remember { mutableStateOf(false) }
        var context: Context? by remember {
            mutableStateOf(null)
        }
        context = LocalContext.current
        viewModel.collectSideEffect {
            when (it) {
                is WalletSideEffect.Message -> {
                    sideEffectIcon = it.icon
                    sideEffectColor = it.color
                    sideEffectMessage = it.message
                    showToast = true
                    delay(2000)
                    showToast = false
                }

                is WalletSideEffect.ShowPromoCodeBottomSheet -> {
                    coroutineScope.launch {
                        promoCodeError = it.errorMessage ?: ""
                        showSheet = it.showSheet
                        if (showSheet) {
                            delay(500)
                            focusRequester.requestFocus()
                        }
                    }
                }
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
                                .clickable { showSheet = false }
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
                        isError = promoCodeError.isNotEmpty(),
                        label = {
                            Text(
                                text = promoCodeError,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                        ),
                        value = promoCodeText,
                        onValueChange = {
                            promoCodeText = it
                        })

                    Spacer(modifier = Modifier.height(10.dp))
                    AppButton(text = LocalStrings.current.addPromoCode) {
                        viewModel::onEventDispatcher.invoke(WalletIntent.CheckPromoCode(code = promoCodeText))
                    }
                }
            }
        }
        Box {
            MainScreenContent(uiState, viewModel::onEventDispatcher)
            FloatingTopToast(
                icon = sideEffectIcon ?: Icons.Outlined.Info,
                color = sideEffectColor ?: MaterialTheme.colorScheme.primary,
                message = sideEffectMessage,
                show = showToast,
            ) {

            }
        }
    }

    @Composable
    fun MainScreenContent(uiState: WalletUIState, onEventDispatcher: (WalletIntent) -> Unit) {

        var balance by rememberSaveable {
            mutableStateOf("")
        }
        var activeMethod by rememberSaveable {
            mutableIntStateOf(-1)
        }
        var balanceLoading by remember {
            mutableStateOf(false)
        }
        var cardLoading by remember {
            mutableStateOf(false)
        }
        var cardsList: List<CardItem> by rememberSaveable {
            mutableStateOf(listOf())
        }

        when (uiState) {
            is WalletUIState.Error -> {

            }

            is WalletUIState.Loading -> {
                cardLoading = uiState.cardsLoading == true
                balanceLoading = uiState.balanceLoading == true
            }

            is WalletUIState.Success -> {
                if (uiState.balance != null) {
                    if (uiState.activeMethod != null) {
                        activeMethod = uiState.activeMethod
                    }
                    balanceLoading = false
                    balance = uiState.balance
                }
                if (uiState.cards != null) {
                    cardLoading = false
                    cardsList = uiState.cards
                }
            }

            WalletUIState.Initial -> {

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

            val activity = LocalContext.current as? MainActivity
            var checked by remember {
                mutableStateOf(false)
            }
            //demo
            Row {
                IosSwitch(checked = checked, onCheckedChange = {
                    if (activity != null) {
                        if (checked) {
                            activity.lyricist.languageTag = Locales.UZ
                        } else {
                            activity.lyricist.languageTag = Locales.EN
                        }
                        checked = !checked
                    }
                })
                Text(text = "Language change")
            }


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
                                .width(120.dp)
                                .height(32.dp)
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
            if (cardLoading) {
                Column {
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .shimmer()
                            .background(Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .shimmer()
                            .background(Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .shimmer()
                            .background(Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .shimmer()
                            .background(Color.Gray)
                    )
                }
            } else {
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
                            id = R.drawable.promokod,
                            hasRowIcon = true,
                            text = LocalStrings.current.addPromoCode
                        ) {
                            onEventDispatcher.invoke(WalletIntent.AddPromoCode)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    item {
                        CardItem(
                            switcher = activeMethod == 1,
                            id = R.drawable.cash,
                            hasRowIcon = false,
                            text = LocalStrings.current.cash
                        ) {
                            if (1 != activeMethod) {
                                onEventDispatcher.invoke(
                                    WalletIntent.ChangePaymentMethod(
                                        activeMethod = Const.cash, cardId = 1
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    items(cardsList) { item ->
                        CardItem(
                            switcher = item.id == activeMethod,
                            id = R.drawable.card,
                            hasRowIcon = false,
                            text = maskText(item.number ?: "")
                        ) {
                            if (item.id != activeMethod) {
                                onEventDispatcher.invoke(
                                    WalletIntent.ChangePaymentMethod(
                                        activeMethod = Const.card, cardId = item.id ?: 0
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    item {
                        CardItem(
                            R.drawable.add_card,
                            hasRowIcon = true,
                            text = LocalStrings.current.addNewCard
                        ) {
                            onEventDispatcher.invoke(WalletIntent.AddCard)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

        }


    }


    private fun maskText(input: String): String {
        if (input.length <= 4) return input
        val visiblePart = input.takeLast(4)
        val maskedPart = "*".repeat(input.length - 4)
        return maskedPart + visiblePart
    }


    @Composable
    private fun CardItem(
        @DrawableRes id: Int,
        text: String,
        hasRowIcon: Boolean,
        switcher: Boolean? = null,
        onClick: (Boolean?) -> Unit
    ) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                if (hasRowIcon) {
                    onClick(null)
                }
            }
            .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
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
                IosSwitch(checked = switcher ?: false, onCheckedChange = {
                    onClick(switcher)
                })
            }
        }
    }


}