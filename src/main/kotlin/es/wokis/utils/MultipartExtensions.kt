package es.wokis.utils

import io.ktor.http.content.*

suspend fun MultiPartData.getAllParts(): List<PartData> = mutableListOf<PartData>().apply {
    forEachPart {
        add(it)
    }
}.toList()