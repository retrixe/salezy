package xyz.retrixe.salezy.api.entities.ephemeral

import kotlinx.serialization.Serializable

@Serializable data class EphemeralCustomer(
    val phone: String,
    val name: String?,
    val email: String?,
    val address: String?,
    val taxIdNumber: String?,
    val notes: String?,
)
