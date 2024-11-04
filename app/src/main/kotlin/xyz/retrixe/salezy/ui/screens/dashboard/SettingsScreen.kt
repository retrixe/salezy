package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import xyz.retrixe.salezy.api.Api
import xyz.retrixe.salezy.state.LocalSnackbarHostState
import xyz.retrixe.salezy.state.RemoteSettings
import xyz.retrixe.salezy.state.RemoteSettingsState
import xyz.retrixe.salezy.ui.screens.dashboard.settings.AccountsTab
import xyz.retrixe.salezy.ui.screens.dashboard.settings.GeneralTab
import xyz.retrixe.salezy.utils.asDecimal
import xyz.retrixe.salezy.utils.toDecimalLong

private enum class SettingsScreenTabs(val pretty: String) {
    GENERAL("General"),
    ACCOUNTS("Accounts")
}

@Composable
fun SettingsScreen(setRemoteSettings: (RemoteSettings) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val remoteSettings = RemoteSettingsState.current
    var currentTab by remember { mutableStateOf(SettingsScreenTabs.GENERAL) }

    var taxRate by remember { mutableStateOf(Pair(remoteSettings.taxRate.asDecimal(), "")) }

    val changed =
        (taxRate.first.isEmpty() || taxRate.first.toDecimalLong() != remoteSettings.taxRate)

    fun onSave() = coroutineScope.launch {
        if (!changed) return@launch // ExtendedFloatingActionButton can't even be disabled...
        if (taxRate.first.isBlank()) taxRate = Pair(taxRate.first, "No tax rate provided!")

        try {
            val newSettings = RemoteSettings(
                if (taxRate.second.isEmpty()) taxRate.first.toDecimalLong() else return@launch
            )
            Api.postSettings(newSettings)
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
            ExtendedFloatingActionButton(
                onClick = ::onSave,
                icon = { Icon(imageVector = Icons.Filled.Save, "Save Changes") },
                text = { Text("Save Changes") }
            )
        }
        TabRow(
            modifier = Modifier.padding(vertical = 8.dp),
            selectedTabIndex = currentTab.ordinal
        ) {
            SettingsScreenTabs.entries.forEach { tab ->
                Tab(
                    text = { Text(tab.pretty) },
                    selected = currentTab == tab,
                    onClick = { currentTab = tab }
                )
            }
        }
        Card(Modifier.fillMaxSize()) {
            when (currentTab) {
                SettingsScreenTabs.GENERAL -> GeneralTab(
                    taxRate, { taxRate = it }
                )
                SettingsScreenTabs.ACCOUNTS -> AccountsTab()
            }
        }
    }
}
