package br.com.minibiblia.watch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import br.com.minibiblia.watch.data.Book

/** Tela 2: grade de capítulos, 3 por linha. */
@Composable
fun ChaptersScreen(book: Book, onChapter: (Int) -> Unit) {
    val state = rememberScalingLazyListState()
    val rows = (1..book.chapters).chunked(3)
    Scaffold(
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = state) },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = state,
            contentPadding = PaddingValues(top = 36.dp, bottom = 36.dp, start = 10.dp, end = 10.dp),
        ) {
            item {
                ListHeader {
                    Text(book.name, color = Gold, fontFamily = FontFamily.Serif, style = MaterialTheme.typography.title2)
                }
            }
            item { Text("Escolha o capítulo", color = Muted, style = MaterialTheme.typography.caption1) }
            items(rows) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                ) {
                    row.forEach { n ->
                        Button(
                            onClick = { onChapter(n) },
                            modifier = Modifier.size(46.dp),
                            colors = ButtonDefaults.secondaryButtonColors(),
                        ) { Text("$n") }
                    }
                    repeat(3 - row.size) { Spacer(Modifier.size(46.dp)) }
                }
            }
        }
    }
}
