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
import xyz.retrixe.salezy.state.defaultConfiguration

class Api {
    companion object {
        val instance = Api()
    }

    var token = ""
    var url = defaultConfiguration.instanceUrl
    private val client = HttpClient(Java) {
        install(ContentNegotiation) {
            json(Json)
        }
        install(DefaultRequest) {
            url(this@Api.url)
            url.appendPathSegments("api")
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
        if (response.status.isSuccess()) {
            val body: LoginResponseBody = response.body()
            return body.token
        } else {
            val body: ErrorResponseBody = response.body()
            throw ApiException(body.error)
        }
    }
}
