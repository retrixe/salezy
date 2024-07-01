package xyz.retrixe.salezy.api.entities

import kotlinx.serialization.Serializable

// FIXME: Nullable values kinda suck, come to a policy about this
@Serializable data class Customer(
    val id: Int,
    val phone: String,
    val name: String?,
    val email: String?,
    val address: String?,
    val taxIdNumber: String?,
    val notes: String?,
)
