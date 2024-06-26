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
import xyz.retrixe.salezy.state.TempState
import xyz.retrixe.salezy.ui.components.HeadTableCell
import xyz.retrixe.salezy.ui.components.SearchField
import xyz.retrixe.salezy.ui.components.TableCell
import xyz.retrixe.salezy.ui.dialogs.AddEditCustomerDialog

@Composable
fun CustomersScreen() {
    var query by remember { mutableStateOf("") }
    var customers by remember { mutableStateOf(TempState.customers) }

    val customersFiltered = customers.filter { it.phone.contains(query, ignoreCase = true) } // FIXME fuzzy search

    var openNewCustomerDialog by remember { mutableStateOf(false) }
    AddEditCustomerDialog(
        open = openNewCustomerDialog,
        label = "Add New Customer",
        initialValue = null,
        onDismiss = { openNewCustomerDialog = false },
        onSubmit = { TempState.customers.add(it); customers = TempState.customers })

    var openEditCustomerDialog by remember { mutableStateOf<Int?>(null) }
    if (openEditCustomerDialog != null) AddEditCustomerDialog( // FIXME ugh
        open = true,
        label = "Edit Item",
        initialValue = customers.find { it.id == openEditCustomerDialog },
        onDismiss = { openEditCustomerDialog = null },
        onSubmit = { TempState.customers[TempState.customers.indexOfFirst {
            customer -> customer.id == openEditCustomerDialog
        }] = it })

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
                    // FIXME: Actions should have max width
                    HeadTableCell("Actions", .15f)
                    HeadTableCell("Phone No", .15f)
                    HeadTableCell("Name", .15f)
                    HeadTableCell("Email", .15f)
                    HeadTableCell("Address", .2f)
                    HeadTableCell("Notes", .2f)
                }
                LazyColumn {
                    // item {}
                    items(customersFiltered) { customer ->
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)
                        Row(Modifier.fillMaxWidth()) {
                            Row(Modifier.weight(.15f)) {
                                // FIXME do something
                                IconButton(onClick = { openEditCustomerDialog = customer.id }) {
                                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = { println("History") }) {
                                    Icon(imageVector = Icons.Filled.History, contentDescription = "History")
                                }
                            }
                            TableCell(text = customer.phone, weight = .15f)
                            // FIXME truncate if too long
                            TableCell(text = customer.name ?: "N/A", weight = .15f)
                            TableCell(text = customer.email ?: "N/A", weight = .15f)
                            TableCell(text = customer.address ?: "N/A", weight = .2f)
                            TableCell(text = customer.notes ?: "N/A", weight = .2f)
                        }
                    }
                }
            }
        }
    }
}
