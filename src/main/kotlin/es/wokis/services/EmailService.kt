package es.wokis.services

import es.wokis.data.bo.recover.RecoverBO
import es.wokis.data.bo.user.UserBO
import es.wokis.data.bo.verification.VerificationBO
import es.wokis.data.constants.ServerConstants.LANG_ES
import es.wokis.data.repository.verify.VerifyRepository
import es.wokis.plugins.config
import es.wokis.utils.HashGenerator
import java.util.*
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailService(
    private val verifyRepository: VerifyRepository
) {
    private val fromEmail = config.getString("mail.user")
    private val fromPassword = config.getString("mail.pass")

    suspend fun sendVerifyEmail(user: UserBO): VerificationBO? {
        val emailHtml = this::class.java.getResource("/emails/${user.lang}/email-verify.html")
            ?: this::class.java.getResource("/emails/en/email-verify.html") ?: throw IllegalAccessException()

        val properties: Properties = getProperties()
        val hash = HashGenerator.generateHash(20)
        val body = emailHtml.readText().replace("%%TOKEN", hash)

        val session = Session.getDefaultInstance(properties)
        val message = MimeMessage(session)
        val emailSubject = when (user.lang) {
            LANG_ES -> VERIFY_EMAIL_ES
            else -> VERIFY_EMAIL_EN
        }

        if (sendMessage(emailSubject, message, user, body, session)) return null

        return VerificationBO(
            email = user.email,
            verificationToken = hash
        ).also {
            verifyRepository.addVerification(it)
        }
    }

    suspend fun sendRecoverPass(user: UserBO): RecoverBO? {
        val emailHtml = this::class.java.getResource("/emails/${user.lang}/recover-pass.html")
            ?: this::class.java.getResource("/emails/en/recover-pass.html") ?: throw IllegalAccessException()

        val properties: Properties = getProperties()
        val hash = HashGenerator.generateHash(12)
        val body = emailHtml.readText().replace("%%recover", hash)

        val session = Session.getDefaultInstance(properties)
        val message = MimeMessage(session)
        val emailSubject = when (user.lang) {
            LANG_ES -> RECOVER_PASS_ES
            else -> RECOVER_PASS_EN
        }

        if (sendMessage(emailSubject, message, user, body, session)) return null

        return RecoverBO(
            email = user.email,
            verificationToken = hash
        )
    }

    private fun EmailService.sendMessage(
        emailSubject: String,
        message: MimeMessage,
        user: UserBO,
        body: String,
        session: Session
    ): Boolean {
        try {
            with(message) {
                setFrom(InternetAddress(fromEmail))
                addRecipients(Message.RecipientType.TO, user.email)
                subject = emailSubject
                setContent(body, "text/html")
            }

            with(session.getTransport("smtp")) {
                connect("ssl0.ovh.net", fromEmail, fromPassword)
                sendMessage(message, message.allRecipients)
                close()
            }

        } catch (e: MessagingException) {
            println(e.message)
            return true
        }
        return false
    }

    private fun getProperties() = System.getProperties().apply {
        put("mail.smtp.host", "ssl0.ovh.net")
        put("mail.smtp.user", fromEmail)
        put("mail.smtp.clave", fromPassword)
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.ssl.trust", "ssl0.ovh.net");
        put("mail.smtp.port", 587)
    }


    companion object {
        private const val VERIFY_EMAIL_EN = "ECIBot - Verify Email"
        private const val VERIFY_EMAIL_ES = "ECIBot - Verificar Email"
        private const val RECOVER_PASS_EN = "ECIBot - Recover password"
        private const val RECOVER_PASS_ES = "ECIBot - Recuperar contraseña"
    }
}
