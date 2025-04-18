package uz.rezanen.task.localization

import androidx.compose.runtime.staticCompositionLocalOf

val strings = mapOf(
    Locales.EN to EnStrings,
    Locales.UZ to UzStrings,
)
val LocalStrings = staticCompositionLocalOf { EnStrings }