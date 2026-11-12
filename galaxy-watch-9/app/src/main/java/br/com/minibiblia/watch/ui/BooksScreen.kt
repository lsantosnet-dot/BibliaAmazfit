package br.com.minibiblia.watch.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
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
import br.com.minibiblia.watch.data.Book
import br.com.minibiblia.watch.data.Position

/** Tela 1: Continuar lendo + 66 livros (Antigo e Novo Testamento) + ajuste de fonte. */
@Composable
fun BooksScreen(
    books: List<Book>,
    last: Position,
    lastBookName: String,
    onContinue: () -> Unit,
    onBook: (Book) -> Unit,
    onSettings: () -> Unit,
) {
    val state = rememberScalingLazyListState()
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
                    Text("MiniBíblia", color = Gold, fontFamily = FontFamily.Serif, style = MaterialTheme.typography.title2)
                }
            }
            item {
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onContinue,
                    colors = ChipDefaults.primaryChipColors(),
                    label = { Text("Continuar lendo") },
                    secondaryLabel = { Text("$lastBookName ${last.chapter}", fontFamily = FontFamily.Serif) },
                )
            }
            item { ListHeader { Text("ANTIGO TESTAMENTO", color = GoldDim, style = MaterialTheme.typography.caption1) } }
            items(books.filter { it.id < 40 }, key = { it.id }) { BookChip(it, onBook) }
            item { ListHeader { Text("NOVO TESTAMENTO", color = GoldDim, style = MaterialTheme.typography.caption1) } }
            items(books.filter { it.id >= 40 }, key = { it.id }) { BookChip(it, onBook) }
            item { ListHeader { Text("AJUSTES", color = GoldDim, style = MaterialTheme.typography.caption1) } }
            item {
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSettings,
                    colors = ChipDefaults.secondaryChipColors(),
                    label = { Text("Tamanho da fonte", color = Gold) },
                )
            }
        }
    }
}

@Composable
private fun BookChip(book: Book, onBook: (Book) -> Unit) {
    Chip(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onBook(book) },
        colors = ChipDefaults.secondaryChipColors(),
        label = { Text(book.name) },
        secondaryLabel = { Text("${book.chapters} cap.", color = Muted) },
    )
}
