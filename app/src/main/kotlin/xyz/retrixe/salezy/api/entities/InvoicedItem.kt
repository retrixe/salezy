package xyz.retrixe.salezy.api.entities

import kotlinx.serialization.Serializable

@Serializable data class InvoicedItem(
    val id: Long,
    val count: Int
    // name, upc, sku, costPrice, sellingPrice
)
