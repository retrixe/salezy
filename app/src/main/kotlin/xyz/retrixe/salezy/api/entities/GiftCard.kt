package xyz.retrixe.salezy.api.entities

import kotlinx.serialization.Serializable

@Serializable data class GiftCard(
    val id: String,
    val currentBalance: Long,
    val issuedBalance: Long,
    val issuedOn: Long,
    val expiresOn: Long
)
