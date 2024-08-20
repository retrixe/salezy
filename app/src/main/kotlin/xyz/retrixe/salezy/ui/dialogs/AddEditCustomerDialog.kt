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
import xyz.retrixe.salezy.api.entities.Customer

@Composable
fun AddEditCustomerDialog(
    open: Boolean,
    label: String,
    initialValue: Customer?,
    onDismiss: () -> Unit,
    onSubmit: (Customer) -> Unit // FIXME API logic in dialog
) {
    var id by remember { mutableStateOf(initialValue?.id ?: (Math.random() * 10000).toInt()) }
    var phone by remember { mutableStateOf(initialValue?.phone ?: "") }
    var name by remember { mutableStateOf(initialValue?.name ?: "") }
    var email by remember { mutableStateOf(initialValue?.email ?: "") }
    var address by remember { mutableStateOf(initialValue?.address ?: "") }
    var taxIdNumber by remember { mutableStateOf(initialValue?.taxIdNumber ?: "") }
    var notes by remember { mutableStateOf(initialValue?.notes ?: "") }

    fun onSave() {
        onSubmit(Customer(
            id,
            phone,
            name.ifBlank { null },
            email.ifBlank { null },
            address.ifBlank { null },
            taxIdNumber.ifBlank { null },
            notes.ifBlank { null }))
        onDismiss()
    }

    // FIXME eugh eugh eugh
    fun onExit() {
        id = (Math.random() * 10000).toInt()
        phone = ""
        name = ""
        email = ""
        address = ""
        taxIdNumber = ""
        notes = ""
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
                    OutlinedTextField(value = phone, onValueChange = { phone = it },
                        label = { Text("Phone") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    OutlinedTextField(value = email, onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                    OutlinedTextField(value = address, onValueChange = { address = it },
                        label = { Text("Address") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
                    OutlinedTextField(value = taxIdNumber, onValueChange = { taxIdNumber = it },
                        label = { Text("Tax ID Number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
                    OutlinedTextField(value = notes, onValueChange = { notes = it },
                        label = { Text("Notes") },
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))

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
