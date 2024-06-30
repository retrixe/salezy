package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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

@Composable
fun CustomersScreen() {
    var query by remember { mutableStateOf("") }
    val customers by remember { mutableStateOf(TempState.customers) }

    val customersFiltered = customers.filter { it.phone.contains(query, ignoreCase = true) } // FIXME fuzzy search

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Customers", fontSize = 24.sp)
            ExtendedFloatingActionButton(
                onClick = { println("Add Customer") }, // FIXME: Open add item dialog which calls back API
                icon = { Icon(imageVector = Icons.Filled.Add, "Add Customer") },
                text = { Text("Add Customer") }
            )
        }
        Box(Modifier.padding(8.dp))
        Card(Modifier.fillMaxSize()) {
            Column(Modifier.padding(24.dp)) {
                SearchField(query = query, onQueryChange = { query = it })

                Box(Modifier.padding(8.dp))

                // FIXME: Edit, History and Delete options
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
                                IconButton(onClick = { println("Edit") }) {
                                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = { println("Delete") }) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
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
