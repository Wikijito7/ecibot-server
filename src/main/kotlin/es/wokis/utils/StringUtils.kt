package es.wokis.utils

import es.wokis.plugins.words
import java.io.File

fun String.normalizeUrl() = File(this).normalize().path.replace("\\", "/")

fun getRandomWords(amount: Int = 12): List<String> {
    val wordsDictionary = words.readLines()
    return mutableListOf<String>().apply {
        repeat(amount) {
            add(wordsDictionary.random())
        }
    }.toList()
}