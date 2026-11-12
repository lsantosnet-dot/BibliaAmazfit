package br.com.minibiblia.watch.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Book(val id: Int, val name: String, val chapters: Int)

data class Position(val book: Int, val chapter: Int)

/**
 * Lê os mesmos JSON do app Amazfit, agora de src/main/assets:
 *  - books.json          índice com id, nome e número de capítulos
 *  - bible/{id}.json     { "book": "...", "chapters": [ [versículos], ... ] }
 * Carrega só o livro que está sendo lido. No Wear OS não precisa de decodificador
 * UTF-8 manual: o org.json já lê UTF-8 corretamente.
 */
class BibleRepository(private val context: Context) {

    private val books: List<Book> by lazy {
        val text = context.assets.open("books.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        val arr = JSONArray(text)
        List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            Book(o.getInt("id"), o.getString("name"), o.getInt("chapters"))
        }
    }

    fun books(): List<Book> = books

    fun book(id: Int): Book = books.first { it.id == id }

    fun verses(bookId: Int, chapter: Int): List<String> {
        val text = context.assets.open("bible/$bookId.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        val chapters = JSONObject(text).getJSONArray("chapters")
        if (chapter < 1 || chapter > chapters.length()) return emptyList()
        val arr = chapters.getJSONArray(chapter - 1)
        return List(arr.length()) { arr.getString(it) }
    }

    fun next(p: Position): Position? = when {
        p.chapter < book(p.book).chapters -> Position(p.book, p.chapter + 1)
        p.book < books.last().id -> Position(p.book + 1, 1)
        else -> null
    }

    fun previous(p: Position): Position? = when {
        p.chapter > 1 -> Position(p.book, p.chapter - 1)
        p.book > books.first().id -> Position(p.book - 1, book(p.book - 1).chapters)
        else -> null
    }
}
