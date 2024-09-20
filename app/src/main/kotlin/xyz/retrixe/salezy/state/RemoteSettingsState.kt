package xyz.retrixe.salezy.state

import androidx.compose.runtime.compositionLocalOf
import kotlinx.serialization.Serializable

@Serializable
data class RemoteSettings(
    val taxRate: Long
) {companion object {
    val default = RemoteSettings(20L)
}}

val RemoteSettingsState = compositionLocalOf { RemoteSettings.default }
