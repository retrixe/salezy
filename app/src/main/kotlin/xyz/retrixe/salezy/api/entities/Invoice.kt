package xyz.retrixe.salezy.api.entities

import kotlinx.serialization.Serializable

@Serializable data class Invoice(
    val id: Int,
    val customerId: Int,
    val beforeTaxCost: Long,
    val afterTaxCost: Long,
    val issuedOn: Long,
    val items: List<InvoicedItem>,
    val notes: String?,
)
