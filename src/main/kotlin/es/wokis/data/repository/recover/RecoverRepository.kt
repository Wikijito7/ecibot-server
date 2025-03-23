package es.wokis.data.repository.recover

import es.wokis.data.bo.recover.RecoverBO
import es.wokis.data.bo.response.AcknowledgeBO
import es.wokis.data.datasource.local.recover.RecoverLocalDataSource
import es.wokis.data.dto.user.auth.ChangePassRequestDTO
import es.wokis.data.exception.RecoverCodeNotFoundException
import es.wokis.data.exception.UserNotFoundException
import es.wokis.data.repository.user.UserRepository
import es.wokis.services.EmailService
import org.mindrot.jbcrypt.BCrypt

interface RecoverRepository {
    suspend fun changeUserPassword(changePassRequest: ChangePassRequestDTO): AcknowledgeBO
    suspend fun requestChangePass(email: String): AcknowledgeBO
}

class RecoverRepositoryImpl(
    private val localDataSource: RecoverLocalDataSource,
    private val userRepository: UserRepository,
    private val emailService: EmailService
) : RecoverRepository {

    override suspend fun changeUserPassword(changePassRequest: ChangePassRequestDTO): AcknowledgeBO {
        if (changePassRequest.recoverCode == null) {
            throw RecoverCodeNotFoundException
        }
        val recover = localDataSource.getRecoverByToken(changePassRequest.recoverCode)
        recover?.let { recover ->
            val user = userRepository.getUserByEmail(recover.email)
            return user?.let {
                recover.id?.let { recoverId ->
                    localDataSource.removeRecover(recoverId)
                }
                userRepository.updateUser(
                    user.copy(
                        password = BCrypt.hashpw(
                            changePassRequest.newPass,
                            BCrypt.gensalt()
                        ),
                        sessions = listOf()
                    )
                )
            } ?: throw UserNotFoundException
        }
        throw RecoverCodeNotFoundException
    }

    override suspend fun requestChangePass(email: String): AcknowledgeBO {
        val user = userRepository.getUserByEmail(email)
        user?.let {
            if (user.emailVerified) {
                throw IllegalStateException()
            }
            emailService.sendRecoverPass(user)?.also {
                return saveRequestChangePass(it)
            } ?: throw IllegalStateException()
        }
        throw UserNotFoundException
    }

    private suspend fun saveRequestChangePass(recoverRequest: RecoverBO): AcknowledgeBO {
        return AcknowledgeBO(localDataSource.saveRecoverRequest(recoverRequest))
    }

}