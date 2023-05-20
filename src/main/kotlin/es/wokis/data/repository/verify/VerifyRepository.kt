package es.wokis.data.repository.verify

import es.wokis.data.bo.response.AcknowledgeBO
import es.wokis.data.bo.verification.VerificationBO
import es.wokis.data.datasource.verify.VerifyLocalDataSource
import es.wokis.data.exception.VerificationNotFoundException
import es.wokis.data.repository.user.UserRepository

interface VerifyRepository {
    suspend fun addVerification(verification: VerificationBO)
    suspend fun verify(token: String): AcknowledgeBO
}

class VerifyRepositoryImpl(
    private val localDataSource: VerifyLocalDataSource,
    private val userRepository: UserRepository
) : VerifyRepository {

    override suspend fun addVerification(verification: VerificationBO) {
        localDataSource.addVerification(verification)
    }

    override suspend fun verify(token: String): AcknowledgeBO {
        val verification = localDataSource.getVerificationByToken(token)
        verification?.let {
            val user = userRepository.getUserByEmail(it.email)
            user?.let {
                verification.id?.let { id ->
                    localDataSource.removeVerification(id)
                }

                return userRepository.updateUser(user.copy(emailVerified = true))
            }
        }
        throw VerificationNotFoundException
    }

}