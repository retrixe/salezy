package xyz.retrixe.salezy.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    var name by remember { mutableStateOf(initialValue?.name ?: "") }
    // var imageUrl by remember { mutableStateOf<String?>(null) }
    var upc by remember { mutableStateOf(initialValue?.upc ?: 0L) }
    var sku by remember { mutableStateOf(initialValue?.sku ?: "") }
    var price by remember { mutableStateOf(initialValue?.price ?: 0L) }
    var quantity by remember { mutableStateOf(initialValue?.quantity ?: 0) }

    fun onSave() {
        onSubmit(InventoryItem(name, null, upc, sku, price, quantity))
        onDismiss()
    }

    // FIXME eugh eugh eugh
    fun onExit() {
        name = ""
        upc = 0L
        sku = ""
        price = 0L
        quantity = 0
        onDismiss()
    }

    AnimatedVisibility(open) {
        Dialog(onDismissRequest = { onExit() }) {
            Card(
                modifier = Modifier.wrapContentSize().width(420.dp).padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // FIXME: This is not good, it's the bare minimum, need required fields etc
                    Text(label, fontSize = 28.sp, modifier = Modifier.padding(vertical = 16.dp))

                    OutlinedTextField(value = name, onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
                    // FIXME image upload
                    OutlinedTextField(value = upc.toString(), onValueChange = { upc = it.toLongOrNull() ?: upc },
                        label = { Text("UPC") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = sku, onValueChange = { sku = it },
                        label = { Text("SKU") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
                    OutlinedTextField(value = price.toString(), onValueChange = { price = it.toLongOrNull() ?: price },
                        label = { Text("Price") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = quantity.toString(), onValueChange = { quantity = it.toIntOrNull() ?: quantity },
                        label = { Text("Quantity") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        TextButton(onClick = { onExit() }, modifier = Modifier.padding(8.dp)) {
                            Text("Cancel")
                        }
                        TextButton(onClick = { onSave() }, modifier = Modifier.padding(8.dp)) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}
