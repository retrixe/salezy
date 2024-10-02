package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.xdrop.fuzzywuzzy.FuzzySearch
import xyz.retrixe.salezy.state.TempState
import xyz.retrixe.salezy.ui.components.HeadTableCell
import xyz.retrixe.salezy.ui.components.PlainTooltipBox
import xyz.retrixe.salezy.ui.components.SearchField
import xyz.retrixe.salezy.ui.components.TableCell
import xyz.retrixe.salezy.ui.dialogs.AddEditCustomerDialog

@Composable
fun CustomersScreen() {
    var query by remember { mutableStateOf("") }
    var customers by remember { mutableStateOf(TempState.customers) }

    val customersFiltered = if (query.isNotBlank()) {
        FuzzySearch
            .extractSorted(query, customers, { "${it.id} ${it.phone} ${it.name}" }, 60)
            .map { it.referent }
    } else customers

    var openNewCustomerDialog by remember { mutableStateOf(false) }
    AddEditCustomerDialog(
        open = openNewCustomerDialog,
        label = "Add New Customer",
        initialValue = null,
        onDismiss = { openNewCustomerDialog = false },
        onSubmit = { TempState.customers.add(it); customers = TempState.customers })

    var openEditCustomerDialog by remember { mutableStateOf<Int?>(null) }
    AddEditCustomerDialog(
        open = openEditCustomerDialog != null,
        label = "Edit Customer",
        initialValue = customers.find { it.id == openEditCustomerDialog },
        onDismiss = { openEditCustomerDialog = null },
        onSubmit = { TempState.customers[TempState.customers.indexOfFirst {
            customer -> customer.id == openEditCustomerDialog
        }] = it }
    )

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Customers", fontSize = 24.sp)
            ExtendedFloatingActionButton(
                onClick = { openNewCustomerDialog = true },
                icon = { Icon(imageVector = Icons.Filled.Add, "Add Customer") },
                text = { Text("Add Customer") }
            )
        }
        Box(Modifier.padding(8.dp))
        Card(Modifier.fillMaxSize()) {
            Column(Modifier.padding(24.dp)) {
                SearchField(
                    placeholder = "Search by phone number",
                    query = query,
                    onQueryChange = { query = it })

                Box(Modifier.padding(8.dp))

                Row(Modifier.fillMaxWidth()) {
                    HeadTableCell("Actions", 96.dp)
                    HeadTableCell("Phone No", .1f)
                    HeadTableCell("Name", .15f)
                    HeadTableCell("Email", .15f)
                    HeadTableCell("Address", .3f)
                    HeadTableCell("Notes", .3f)
                }
                LazyColumn {
                    items(customersFiltered) { customer ->
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)
                        Row(Modifier.fillMaxWidth()) {
                            Row(Modifier.widthIn(min = 96.dp)) {
                                // FIXME do something
                                PlainTooltipBox("Edit") {
                                    IconButton(onClick = { openEditCustomerDialog = customer.id }) {
                                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                                    }
                                }
                                PlainTooltipBox("History") {
                                    IconButton(onClick = { println("History") }) {
                                        Icon(imageVector = Icons.Filled.History, contentDescription = "History")
                                    }
                                }
                            }
                            TableCell(text = customer.phone, weight = .1f)
                            TableCell(text = customer.name ?: "N/A", weight = .15f)
                            TableCell(text = customer.email ?: "N/A", weight = .15f)
                            TableCell(text = customer.address ?: "N/A", weight = .3f)
                            TableCell(text = customer.notes ?: "N/A", weight = .3f)
                        }
                    }
                }
            }
        }
    }
}
