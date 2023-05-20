package es.wokis.data.exception

object PasswordConflictException : IllegalStateException()

object UsernameAlreadyExistsException : IllegalStateException()

object EmailAlreadyExistsException : IllegalStateException()

object VerificationNotFoundException : IllegalStateException()

object TotpNotFoundException : IllegalStateException()

object RecoverCodeNotFoundException : IllegalStateException()

object UserNotFoundException : IllegalStateException()