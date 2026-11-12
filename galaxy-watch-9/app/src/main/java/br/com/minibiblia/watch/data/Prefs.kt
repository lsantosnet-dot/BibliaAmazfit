package br.com.minibiblia.watch.data

import android.content.Context

/** Guarda tamanho da fonte, "manter tela ligada" e o último capítulo lido. */
class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("minibiblia", Context.MODE_PRIVATE)

    var fontIndex: Int
        get() = sp.getInt("font", 2).coerceIn(0, FONT_SIZES_SP.lastIndex)
        set(v) = sp.edit().putInt("font", v).apply()

    var keepScreenOn: Boolean
        get() = sp.getBoolean("awake", true)
        set(v) = sp.edit().putBoolean("awake", v).apply()

    var last: Position
        get() = Position(sp.getInt("lastBook", 43), sp.getInt("lastChapter", 3))
        set(v) = sp.edit().putInt("lastBook", v.book).putInt("lastChapter", v.chapter).apply()

    companion object {
        val FONT_SIZES_SP = listOf(13f, 14.5f, 16f, 18f, 20f)
    }
}
