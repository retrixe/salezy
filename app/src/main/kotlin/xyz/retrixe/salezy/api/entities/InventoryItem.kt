@file:UseSerializers(LongAsStringSerializer::class)
package xyz.retrixe.salezy.api.entities

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.builtins.LongAsStringSerializer

@Serializable data class InventoryItem(
    val name: String,
    val imageId: String?,
    val upc: Long,
    val sku: String,
    val costPrice: Long,
    val sellingPrice: Long,
    val quantity: Int
)
