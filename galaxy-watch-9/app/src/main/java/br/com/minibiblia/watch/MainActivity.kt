package br.com.minibiblia.watch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import br.com.minibiblia.watch.data.BibleRepository
import br.com.minibiblia.watch.data.Position
import br.com.minibiblia.watch.data.Prefs
import br.com.minibiblia.watch.ui.BooksScreen
import br.com.minibiblia.watch.ui.ChaptersScreen
import br.com.minibiblia.watch.ui.MiniBibliaTheme
import br.com.minibiblia.watch.ui.ReaderScreen
import br.com.minibiblia.watch.ui.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MiniBibliaApp() }
    }
}

private const val READER = "reader/{book}/{chapter}"

@Composable
fun MiniBibliaApp() {
    val context = LocalContext.current.applicationContext
    val repo = remember { BibleRepository(context) }
    val prefs = remember { Prefs(context) }
    val books = remember { repo.books() }

    var fontIndex by remember { mutableIntStateOf(prefs.fontIndex) }
    var keepOn by remember { mutableStateOf(prefs.keepScreenOn) }
    var last by remember { mutableStateOf(prefs.last) }

    val view = LocalView.current
    SideEffect { view.keepScreenOn = keepOn }

    val nav = rememberSwipeDismissableNavController()

    fun openReader(p: Position) {
        last = p
        prefs.last = p
        nav.navigate("reader/${p.book}/${p.chapter}")
    }

    MiniBibliaTheme {
        // Deslizar para a direita (ou o botão Voltar do relógio) volta uma tela.
        SwipeDismissableNavHost(navController = nav, startDestination = "books") {
            composable("books") {
                BooksScreen(
                    books = books,
                    last = last,
                    lastBookName = repo.book(last.book).name,
                    onContinue = { openReader(last) },
                    onBook = { b ->
                        // Livros de um capítulo só (Obadias, Filemom, 2 e 3 João, Judas) abrem direto no texto.
                        if (b.chapters == 1) openReader(Position(b.id, 1)) else nav.navigate("chapters/${b.id}")
                    },
                    onSettings = { nav.navigate("settings") },
                )
            }
            composable(
                "chapters/{book}",
                arguments = listOf(navArgument("book") { type = NavType.IntType }),
            ) { entry ->
                val book = repo.book(entry.arguments?.getInt("book") ?: 1)
                ChaptersScreen(book = book, onChapter = { openReader(Position(book.id, it)) })
            }
            composable(
                READER,
                arguments = listOf(
                    navArgument("book") { type = NavType.IntType },
                    navArgument("chapter") { type = NavType.IntType },
                ),
            ) { entry ->
                val pos = Position(entry.arguments?.getInt("book") ?: 1, entry.arguments?.getInt("chapter") ?: 1)
                ReaderScreen(
                    repo = repo,
                    position = pos,
                    fontSizeSp = Prefs.FONT_SIZES_SP[fontIndex],
                    onNavigate = { p ->
                        last = p
                        prefs.last = p
                        // Troca de capítulo sem empilhar leituras: Voltar continua indo para a lista.
                        nav.navigate("reader/${p.book}/${p.chapter}") {
                            popUpTo(READER) { inclusive = true }
                        }
                    },
                )
            }
            composable("settings") {
                SettingsScreen(
                    fontIndex = fontIndex,
                    keepScreenOn = keepOn,
                    sample = repo.verses(43, 3).getOrElse(15) { "" }.take(70) + "…",
                    onFontIndex = { fontIndex = it; prefs.fontIndex = it },
                    onKeepScreenOn = { keepOn = it; prefs.keepScreenOn = it },
                    onDone = { nav.popBackStack() },
                )
            }
        }
    }
}
