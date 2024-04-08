package xyz.retrixe.salezy.api.entities

import kotlinx.serialization.Serializable

@Serializable data class InventoryItem(
    val name: String,
    val imageUrl: String?,
    val referenceNumber: Long,
    val price: Long,
    val quantity: Int
)
