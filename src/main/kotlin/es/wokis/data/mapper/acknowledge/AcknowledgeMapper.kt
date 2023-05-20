package es.wokis.data.mapper.acknowledge

import es.wokis.data.bo.response.AcknowledgeBO
import es.wokis.data.dto.response.AcknowledgeDTO

fun AcknowledgeBO.toDTO() = AcknowledgeDTO(acknowledge)