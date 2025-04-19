package uz.rezanen.task.presentation.addCard

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.androidx.AndroidScreen
import kotlinx.coroutines.delay
import org.koin.androidx.compose.getViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.rezanen.task.localization.LocalStrings
import uz.rezanen.task.presentation.AppButton
import uz.rezanen.task.presentation.FloatingTopToast
import uz.rezanen.task.presentation.addCard.vm.AddCardIntent
import uz.rezanen.task.presentation.addCard.vm.AddCardSideEffect
import uz.rezanen.task.presentation.addCard.vm.AddCardUIState
import uz.rezanen.task.presentation.addCard.vm.AddCardVM
import uz.rezanen.task.presentation.addCard.vm.AddCardVMImpl
import uz.rezanen.task.utils.CustomMaskTransformation

class AddCardScreen : AndroidScreen() {
    @Composable
    override fun Content() {
        val viewModel: AddCardVM = getViewModel<AddCardVMImpl>()
        val uiState = viewModel.collectAsState().value
        val coroutineScope = rememberCoroutineScope()
        val snackBarHost = remember { SnackbarHostState() }
        var sideEffectMessage by remember {
            mutableStateOf("")
        }
        var sideEffectColor: Color? by remember {
            mutableStateOf(null)
        }
        var sideEffectIcon: ImageVector? by remember {
            mutableStateOf(null)
        }
        var showToast by remember { mutableStateOf(false) }
        var context: Context? by remember {
            mutableStateOf(null)
        }
        context = LocalContext.current
        viewModel.collectSideEffect {
            when (it) {
                is AddCardSideEffect.Message -> {
                    sideEffectIcon = it.icon
                    sideEffectColor = it.color
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
                icon = sideEffectIcon ?: Icons.Outlined.Info,
                color = sideEffectColor ?: MaterialTheme.colorScheme.primary,
                message = sideEffectMessage,
                show = showToast,
            ) {

            }
        }
    }

    @Composable
    fun MainScreenContent(uiState: AddCardUIState, onEventDispatcher: (AddCardIntent) -> Unit) {

        var saveButtonEnabled by rememberSaveable {
            mutableStateOf(false)
        }
        var saveButtonLoading by rememberSaveable {
            mutableStateOf(false)
        }

        when (uiState) {
            is AddCardUIState.Error -> {}
            is AddCardUIState.Loading -> {
                saveButtonLoading = uiState.isLoading
                uiState.enableSaveButton?.let {
                    saveButtonEnabled = it
                }
            }

            AddCardUIState.Success -> {}
            is AddCardUIState.DataFilled -> {
                saveButtonEnabled = uiState.enableSaveButton
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
                        .clickable {
                            onEventDispatcher.invoke(AddCardIntent.BackButton)
                        }
                        .background(MaterialTheme.colorScheme.secondary),

                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "")
                }
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = LocalStrings.current.addCard,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

            }

            Spacer(modifier = Modifier.height(25.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .fillMaxWidth()
                    .aspectRatio(1.77f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surface.copy(0.7f)
                            )
                        )
                    )
            ) {
                Column(modifier = Modifier.padding(horizontal = 26.dp, vertical = 30.dp)) {
                    Spacer(modifier = Modifier.weight(1f))
                    MiniTextField(
                        actualLength = 16,
                        placeHolder = "0000 0000 0000 0000",
                        transformationText = "####-####-####-####"
                    ) {
                        onEventDispatcher.invoke(AddCardIntent.EditingCardNumber(it))
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    MiniTextField(
                        actualLength = 4,
                        placeHolder = "00/00",
                        transformationText = "##/##"
                    ) {
                        onEventDispatcher.invoke(AddCardIntent.EditingExpireDate(it))
                    }


                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            AppButton(
                enabled = saveButtonEnabled && !saveButtonLoading,
                loading = saveButtonLoading,
                text = LocalStrings.current.save
            ) {
                onEventDispatcher.invoke(AddCardIntent.SubmitButton)
            }
        }

    }

    @Composable
    fun MiniTextField(
        placeHolder: String,
        transformationText: String,
        actualLength: Int,
        onValueChange: (String) -> Unit,
    ) {
        var value by rememberSaveable {
            mutableStateOf("")
        }
        var transformedText by rememberSaveable {
            mutableStateOf("")
        }
        val padding = 16
        val fontSizeSp = 16
        val widthDp = (placeHolder.length * fontSizeSp * 0.6f) + padding * 2

        val transformation = CustomMaskTransformation(transformationText)


        Box(
            modifier = Modifier
                .width(widthDp.dp)
                .height(36.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(0.6f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(start = padding.dp), // optional
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = {
                    if (it.length <= actualLength) {
                        value = it
                        transformedText = transformation.filter(AnnotatedString(value)).text.text
                        onValueChange(transformedText)

                    }

                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = transformation,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                modifier = Modifier.fillMaxWidth()
            )
            if (value.isEmpty()) {
                Text(
                    text = placeHolder,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                )
            }
        }
    }


}