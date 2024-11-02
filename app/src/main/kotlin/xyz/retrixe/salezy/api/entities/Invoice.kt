package xyz.retrixe.salezy.api.entities

import kotlinx.serialization.Serializable

@Serializable data class Invoice(
    val id: Int,
    val customerId: Int,
    val costPreTax: Long,
    val costPostTax: Long,
    val taxRate: Int,
    val issuedOn: Long,
    val items: List<InvoicedItem>,
    val paymentMethod: Int,
    val giftCardCode: String?,
    val giftCardAmount: Long?,
    val notes: String?,
)
