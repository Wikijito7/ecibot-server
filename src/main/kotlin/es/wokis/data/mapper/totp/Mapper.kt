package es.wokis.data.mapper.totp

import es.wokis.data.bo.user.TOTPResponseBO
import es.wokis.data.dto.totp.TOTPResponseDTO

fun TOTPResponseBO.toDTO() = TOTPResponseDTO(
    encodedSecret = encodedSecret,
    totpUrl = totpUrl,
    words = words
)