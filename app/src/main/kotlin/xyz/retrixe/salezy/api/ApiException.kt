package xyz.retrixe.salezy.api

import kotlinx.serialization.Serializable

@Serializable data class ErrorResponseBody(val error: String = "Unknown server error!")

class ApiException(message: String) : Exception(message)
