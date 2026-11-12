package br.com.minibiblia.watch.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

val Gold = Color(0xFFFFD700)
val GoldDim = Color(0xFFB89B00)
val Muted = Color(0xFF8D8A84)
val Surface = Color(0xFF17181B)

private val colors = Colors(
    primary = Gold,
    primaryVariant = GoldDim,
    secondary = Gold,
    secondaryVariant = GoldDim,
    background = Color.Black,
    surface = Surface,
    error = Color(0xFFCF6679),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Muted,
    onError = Color.Black,
)

/** Preto absoluto (AMOLED) com dourado, igual ao app Amazfit. */
@Composable
fun MiniBibliaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = colors, content = content)
}
