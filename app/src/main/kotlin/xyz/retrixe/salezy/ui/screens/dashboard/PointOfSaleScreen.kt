package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import xyz.retrixe.salezy.api.entities.Invoice
import xyz.retrixe.salezy.api.entities.InvoicedItem
import xyz.retrixe.salezy.state.LocalSnackbarHostState
import xyz.retrixe.salezy.state.TempState
import xyz.retrixe.salezy.ui.components.HeadTableCell
import xyz.retrixe.salezy.ui.components.PlainTooltipBox
import xyz.retrixe.salezy.ui.components.TableCell
import xyz.retrixe.salezy.ui.dialogs.AddEditCustomerDialog
import xyz.retrixe.salezy.utils.asDecimal
import java.time.Instant

@Composable
fun PointOfSaleScreen() {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current

    val invoiceItems = remember { mutableStateListOf<InvoicedItem>() }
    var addInvoiceItemField by remember { mutableStateOf("") }
    var customerId by remember { mutableStateOf<Int?>(null) }
    var notes by remember { mutableStateOf("") }
    var overrideTaxRateValue by remember { mutableStateOf(20f) }

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
            // FIXME: Search for SKUs before resorting to popover
            coroutineScope.launch { snackbarHostState.showSnackbar(
                message = "Item not found! Enter valid UPC.",
                actionLabel = "Hide",
                duration = SnackbarDuration.Short) }
        }

        addInvoiceItemField = ""
    }

    // FIXME can this be unified into one state?
    var openNewCustomerDialog by remember { mutableStateOf(false) }
    AddEditCustomerDialog(
        open = openNewCustomerDialog,
        label = "Add New Customer",
        initialValue = null,
        onDismiss = { openNewCustomerDialog = false },
        onSubmit = { TempState.customers.add(it); customerId = it.id })

    var openEditCustomerDialog by remember { mutableStateOf(false) }
    if (openEditCustomerDialog) AddEditCustomerDialog( // FIXME dirty hack, remove if statement
        open = openEditCustomerDialog,
        label = "Edit Customer",
        initialValue = TempState.customers.find { it.id == customerId },
        onDismiss = { openEditCustomerDialog = false },
        onSubmit = { TempState.customers[TempState.customers.indexOfFirst { c -> c.id == customerId }] = it })

    var openExistingCustomerDialog by remember { mutableStateOf(false) }
    // FIXME: this is bad.. implement search function lol
    AnimatedVisibility(openExistingCustomerDialog) {
        Dialog(onDismissRequest = { openExistingCustomerDialog = false }) {
            Card(
                modifier = Modifier.wrapContentSize().width(420.dp).padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text("Search for Customer", fontSize = 28.sp)
                    LazyColumn {
                        itemsIndexed(TempState.customers) { index, customer ->
                            TextButton(onClick = {
                                customerId = customer.id
                                openExistingCustomerDialog = false
                            }) {
                                Text("${customer.name ?: "N/A"} (${customer.phone})")
                            }
                        }
                    }
                }
            }
        }
    }

    // FIXME demo dialog
    var openProceedPaymentDialog by remember { mutableStateOf(false) }
    AnimatedVisibility(openProceedPaymentDialog) {
        Dialog(onDismissRequest = { openProceedPaymentDialog = false }) {
            Card(
                modifier = Modifier.wrapContentSize().width(420.dp).padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text("Payment", fontSize = 28.sp)
                    val total = invoiceItems.sumOf { item ->
                        TempState.inventoryItems.find { it.upc == item.id }!!.price * item.count }
                    val tax = (total * overrideTaxRateValue.toInt()) / 100
                    val totalWithTax = total + tax
                    Text("Total incl. tax: \$${totalWithTax.asDecimal()}")
                    Button(onClick = {
                        openProceedPaymentDialog = false
                        TempState.invoices.add(Invoice(
                            id = TempState.invoices.size + 1,
                            customerId = customerId!!,
                            items = invoiceItems,
                            notes = notes.ifBlank { null },
                            taxRate = overrideTaxRateValue.toInt(),
                            beforeTaxCost = total,
                            afterTaxCost = totalWithTax,
                            issuedOn = Instant.now().toEpochMilli()
                        ))
                    }) {
                        Text("Pay")
                    }
                }
            }
        }
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
                            PlainTooltipBox("Add") {
                                IconButton(onClick = { invoiceItems[index] = item.copy(count = item.count + 1) }) {
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                                }
                            }
                            PlainTooltipBox("Remove") {
                                IconButton(onClick = {
                                    if (item.count == 1) invoiceItems.removeAt(index)
                                    else invoiceItems[index] = item.copy(count = item.count - 1)
                                }) {
                                    Icon(imageVector = Icons.Filled.Remove, contentDescription = "Remove")
                                }
                            }
                            PlainTooltipBox("Delete") {
                                IconButton(onClick = { invoiceItems.removeAt(index) }) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                        // FIXME truncate if too long
                        // FIXME item out of stock checks
                        val inventoryItem = TempState.inventoryItems.find { it.upc == item.id }!! // FIXME: Drop assert
                        TableCell(text = inventoryItem.name, weight = .3f)
                        TableCell(text = inventoryItem.upc.toString(), weight = .2f)
                        TableCell(text = "$${inventoryItem.price.asDecimal()}", weight = .15f)
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

            // FIXME drop assert
            val total = invoiceItems.sumOf { item ->
                TempState.inventoryItems.find { it.upc == item.id }!!.price * item.count }
            Text("Total excl tax: \$${total.asDecimal()}", fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp))
        }

        Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(Modifier.weight(1f).fillMaxWidth()) { Column(Modifier.fillMaxSize().padding(12.dp)) {
                Text("Customer Info", fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))
                if (customerId != null) {
                    val customer = TempState.customers.find { it.id == customerId }!!
                    Text("Name: ${customer.name ?: "N/A"}")
                    Text("Phone: ${customer.phone}")
                    Text("Email: ${customer.email ?: "N/A"}")
                    Text("Address: ${customer.address ?: "N/A"}")
                    Text("Notes: ${customer.notes ?: "N/A"}")
                    // FIXME: shipping address
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { openEditCustomerDialog = true }) {
                            Text("Edit Customer")
                        }
                        OutlinedButton(onClick = { customerId = null }) {
                            Text("Clear")
                        }
                    }
                } else {
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { openExistingCustomerDialog = true }, Modifier.weight(1f)) {
                            Text("Returning Customer")
                        }
                        Button(onClick = { openNewCustomerDialog = true }, Modifier.weight(1f)) {
                            Text("New Customer")
                        }
                    }
                }
            } }
            Card(Modifier.weight(1f).fillMaxWidth()) { Column(
                Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.End) {
                OutlinedTextField(value = notes, onValueChange = { notes = it },
                    label = { Text("Notes") },
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).weight(1f))

                OutlinedTextField(value = overrideTaxRateValue.toString(),
                    onValueChange = { overrideTaxRateValue = it.toFloatOrNull() ?: overrideTaxRateValue },
                    label = { Text("Override tax rate") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))

                Button(onClick = {
                    // FIXME better validation
                    if (customerId == null) {
                        coroutineScope.launch { snackbarHostState.showSnackbar(
                            message = "Please select a customer.",
                            actionLabel = "Hide",
                            duration = SnackbarDuration.Short) }
                    } else openProceedPaymentDialog = true
                }) {
                    Text("Proceed to payment")
                }
            } }
        }
    }
}
