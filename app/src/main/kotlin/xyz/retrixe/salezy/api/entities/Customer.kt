package xyz.retrixe.salezy.api.entities

import kotlinx.serialization.Serializable

@Serializable data class Customer(
    val id: Int,
    val phone: String,
    val name: String?,
    val email: String?,
    val address: String?,
    val notes: String?,
)
