package xyz.retrixe.salezy.api.entities

import kotlinx.serialization.Serializable

@Serializable data class GiftCard(
    val id: String,
    val balance: Double,
)
