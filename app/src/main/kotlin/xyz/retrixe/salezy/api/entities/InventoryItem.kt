package xyz.retrixe.salezy.api.entities

import kotlinx.serialization.Serializable

@Serializable data class InventoryItem(
    val name: String,
    val imageUrl: String?,
    val upc: Long,
    val sku: String,
    val costPrice: Long,
    val sellingPrice: Long,
    val quantity: Int
)
