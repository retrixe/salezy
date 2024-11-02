package xyz.retrixe.salezy.api.entities

import kotlinx.serialization.Serializable

@Serializable data class InvoicedItem(
    val upc: Long,
    val quantity: Int,

    val name: String,
    val sku: String,
    val costPrice: Long,
    val sellingPrice: Long
)
