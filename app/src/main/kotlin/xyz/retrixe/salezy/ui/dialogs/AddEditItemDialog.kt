package xyz.retrixe.salezy.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import xyz.retrixe.salezy.api.entities.InventoryItem

@Composable
fun AddEditItemDialog(
    open: Boolean,
    label: String,
    initialValue: InventoryItem?,
    onDismiss: () -> Unit,
    onSubmit: (InventoryItem) -> Unit // FIXME API logic in dialog
) {
    var name by remember { mutableStateOf("") }
    // var imageUrl by remember { mutableStateOf<String?>(null) }
    var upc by remember { mutableStateOf(0L) }
    var sku by remember { mutableStateOf("") }
    var price by remember { mutableStateOf(0L) }
    var quantity by remember { mutableStateOf(0) }

    LaunchedEffect(open) {
        name = initialValue?.name ?: ""
        upc = initialValue?.upc ?: 0L
        sku = initialValue?.sku ?: ""
        price = initialValue?.price ?: 0L
        quantity = initialValue?.quantity ?: 0
    }

    fun onSave() {
        onSubmit(InventoryItem(name, null, upc, sku, price, quantity))
        onDismiss()
    }

    AnimatedVisibility(open) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Card(
                modifier = Modifier.wrapContentSize().width(420.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp)
                ) {
                    // FIXME: This is not good, it's the bare minimum, need required fields etc
                    Text(label, fontSize = 28.sp, modifier = Modifier.padding(vertical = 16.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = name, onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    // FIXME image upload
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = upc.toString(), onValueChange = { upc = it.toLongOrNull() ?: upc },
                        label = { Text("UPC") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = sku, onValueChange = { sku = it },
                        label = { Text("SKU") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = price.toString(), onValueChange = { price = it.toLongOrNull() ?: price },
                        label = { Text("Price") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = quantity.toString(), onValueChange = { quantity = it.toIntOrNull() ?: quantity },
                        label = { Text("Quantity") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        FilledTonalButton(onClick = { onDismiss() }) { Text("Cancel") }
                        Button(onClick = { onSave() }) { Text("Save") }
                    }
                }
            }
        }
    }
}
