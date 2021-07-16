package br.com.erivelton.pix.shared.apiexterna.dto.resposta

import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
data class ChavePixDeletadaResposta(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)
