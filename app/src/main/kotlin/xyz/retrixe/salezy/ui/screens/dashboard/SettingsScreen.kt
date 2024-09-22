package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import xyz.retrixe.salezy.api.Api
import xyz.retrixe.salezy.state.LocalSnackbarHostState
import xyz.retrixe.salezy.state.RemoteSettings
import xyz.retrixe.salezy.state.RemoteSettingsState
import xyz.retrixe.salezy.utils.asDecimal
import xyz.retrixe.salezy.utils.toDecimalLong

@Composable
fun SettingsScreen(setRemoteSettings: (RemoteSettings) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val remoteSettings = RemoteSettingsState.current

    var taxRate by remember { mutableStateOf(Pair("", "")) }
    LaunchedEffect(Unit) {
        taxRate = Pair(RemoteSettings.default.taxRate.asDecimal(), "")
    }

    val changed =
        taxRate.first.isEmpty() || taxRate.first.toDecimalLong() != remoteSettings.taxRate

    fun onSave() = coroutineScope.launch {
        if (taxRate.first.isBlank()) taxRate = Pair(taxRate.first, "No tax rate provided!")
        try {
            val newSettings = RemoteSettings(
                if (taxRate.second.isEmpty()) taxRate.first.toDecimalLong() else return@launch
            )
            Api.instance.postSettings(newSettings)
            setRemoteSettings(newSettings)
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar(
                message = "Failed to save settings! ${e.message}",
                actionLabel = "Hide",
                duration = SnackbarDuration.Long)
        }
    }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Settings", fontSize = 24.sp)
            if (changed) ExtendedFloatingActionButton(
                onClick = { onSave() },
                icon = { Icon(imageVector = Icons.Filled.Save, "Save Changes") },
                text = { Text("Save Changes") }
            )
        }
        Box(Modifier.padding(8.dp))
        Card(Modifier.fillMaxSize()) {
            Column(Modifier.padding(24.dp)) {
                OutlinedTextField(value = taxRate.first,
                    onValueChange = {
                        val conv = it.toBigDecimalOrNull()
                        if (it.isEmpty() || (conv != null && conv.scale() <= 2)) {
                            taxRate = Pair(it, "")
                        }
                    },
                    label = { Text("Tax rate") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    keyboardActions = KeyboardActions(onNext = { /* */ }),
                    isError = taxRate.second.isNotEmpty(),
                    supportingText = if (taxRate.second.isNotEmpty()) {
                        @Composable { Text(taxRate.second) }
                    } else null,
                    modifier = Modifier.width(320.dp)
                        .onKeyEvent {
                            // KeyDown is bust on Linux
                            if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                                true
                            } else false
                        })
            }
        }
    }
}
