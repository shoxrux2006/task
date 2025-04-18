package uz.rezanen.task.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import uz.rezanen.task.utils.CustomMaskTransformation

@Composable
fun IosSwitch(
    checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier
) {
    val switchWidth = 50.dp
    val switchHeight = 30.dp
    val thumbSize = 26.dp

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) switchWidth - thumbSize - 2.dp else 2.dp, label = "ThumbOffset"
    )

    val trackColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary else
            Color(0xFF999999), label = ""
    )

    Box(
        modifier = modifier
            .width(switchWidth)
            .height(switchHeight)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
fun AppButton(
    text: String,
    enabled: Boolean? = null,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp), shape = RoundedCornerShape(10.dp), onClick = onClick,
        enabled = enabled != null && enabled || enabled == null && !loading
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(42.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
            )
        }
    }
}


@Composable
fun FloatingTopToast(
    icon: ImageVector = Icons.Outlined.Info,
    color: Color = MaterialTheme.colorScheme.primary,
    message: String,
    show: Boolean,
    onClick: () -> Unit
) {


    AnimatedVisibility(
        visible = show,
        enter = fadeIn() + slideInVertically { -it },
        exit = fadeOut() + slideOutVertically { -it }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
                .zIndex(10f),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                color = color,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 6.dp,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                    Box(modifier = Modifier.padding(8.dp)) {
                        Icon(
                            icon,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PhoneNumberField(
    hint: String,
    errorMessage: String,
    onChange: (String) -> Unit
) {
    val isError = errorMessage.isNotEmpty()
    var textChange by rememberSaveable {
        mutableStateOf("+998")
    }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .background(color = MaterialTheme.colorScheme.surface),
        visualTransformation = CustomMaskTransformation("####(##)-###-##-##"),
        leadingIcon = {
            Icon(
                Icons.Filled.Phone,
                tint = if (isError) Color.Red else MaterialTheme.colorScheme.primary,
                contentDescription = "logo"
            )
        },

        singleLine = true,
        value = textChange,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = MaterialTheme.colorScheme.secondary,
            errorContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
        ),
        onValueChange = {
            if (it.length <= 13) {
                textChange = it
                onChange(textChange)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        label = {
            Text(
                text = hint,
                color = if (isError) Color.Red else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        maxLines = 1,
        isError = isError
    )
    if (isError) {
        Text(
            text = errorMessage, color = Color.Red, style = MaterialTheme.typography.bodySmall
        )
    }

}
