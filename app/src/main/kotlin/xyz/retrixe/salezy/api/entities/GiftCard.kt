package xyz.retrixe.salezy.api.entities

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.LongAsStringSerializer

@Serializable data class GiftCard(
    val id: String,
    @Serializable(with = LongAsStringSerializer::class) val currentBalance: Long,
    @Serializable(with = LongAsStringSerializer::class) val issuedBalance: Long,
    val issuedOn: Long,
    val expiresOn: Long,
    val invalid: Boolean
)
