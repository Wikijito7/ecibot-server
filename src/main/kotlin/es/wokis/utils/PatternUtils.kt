package es.wokis.utils

import java.util.regex.Pattern

fun String.isEmail(): Boolean = Pattern.compile("^[\\w-.]+@([\\w-]+.)+[\\w-]{2,4}\$").matcher(this).matches()