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
data class LocalConfiguration(
    val instanceUrl: String
) {companion object {
    @OptIn(ExperimentalResourceApi::class)
    val default = Json.decodeFromString<LocalConfiguration>(runBlocking {
        Res.readBytes("files/config.json").decodeToString()
    })
}}

val LocalConfigurationState = compositionLocalOf { LocalConfiguration.default }

// If all this becomes unwieldy at some point, consider moving it to `storage` package.
val userConfigFolder = if (System.getProperty("os.name").startsWith("Windows", true)) {
    File(System.getenv("LOCALAPPDATA"))
} else if (System.getProperty("os.name").startsWith("Mac", true)) {
    File(System.getProperty("user.home"), "Library/Application Support")
} else {
    File(System.getenv("XDG_CONFIG_HOME") ?: "${System.getProperty("user.home")}/.config")
}

@OptIn(ExperimentalResourceApi::class)
suspend fun configurationFile(): File = withContext(Dispatchers.IO) {
    File(File(userConfigFolder, "salezy"), "config.json").apply {
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

suspend fun loadConfiguration(): LocalConfiguration = withContext(Dispatchers.IO) {
    Json.decodeFromString<LocalConfiguration>(configurationFile().readText())
}

suspend fun saveConfiguration(configuration: LocalConfiguration) = withContext(Dispatchers.IO) {
    configurationFile().writeText(Json.encodeToString(configuration))
}
