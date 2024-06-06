package xyz.retrixe.salezy.api.entities

import kotlinx.serialization.Serializable

@Serializable data class InvoicedItem(
    val id: Int,
    val count: Int
)
