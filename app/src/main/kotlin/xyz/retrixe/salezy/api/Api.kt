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
import xyz.retrixe.salezy.state.LocalConfiguration
import xyz.retrixe.salezy.state.RemoteSettings

class Api {
    companion object {
        val instance = Api()
    }

    var token = ""
    var url = LocalConfiguration.default.instanceUrl
    private val client = HttpClient(Java) {
        // TODO: https://ktor.io/docs/client-response-validation.html
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
        if (!response.status.isSuccess()) {
            throw ApiException(response.body<ErrorResponseBody>().error)
        }
        return response.body<LoginResponseBody>().token
    }

    suspend fun getSettings(): RemoteSettings {
        // For now, this is compatible with RemoteSettings
        // @Serializable data class SettingsResponseBody()

        val response = client.get("settings")
        if (!response.status.isSuccess()) {
            throw ApiException(response.body<ErrorResponseBody>().error)
        }
        return response.body<RemoteSettings>()
    }

    suspend fun postSettings(settings: RemoteSettings) {
        // For now, this is compatible with RemoteSettings
        // @Serializable data class SettingsRequestBody()

        val response = client.post("settings") {
            setBody(settings)
        }
        if (!response.status.isSuccess()) {
            throw ApiException(response.body<ErrorResponseBody>().error)
        }
    }
}
