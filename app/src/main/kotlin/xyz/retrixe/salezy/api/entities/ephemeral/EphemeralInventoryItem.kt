@file:UseSerializers(LongAsStringSerializer::class)
package xyz.retrixe.salezy.api.entities.ephemeral

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.builtins.LongAsStringSerializer

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
