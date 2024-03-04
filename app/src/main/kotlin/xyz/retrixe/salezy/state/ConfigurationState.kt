package xyz.retrixe.salezy.state

import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import xyz.retrixe.salezy.generated.resources.Res
import java.io.File
import java.io.IOException

@Serializable
data class Configuration(
    val instanceUrl: String
)

val ConfigurationState = compositionLocalOf { defaultConfiguration }

@OptIn(ExperimentalResourceApi::class)
val defaultConfiguration = Json.decodeFromString<Configuration>(runBlocking {
    Res.readBytes("files/config.json").decodeToString()
})

// If all this becomes unwieldy at some point, consider moving it to `store` package.

fun getUserConfigFolder() = "/home/ideapad/.config/" // FIXME OS dependent drivel

@OptIn(ExperimentalResourceApi::class)
suspend fun configurationFile(): File = withContext(Dispatchers.IO) {
    File(File(getUserConfigFolder(), "salezy"), "config.json").apply {
        if (!exists()) {
            if (!parentFile.isDirectory && !parentFile.mkdirs()) {
                throw IOException("Failed to create config parent folders at ${parent}!")
            }
            writeBytes(Res.readBytes("files/config.json"))
        } else if (!isFile) {
            throw IOException("Path $path is not a file!")
        }
    }
}

suspend fun loadConfiguration(): Configuration = withContext(Dispatchers.IO) {
    Json.decodeFromString<Configuration>(configurationFile().readText())
}

suspend fun saveConfiguration(configuration: Configuration) = withContext(Dispatchers.IO) {
    configurationFile().writeText(Json.encodeToString(configuration))
}
