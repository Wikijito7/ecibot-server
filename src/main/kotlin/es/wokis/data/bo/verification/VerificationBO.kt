package es.wokis.data.bo.verification

import java.util.*

data class VerificationBO(
    val id: String? = null,
    val email: String,
    val verificationToken: String,
    val timeStamp: Date = Date()
)