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
import xyz.retrixe.salezy.api.entities.Customer

// TODO (low priority): Dedup add/edit dialog calls
@Composable
fun AddEditCustomerDialog(
    open: Boolean,
    label: String,
    initialValue: Customer?,
    onDismiss: () -> Unit,
    onSubmit: (Customer) -> Unit // FIXME API logic in dialog
) {
    var id by remember { mutableStateOf(-1) }
    var phone by remember { mutableStateOf(Pair("", "")) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var taxIdNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(open) {
        id = initialValue?.id ?: -1
        phone = Pair(initialValue?.phone ?: "", "")
        name = initialValue?.name ?: ""
        email = initialValue?.email ?: ""
        address = initialValue?.address ?: ""
        taxIdNumber = initialValue?.taxIdNumber ?: ""
        notes = initialValue?.notes ?: ""
    }

    fun onSave() {
        if (phone.first.isBlank()) phone = Pair(phone.first, "No phone number provided!")
        onSubmit(Customer(
            id,
            if (phone.second.isEmpty()) phone.first else return,
            name.ifBlank { null },
            email.ifBlank { null },
            address.ifBlank { null },
            taxIdNumber.ifBlank { null },
            notes.ifBlank { null }))
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
                        value = phone.first, onValueChange = { phone = Pair(it, "") },
                        label = { Text("Phone*") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = phone.second.isNotEmpty(),
                        supportingText = if (phone.second.isNotEmpty()) {
                            @Composable { Text(phone.second) }
                        } else null
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = name, onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = email, onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = address, onValueChange = { address = it },
                        label = { Text("Address") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = taxIdNumber, onValueChange = { taxIdNumber = it },
                        label = { Text("Tax ID Number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = notes, onValueChange = { notes = it },
                        label = { Text("Notes") },
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
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
