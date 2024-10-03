package xyz.retrixe.salezy.api.entities.ephemeral

import kotlinx.serialization.Serializable

@Serializable data class EphemeralInventoryItem(
    val name: String,
    val imageId: String?,
    val image: String?,
    val upc: Long,
    val sku: String,
    val costPrice: Long,
    val sellingPrice: Long,
    val quantity: Int
)
