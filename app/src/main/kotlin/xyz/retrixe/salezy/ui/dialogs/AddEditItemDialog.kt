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
import xyz.retrixe.salezy.utils.asDecimal
import xyz.retrixe.salezy.utils.toDecimalLong

// TODO (low priority): Dedup add/edit dialog calls
@Composable
fun AddEditItemDialog(
    open: Boolean,
    label: String,
    initialValue: InventoryItem?,
    onDismiss: () -> Unit,
    onSubmit: (InventoryItem) -> Unit // FIXME API logic in dialog
) {
    var name by remember { mutableStateOf(Pair("", "")) }
    // var imageUrl by remember { mutableStateOf<String?>(null) }
    var upc by remember { mutableStateOf(Pair("", "")) }
    var sku by remember { mutableStateOf(Pair("", "")) }
    var price by remember { mutableStateOf(Pair("", "")) }
    var quantity by remember { mutableStateOf(Pair("", "")) }

    LaunchedEffect(open) {
        name = Pair(initialValue?.name ?: "", "")
        upc = Pair(initialValue?.upc?.toString() ?: "", "")
        sku = Pair(initialValue?.sku ?: "", "")
        price = Pair(initialValue?.price?.asDecimal() ?: "", "")
        quantity = Pair(initialValue?.quantity?.toString() ?: "", "")
    }

    fun onSave() {
        if (name.first.isBlank()) name = Pair(name.first, "No name provided!")
        if (upc.first.isBlank()) upc = Pair(upc.first, "No UPC provided!")
        if (sku.first.isBlank()) sku = Pair(sku.first, "No SKU provided!")
        if (price.first.isBlank()) price = Pair(price.first, "No price provided!")
        if (quantity.first.isBlank()) quantity = Pair(quantity.first, "No quantity provided!")
        onSubmit(InventoryItem(
            if (name.second.isEmpty()) name.first else return,
            null,
            if (upc.second.isEmpty()) upc.first.toLong() else return,
            if (sku.second.isEmpty()) sku.first else return,
            if (price.second.isEmpty()) price.first.toDecimalLong() else return,
            if (quantity.second.isEmpty()) quantity.first.toInt() else return,
        ))
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
                    Text(label, fontSize = 28.sp, modifier = Modifier.padding(vertical = 16.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = name.first, onValueChange = { name = Pair(it, "") },
                        label = { Text("Name*") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        isError = name.second.isNotEmpty(),
                        supportingText = if (name.second.isNotEmpty()) {
                            @Composable { Text(name.second) }
                        } else null
                    )
                    // FIXME image upload
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = upc.first,
                        onValueChange = {
                            if (it.isEmpty() || it.toLongOrNull() != null) upc = Pair(it, "")
                        },
                        label = { Text("UPC*") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = upc.second.isNotEmpty(),
                        supportingText = if (upc.second.isNotEmpty()) {
                            @Composable { Text(upc.second) }
                        } else null
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = sku.first, onValueChange = { sku = Pair(it, "") },
                        label = { Text("SKU*") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        isError = sku.second.isNotEmpty(),
                        supportingText = if (sku.second.isNotEmpty()) {
                            @Composable { Text(sku.second) }
                        } else null
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = price.first,
                        onValueChange = {
                            val conv = it.toBigDecimalOrNull()
                            if (it.isEmpty() || (conv != null && conv.scale() <= 2)) {
                                price = Pair(it, "")
                            }
                        },
                        label = { Text("Price*") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = price.second.isNotEmpty(),
                        supportingText = if (price.second.isNotEmpty()) {
                            @Composable { Text(price.second) }
                        } else null
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = quantity.first,
                        onValueChange = {
                            if (it.isEmpty() || it.toIntOrNull() != null) quantity = Pair(it, "")
                        },
                        label = { Text("Quantity*") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = quantity.second.isNotEmpty(),
                        supportingText = if (quantity.second.isNotEmpty()) {
                            @Composable { Text(quantity.second) }
                        } else null
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
