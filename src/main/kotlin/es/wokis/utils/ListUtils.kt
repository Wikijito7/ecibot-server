package es.wokis.utils

fun <T> List<T>.takeAtMost(count: Int) = if (size < count) this else this.subList(0, count)