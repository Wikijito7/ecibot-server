package es.wokis.data.bo.recover

import java.util.*

data class RecoverBO(
    val id: String? = null,
    val email: String,
    val verificationToken: String,
    val timeStamp: Date = Date()
)