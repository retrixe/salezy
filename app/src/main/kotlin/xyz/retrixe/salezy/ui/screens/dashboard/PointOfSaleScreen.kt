package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.retrixe.salezy.api.entities.InvoicedItem
import xyz.retrixe.salezy.state.TempState
import xyz.retrixe.salezy.ui.components.HeadTableCell
import xyz.retrixe.salezy.ui.components.TableCell

@Composable
fun PointOfSaleScreen() {
    val invoiceItems = remember { mutableStateListOf<InvoicedItem>() }
    var customerId by remember { mutableStateOf<Int?>(null) }
    var addInvoiceItemField by remember { mutableStateOf("") }

    fun addInvoiceItem() {
        val fieldAsUPC = addInvoiceItemField.toLongOrNull()
        val invoiceItemByUPC =
            if (fieldAsUPC != null) invoiceItems.indexOfFirst { it.id == fieldAsUPC }
            else -1
        val inventoryItemByUPC =
            if (fieldAsUPC != null) TempState.inventoryItems.find { it.upc == fieldAsUPC }
            else null

        if (fieldAsUPC != null && inventoryItemByUPC != null && invoiceItemByUPC == -1) {
            invoiceItems.add(InvoicedItem(fieldAsUPC, 1))
        } else if (fieldAsUPC != null && inventoryItemByUPC != null) {
            invoiceItems[invoiceItemByUPC] = invoiceItems[invoiceItemByUPC]
                .copy(count = invoiceItems[invoiceItemByUPC].count + 1)
        } else {
            // FIXME: Search SKU else show popover
        }

        addInvoiceItemField = ""
    }

    Row(Modifier.fillMaxSize().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(Modifier.weight(1f).fillMaxHeight()) {
            Row(Modifier.fillMaxWidth()) {
                // FIXME: Actions should have max width
                HeadTableCell("Actions", .3f)
                HeadTableCell("Name", .3f)
                HeadTableCell("UPC", .2f)
                HeadTableCell("Price", .15f)
                HeadTableCell("Qty", .1f)
            }
            LazyColumn(Modifier.weight(1f)) {
                itemsIndexed(invoiceItems) { index, item ->
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)
                    Row(Modifier.fillMaxWidth()) {
                        Row(Modifier.weight(.3f)) {
                            IconButton(onClick = { invoiceItems[index] = item.copy(count = item.count + 1) }) {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                            }
                            IconButton(onClick = {
                                if (item.count == 1) invoiceItems.removeAt(index)
                                else invoiceItems[index] = item.copy(count = item.count - 1)
                            }) {
                                Icon(imageVector = Icons.Filled.Remove, contentDescription = "Remove")
                            }
                            IconButton(onClick = { invoiceItems.removeAt(index) }) {
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                            }
                        }
                        // FIXME truncate if too long
                        val inventoryItem = TempState.inventoryItems.find { it.upc == item.id }!! // FIXME: Drop assert
                        TableCell(text = inventoryItem.name, weight = .3f)
                        TableCell(text = inventoryItem.upc.toString(), weight = .2f)
                        TableCell(text = "$${inventoryItem.price}", weight = .15f)
                        TableCell(text = item.count.toString(), weight = .1f)
                    }
                }
            }
            OutlinedTextField(value = addInvoiceItemField, onValueChange = { addInvoiceItemField = it },
                label = { Text("Add item by UPC") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                keyboardActions = KeyboardActions(onNext = { addInvoiceItem() }),
                modifier = Modifier.fillMaxWidth().padding(12.dp)
                    .onKeyEvent {
                        // KeyDown is bust on Linux
                        if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                            addInvoiceItem()
                            true
                        } else false
                    })
        }

        Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(Modifier.weight(1f).fillMaxWidth()) { Column(Modifier.fillMaxSize().padding(12.dp)) {
                Text("Customer Info", fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))
                if (customerId != null) {
                    val customer = TempState.customers.find { it.id == customerId }!!
                    Text("Name: ${customer.name}")
                    Text("Phone: ${customer.phone}")
                    Text("Email: ${customer.email}")
                    Text("Address: ${customer.address}")
                    Text("Notes: ${customer.notes}")
                    Row {
                        Button(onClick = { /* FIXME: Open customer edit */ }) {
                            Text("Edit Customer")
                        }
                        Button(onClick = { customerId = null }) {
                            Text("Clear")
                        }
                    }
                } else {
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { /* FIXME: Open customer search */ }, Modifier.weight(1f)) {
                            Text("Returning Customer")
                        }
                        Button(onClick = { /* FIXME */ }, Modifier.weight(1f)) {
                            Text("New Customer")
                        }
                    }
                }
            } }
            Card(Modifier.weight(1f).fillMaxWidth()) {
                // FIXME: Notes and specialised tax rate
            }
        }
    }
}
