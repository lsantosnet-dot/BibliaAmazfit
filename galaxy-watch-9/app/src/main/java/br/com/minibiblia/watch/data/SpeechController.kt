package br.com.minibiblia.watch.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

/**
 * Leitura em voz alta do capítulo com o motor de voz (TextToSpeech) do relógio.
 * Fala o título e depois um versículo por vez, informando qual versículo está sendo lido
 * para a tela destacá-lo e rolar até ele.
 */
class SpeechController(context: Context) {

    var ready by mutableStateOf(false); private set
    /** false quando o relógio não tem voz em português instalada. */
    var available by mutableStateOf(true); private set
    var playing by mutableStateOf(false); private set
    /** Índice (base 0) do versículo em leitura, ou -1. */
    var currentVerse by mutableIntStateOf(-1); private set
    /** Versículo de onde "Continuar" retoma depois de uma pausa. */
    var resumeFrom by mutableIntStateOf(0); private set

    private val main = Handler(Looper.getMainLooper())
    private var lastIndex = -1
    private var tts: TextToSpeech? = null

    private val listener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            main.post {
                if (playing && utteranceId != null && utteranceId.startsWith("v")) {
                    currentVerse = utteranceId.substring(1).toIntOrNull() ?: currentVerse
                }
            }
        }

        override fun onDone(utteranceId: String?) {
            main.post { if (playing && utteranceId == "v$lastIndex") finish() }
        }

        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String?) {
            main.post { playing = false }
        }

        override fun onError(utteranceId: String?, errorCode: Int) {
            main.post { playing = false }
        }
    }

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("pt", "BR"))
                available = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
                tts?.setOnUtteranceProgressListener(listener)
                ready = true
            } else {
                available = false
            }
        }
    }

    fun play(title: String, verses: List<String>, from: Int = 0) {
        val t = tts ?: return
        if (!ready || !available || verses.isEmpty()) return
        t.stop()
        val start = from.coerceIn(0, verses.lastIndex)
        lastIndex = verses.lastIndex
        playing = true
        currentVerse = start
        resumeFrom = start
        if (start == 0) t.speak(title, TextToSpeech.QUEUE_ADD, null, "title")
        for (i in start..verses.lastIndex) t.speak(verses[i], TextToSpeech.QUEUE_ADD, null, "v$i")
    }

    fun pause() {
        tts?.stop()
        playing = false
        resumeFrom = currentVerse.coerceAtLeast(0)
    }

    fun stop() {
        tts?.stop()
        finish()
    }

    private fun finish() {
        playing = false
        currentVerse = -1
        resumeFrom = 0
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
