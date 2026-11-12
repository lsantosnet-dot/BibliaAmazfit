package br.com.minibiblia.watch.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import br.com.minibiblia.watch.data.SpeechController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val VerseColor = Color(0xFFF1EEE7)
private val VerseDim = Color(0xFF8F8C85)

/**
 * Tela 3: versículos. A luneta giratória rola a lista, o botão "Ouvir capítulo" lê o
 * capítulo em voz alta (destacando o versículo atual) e há botões de capítulo anterior/próximo ao fim.
 */
@Composable
fun ReaderScreen(
    repo: BibleRepository,
    speech: SpeechController,
    position: Position,
    fontSizeSp: Float,
    onNavigate: (Position) -> Unit,
) {
    val book = repo.book(position.book)
    val title = "${book.name} ${position.chapter}"
    val verses by produceState<List<String>?>(null, position) {
        value = null
        value = withContext(Dispatchers.IO) { repo.verses(position.book, position.chapter) }
    }
    val prev = repo.previous(position)
    val next = repo.next(position)

    // Ao trocar de capítulo ou sair da tela, a leitura para.
    DisposableEffect(position) { onDispose { speech.stop() } }

    key(position) {
        val state = rememberScalingLazyListState()

        // Acompanha o versículo lido: itens 0 = título, 1 = botão, versículos a partir do 2.
        LaunchedEffect(speech.currentVerse) {
            if (speech.playing && speech.currentVerse >= 0) state.animateScrollToItem(speech.currentVerse + 2)
        }

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
                        Text(title, color = Gold, fontFamily = FontFamily.Serif, style = MaterialTheme.typography.title2)
                    }
                }
                verses?.let { list ->
                    item {
                        val active = speech.playing || speech.resumeFrom > 0
                        Chip(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (speech.playing) speech.pause() else speech.play(title, list, speech.resumeFrom)
                            },
                            enabled = speech.ready && speech.available,
                            colors = ChipDefaults.primaryChipColors(),
                            icon = { PlayPauseIcon(playing = speech.playing, color = MaterialTheme.colors.onPrimary) },
                            label = {
                                Text(
                                    when {
                                        !speech.available -> "Sem voz em português"
                                        speech.playing -> "Pausar"
                                        speech.resumeFrom > 0 -> "Continuar"
                                        else -> "Ouvir capítulo"
                                    }
                                )
                            },
                            secondaryLabel = if (active) {
                                { Text("Versículo ${speech.currentVerse.coerceAtLeast(0) + 1} de ${list.size}") }
                            } else null,
                        )
                    }
                    itemsIndexed(list) { i, text ->
                        val color = when {
                            speech.currentVerse < 0 -> VerseColor
                            i == speech.currentVerse -> Color.White
                            else -> VerseDim
                        }
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = Gold, fontWeight = FontWeight.SemiBold, fontSize = (fontSizeSp * 0.7f).sp)) {
                                    append("${i + 1} ")
                                }
                                append(text)
                            },
                            color = color,
                            fontFamily = FontFamily.Serif,
                            fontSize = fontSizeSp.sp,
                            lineHeight = (fontSizeSp * 1.4f).sp,
                            fontWeight = if (i == speech.currentVerse) FontWeight.Medium else FontWeight.Normal,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                        )
                    }
                    if (next != null) item {
                        Chip(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigate(next) },
                            colors = ChipDefaults.secondaryChipColors(),
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

/** Ícone de play (triângulo) ou pausa (duas barras), desenhado sem depender de pacote de ícones. */
@Composable
private fun PlayPauseIcon(playing: Boolean, color: Color) {
    Canvas(Modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        if (playing) {
            drawRect(color, topLeft = Offset(w * 0.18f, h * 0.12f), size = Size(w * 0.24f, h * 0.76f))
            drawRect(color, topLeft = Offset(w * 0.58f, h * 0.12f), size = Size(w * 0.24f, h * 0.76f))
        } else {
            val p = Path().apply {
                moveTo(w * 0.22f, h * 0.10f)
                lineTo(w * 0.92f, h * 0.50f)
                lineTo(w * 0.22f, h * 0.90f)
                close()
            }
            drawPath(p, color)
        }
    }
}
