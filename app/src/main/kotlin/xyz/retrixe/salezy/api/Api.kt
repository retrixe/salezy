package xyz.retrixe.salezy.api

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.http.*
import xyz.retrixe.salezy.state.defaultConfiguration

class Api {
    private val client = HttpClient(Java)
    var url = defaultConfiguration.instanceUrl

    companion object {
        val instance = Api()
    }

    suspend fun login(username: String, password: String): String {
        val response = client.post(url) {
            url { appendPathSegments("/api/login") }
            setBody("{}") // FIXME
        }
        if (response.status.isSuccess()) {
            // FIXME
        } else {
            // FIXME
        }
        return ""
    }
}
