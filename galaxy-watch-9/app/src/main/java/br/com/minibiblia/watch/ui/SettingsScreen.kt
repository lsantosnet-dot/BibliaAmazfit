package br.com.minibiblia.watch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import br.com.minibiblia.watch.data.Prefs

/** Tela 4: tamanho da fonte com amostra ao vivo e opção de manter a tela ligada. */
@Composable
fun SettingsScreen(
    fontIndex: Int,
    keepScreenOn: Boolean,
    sample: String,
    onFontIndex: (Int) -> Unit,
    onKeepScreenOn: (Boolean) -> Unit,
    onDone: () -> Unit,
) {
    val state = rememberScalingLazyListState()
    val sizeSp = Prefs.FONT_SIZES_SP[fontIndex]
    Scaffold(timeText = { TimeText() }) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = state,
            contentPadding = PaddingValues(top = 36.dp, bottom = 36.dp, start = 12.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
        ) {
            item {
                ListHeader {
                    Text("Fonte", color = Gold, fontFamily = FontFamily.Serif, style = MaterialTheme.typography.title2)
                }
            }
            item {
                Text(
                    sample,
                    fontFamily = FontFamily.Serif,
                    fontSize = sizeSp.sp,
                    lineHeight = (sizeSp * 1.4f).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = { onFontIndex(fontIndex - 1) },
                        enabled = fontIndex > 0,
                        modifier = Modifier.size(48.dp),
                        colors = ButtonDefaults.secondaryButtonColors(),
                    ) { Text("A", fontFamily = FontFamily.Serif, fontSize = 16.sp) }
                    Text("${fontIndex + 1}/${Prefs.FONT_SIZES_SP.size}", color = Gold)
                    Button(
                        onClick = { onFontIndex(fontIndex + 1) },
                        enabled = fontIndex < Prefs.FONT_SIZES_SP.lastIndex,
                        modifier = Modifier.size(48.dp),
                        colors = ButtonDefaults.secondaryButtonColors(),
                    ) { Text("A", fontFamily = FontFamily.Serif, fontSize = 26.sp) }
                }
            }
            item {
                ToggleChip(
                    modifier = Modifier.fillMaxWidth(),
                    checked = keepScreenOn,
                    onCheckedChange = onKeepScreenOn,
                    label = { Text("Manter tela ligada") },
                    toggleControl = {
                        Icon(imageVector = ToggleChipDefaults.switchIcon(keepScreenOn), contentDescription = null)
                    },
                )
            }
            item {
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDone,
                    colors = ChipDefaults.primaryChipColors(),
                    label = { Text("Pronto", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                )
            }
        }
    }
}
