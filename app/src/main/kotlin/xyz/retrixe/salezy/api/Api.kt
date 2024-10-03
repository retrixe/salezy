package xyz.retrixe.salezy.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import xyz.retrixe.salezy.api.entities.Customer
import xyz.retrixe.salezy.api.entities.InventoryItem
import xyz.retrixe.salezy.api.entities.ephemeral.EphemeralCustomer
import xyz.retrixe.salezy.api.entities.ephemeral.EphemeralInventoryItem
import xyz.retrixe.salezy.state.LocalConfiguration
import xyz.retrixe.salezy.state.RemoteSettings

object Api {
    var token = ""
    var url = LocalConfiguration.default.instanceUrl
    private val client = HttpClient(Java) {
        // TODO: https://ktor.io/docs/client-response-validation.html
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(DefaultRequest) {
            url(this@Api.url)
            headers["Authorization"] = token
            contentType(ContentType.Application.Json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
        }
    }

    suspend fun login(username: String, password: String): String {
        @Serializable data class LoginRequestBody(val username: String, val password: String)
        @Serializable data class LoginResponseBody(val token: String)

        val response = client.post("login") {
            setBody(LoginRequestBody(username, password))
        }
        if (!response.status.isSuccess()) {
            throw ApiException(response.body<ErrorResponseBody>().error)
        }
        return response.body<LoginResponseBody>().token
    }

    suspend fun getSettings(): RemoteSettings {
        val response = client.get("settings")
        if (!response.status.isSuccess()) {
            throw ApiException(response.body<ErrorResponseBody>().error)
        }
        return response.body<RemoteSettings>()
    }

    suspend fun postSettings(settings: RemoteSettings) {
        val response = client.post("settings") {
            setBody(settings)
        }
        if (!response.status.isSuccess()) {
            throw ApiException(response.body<ErrorResponseBody>().error)
        }
    }

    suspend fun getCustomers(): Sequence<Customer> {
        val response = client.get("customers")
        if (!response.status.isSuccess()) {
            throw ApiException(response.body<ErrorResponseBody>().error)
        } else {
            return response.body<Sequence<Customer>>()
        }
    }

    suspend fun postCustomer(customer: EphemeralCustomer): Customer {
        val response = client.post("customer") {
            setBody(customer)
        }
        if (!response.status.isSuccess()) {
            throw ApiException(response.body<ErrorResponseBody>().error)
        } else {
            return response.body<Customer>()
        }
    }

    suspend fun patchCustomer(id: Int, customer: EphemeralCustomer): Customer {
        val response = client.patch("customer") {
            url.appendPathSegments(id.toString())
            setBody(customer)
        }
        if (!response.status.isSuccess()) {
            throw ApiException(response.body<ErrorResponseBody>().error)
        } else {
            return response.body<Customer>()
        }
    }

    suspend fun getInventoryItems(): Sequence<InventoryItem> {
        val response = client.get("inventoryItems")
        if (!response.status.isSuccess()) {
            throw ApiException(response.body<ErrorResponseBody>().error)
        } else {
            return response.body<Sequence<InventoryItem>>()
        }
    }

    suspend fun postInventoryItem(inventoryItem: EphemeralInventoryItem): InventoryItem {
        val response = client.post("inventoryItem") {
            setBody(inventoryItem)
        }
        if (!response.status.isSuccess()) {
            throw ApiException(response.body<ErrorResponseBody>().error)
        } else {
            return response.body<InventoryItem>()
        }
    }

    suspend fun patchInventoryItem(inventoryItem: EphemeralInventoryItem): InventoryItem {
        val response = client.patch("inventoryItem") {
            url.appendPathSegments(inventoryItem.upc.toString())
            setBody(inventoryItem)
        }
        if (!response.status.isSuccess()) {
            throw ApiException(response.body<ErrorResponseBody>().error)
        } else {
            return response.body<InventoryItem>()
        }
    }

    fun getAssetUrl(assetId: String) =
        URLBuilder(url).appendPathSegments("asset", assetId).buildString()
}
