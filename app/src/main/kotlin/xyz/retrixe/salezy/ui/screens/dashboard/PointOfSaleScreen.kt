package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import xyz.retrixe.salezy.api.Api
import xyz.retrixe.salezy.api.entities.Customer
import xyz.retrixe.salezy.api.entities.InventoryItem
import xyz.retrixe.salezy.api.entities.Invoice
import xyz.retrixe.salezy.api.entities.InvoicedItem
import xyz.retrixe.salezy.state.LocalSnackbarHostState
import xyz.retrixe.salezy.state.RemoteSettingsState
import xyz.retrixe.salezy.state.TempState
import xyz.retrixe.salezy.ui.components.HeadTableCell
import xyz.retrixe.salezy.ui.components.PlainTooltipBox
import xyz.retrixe.salezy.ui.components.TableCell
import xyz.retrixe.salezy.ui.dialogs.AddEditCustomerDialog
import xyz.retrixe.salezy.ui.dialogs.SearchForCustomerDialog
import xyz.retrixe.salezy.utils.asDecimal
import java.time.Instant

data class TempInvoiceItem(val inventoryItem: InventoryItem, val count: Int) {
    fun asInvoicedItem() = InvoicedItem(inventoryItem.upc, count)
}

@Composable
fun PointOfSaleScreen() {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current

    val invoiceItems = remember { mutableStateMapOf<Long, TempInvoiceItem>() }
    var addInvoiceItemField by remember { mutableStateOf("") }
    var customer by remember { mutableStateOf<Customer?>(null) }
    var shippingAddress by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var overrideTaxRateValue by remember { mutableStateOf("") }

    fun addInvoiceItem() {
        val id = addInvoiceItemField
        addInvoiceItemField = ""
        coroutineScope.launch { try {
            if (id.isBlank()) return@launch
            val fieldAsUPC = id.toLongOrNull()
            val existingInvoiceItem = fieldAsUPC?.let { invoiceItems[fieldAsUPC] }
            if (existingInvoiceItem != null) {
                invoiceItems[fieldAsUPC] =
                    existingInvoiceItem.copy(count = existingInvoiceItem.count + 1)
                return@launch
            }

            val matchingInventoryItem = Api.queryInventoryItemById(id)
            if (matchingInventoryItem != null) {
                invoiceItems[matchingInventoryItem.upc] = TempInvoiceItem(matchingInventoryItem, 1)
            } else {
                snackbarHostState.showSnackbar(
                    message = "Item not found! Enter valid UPC or SKU.",
                    actionLabel = "Hide",
                    duration = SnackbarDuration.Short)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar(
                message = "An error has occurred!",
                actionLabel = "Hide",
                duration = SnackbarDuration.Short)
        } }
    }

    var openNewCustomerDialog by remember { mutableStateOf(false) }
    AddEditCustomerDialog(
        open = openNewCustomerDialog,
        label = "Add New Customer",
        initialValue = null,
        onDismiss = { openNewCustomerDialog = false },
        onSubmit = { customer = it })

    var openEditCustomerDialog by remember { mutableStateOf(false) }
    AddEditCustomerDialog(
        open = openEditCustomerDialog,
        label = "Edit Customer",
        initialValue = customer,
        onDismiss = { openEditCustomerDialog = false },
        onSubmit = { customer = it })

    var openExistingCustomerDialog by remember { mutableStateOf(false) }
    SearchForCustomerDialog(openExistingCustomerDialog,
        { openExistingCustomerDialog = false },
        { customer = it })

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
                    val total = invoiceItems.values
                        .sumOf { item -> item.inventoryItem.sellingPrice * item.count }
                    val tax = (total * overrideTaxRateValue.toInt()) / 100
                    val totalWithTax = total + tax
                    Text("Total incl. tax: \$${totalWithTax.asDecimal()}")
                    Button(onClick = {
                        openProceedPaymentDialog = false
                        // FIXME: Remove TempState reference
                        TempState.invoices.add(Invoice(
                            id = TempState.invoices.size + 1,
                            customerId = customer!!.id,
                            items = invoiceItems.map { it.value.asInvoicedItem() },
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
        Card(Modifier.weight(2f).fillMaxHeight()) {
            Row(Modifier.fillMaxWidth()) {
                HeadTableCell("Actions", 144.dp)
                HeadTableCell("Name", .25f)
                HeadTableCell("UPC", .25f)
                HeadTableCell("SKU", .25f)
                HeadTableCell("Price", .15f)
                HeadTableCell("Qty", .1f)
            }
            LazyColumn(Modifier.weight(1f)) {
                itemsIndexed(invoiceItems.toList()) { _, itemPair ->
                    val itemId = itemPair.first
                    val item = itemPair.second
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)
                    Row(Modifier.fillMaxWidth()) {
                        Row(Modifier.widthIn(min = 144.dp)) {
                            PlainTooltipBox("Add") {
                                IconButton(onClick = { invoiceItems[itemId] = item.copy(count = item.count + 1) }) {
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                                }
                            }
                            PlainTooltipBox("Remove") {
                                IconButton(onClick = {
                                    if (item.count == 1) invoiceItems.remove(itemId)
                                    else invoiceItems[itemId] = item.copy(count = item.count - 1)
                                }) {
                                    Icon(imageVector = Icons.Filled.Remove, contentDescription = "Remove")
                                }
                            }
                            PlainTooltipBox("Delete") {
                                IconButton(onClick = { invoiceItems.remove(itemId) }) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                        val inventoryItem = item.inventoryItem
                        TableCell(text = inventoryItem.name, weight = .25f)
                        TableCell(text = inventoryItem.upc.toString(), weight = .25f)
                        TableCell(text = inventoryItem.sku, weight = .25f)
                        TableCell(text = "$${inventoryItem.sellingPrice.asDecimal()}", weight = .15f)
                        TableCell(text = item.count.toString(), weight = .1f)
                    }
                }
            }

            OutlinedTextField(value = addInvoiceItemField, onValueChange = { addInvoiceItemField = it },
                label = { Text("Add item by UPC or SKU") },
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

            val total = invoiceItems.values
                .sumOf { item -> item.inventoryItem.sellingPrice * item.count }
            Text("Total excl tax: \$${total.asDecimal()}", fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp))
        }

        Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(Modifier.weight(1f).fillMaxWidth()) {
                Column(Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState())) {
                    Text("Customer Info", fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))
                    val c = customer
                    if (c != null) {
                        Text("Name: ${c.name ?: "N/A"}")
                        Text("Phone: ${c.phone}")
                        Text("Email: ${c.email ?: "N/A"}")
                        Text("Address: ${c.address ?: "N/A"}")
                        Text("Notes: ${c.notes ?: "N/A"}")
                        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(onClick = { openEditCustomerDialog = true }) {
                                Text("Edit Customer")
                            }
                            OutlinedButton(onClick = { customer = null }) {
                                Text("Clear")
                            }
                        }

                        OutlinedTextField(value = shippingAddress,
                            onValueChange = { shippingAddress = it },
                            label = { Text("Custom shipping address (Override customer address)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    } else {
                        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = { openExistingCustomerDialog = true }, Modifier.weight(1f)) {
                                Text("Exists Customer")
                            }
                            Button(onClick = { openNewCustomerDialog = true }, Modifier.weight(1f)) {
                                Text("New Customer")
                            }
                        }
                    }
                }
            }
            Card(Modifier.weight(1f).fillMaxWidth()) { Column(
                Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.End) {
                OutlinedTextField(value = notes, onValueChange = { notes = it },
                    label = { Text("Notes") },
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).weight(1f))

                OutlinedTextField(value = overrideTaxRateValue,
                    onValueChange = {
                        val conv = it.toBigDecimalOrNull()
                        if (it.isEmpty() || (conv != null && conv.scale() <= 2)) {
                            overrideTaxRateValue = it
                        }
                    },
                    label = {
                        Text("Override tax rate (Default: ${RemoteSettingsState.current.taxRate.asDecimal()})")
                    },
                    // TODO: Material3 1.4.0 - labelPosition = TextFieldLabelPosition.Above(), remove default from label
                    placeholder = {
                        Text("Default: ${RemoteSettingsState.current.taxRate.asDecimal()}")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))

                Button(onClick = {
                    if (customer == null) {
                        coroutineScope.launch { snackbarHostState.showSnackbar(
                            message = "Please select a customer.",
                            actionLabel = "Hide",
                            duration = SnackbarDuration.Short) }
                    } else if (invoiceItems.isEmpty()) {
                        coroutineScope.launch { snackbarHostState.showSnackbar(
                            message = "No items have been added to the invoice cart!",
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
