package br.com.minibiblia.watch.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import br.com.minibiblia.watch.data.BibleRepository
import br.com.minibiblia.watch.data.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Tela 3: versículos. A luneta giratória rola a lista (comportamento padrão do
 * ScalingLazyColumn) e há botões de capítulo anterior/próximo ao fim.
 */
@Composable
fun ReaderScreen(
    repo: BibleRepository,
    position: Position,
    fontSizeSp: Float,
    onNavigate: (Position) -> Unit,
) {
    val book = repo.book(position.book)
    val verses by produceState<List<String>?>(null, position) {
        value = null
        value = withContext(Dispatchers.IO) { repo.verses(position.book, position.chapter) }
    }
    val prev = repo.previous(position)
    val next = repo.next(position)

    key(position) {
        val state = rememberScalingLazyListState()
        Scaffold(
            timeText = { TimeText() },
            positionIndicator = { PositionIndicator(scalingLazyListState = state) },
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        ) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = state,
                contentPadding = PaddingValues(top = 36.dp, bottom = 40.dp, start = 14.dp, end = 14.dp),
            ) {
                item {
                    ListHeader {
                        Text(
                            "${book.name} ${position.chapter}",
                            color = Gold,
                            fontFamily = FontFamily.Serif,
                            style = MaterialTheme.typography.title2,
                        )
                    }
                }
                verses?.let { list ->
                    itemsIndexed(list) { i, text ->
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = Gold, fontWeight = FontWeight.SemiBold, fontSize = (fontSizeSp * 0.7f).sp)) {
                                    append("${i + 1} ")
                                }
                                append(text)
                            },
                            fontFamily = FontFamily.Serif,
                            fontSize = fontSizeSp.sp,
                            lineHeight = (fontSizeSp * 1.4f).sp,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                        )
                    }
                    if (next != null) item {
                        Chip(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigate(next) },
                            colors = ChipDefaults.primaryChipColors(),
                            label = { Text("Próximo: ${repo.book(next.book).name} ${next.chapter}") },
                        )
                    }
                    if (prev != null) item {
                        Chip(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigate(prev) },
                            colors = ChipDefaults.secondaryChipColors(),
                            label = { Text("Anterior: ${repo.book(prev.book).name} ${prev.chapter}") },
                        )
                    }
                }
            }
        }
    }
}
