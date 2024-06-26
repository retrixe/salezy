package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen() {
    var taxRateValue by remember { mutableStateOf(20F) }

    // FIXME: Tax rate configuration server-side
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Settings", fontSize = 24.sp)
        }
        Box(Modifier.padding(8.dp))
        Card(Modifier.fillMaxSize()) {
            Column(Modifier.padding(24.dp)) {
                OutlinedTextField(value = taxRateValue.toString(),
                    onValueChange = { taxRateValue = it.toFloatOrNull() ?: taxRateValue },
                    label = { Text("Tax rate") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    keyboardActions = KeyboardActions(onNext = { /* */ }),
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
