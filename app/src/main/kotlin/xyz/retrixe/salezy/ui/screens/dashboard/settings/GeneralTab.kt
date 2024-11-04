package xyz.retrixe.salezy.ui.screens.dashboard.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun GeneralTab(
    taxRate: Pair<String, String>, setTaxRate: (Pair<String, String>) -> Unit,
) {
    Column(Modifier.padding(24.dp)) {
        OutlinedTextField(
            value = taxRate.first,
            onValueChange = {
                val conv = it.toBigDecimalOrNull()
                if (it.isEmpty() || (conv != null && conv.scale() <= 2)) {
                    setTaxRate(Pair(it, ""))
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
