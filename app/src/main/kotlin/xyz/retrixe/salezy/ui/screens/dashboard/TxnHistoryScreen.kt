package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import java.time.Instant

@Composable
fun TxnHistoryScreen() {
    var query by remember { mutableStateOf("") }
    val invoices by remember { mutableStateOf(TempState.invoices) }

    val invoicesFiltered = invoices.filter { it.id.toString().contains(query, ignoreCase = true) } // FIXME fuzzy search

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Invoice History", fontSize = 24.sp)
        }
        Box(Modifier.padding(8.dp))
        Card(Modifier.fillMaxSize()) {
            Column(Modifier.padding(24.dp)) {
                SearchField(
                    placeholder = "Search by ID",
                    query = query,
                    onQueryChange = { query = it })

                Box(Modifier.padding(8.dp))

                Row(Modifier.fillMaxWidth()) {
                    HeadTableCell("ID", .15f)
                    HeadTableCell("Customer ID", .15f)
                    HeadTableCell("Date", .23f)
                    HeadTableCell("Cost (pre-tax)", .15f)
                    HeadTableCell("Cost (post-tax)", .15f)
                    HeadTableCell("Item qty", .1f)
                    HeadTableCell("Details", .07f)
                }
                LazyColumn {
                    // item {}
                    items(invoicesFiltered) { invoice ->
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)
                        Row(Modifier.fillMaxWidth()) {
                            TableCell(text = invoice.id.toString(), weight = .15f)
                            TableCell(text = invoice.customerId.toString(), weight = .15f)
                            TableCell(text = Instant.ofEpochMilli(invoice.issuedOn).toString(), weight = .23f)
                            TableCell(text = "\$${invoice.beforeTaxCost}", weight = .15f)
                            TableCell(text = "\$${invoice.afterTaxCost}", weight = .15f)
                            TableCell(text = invoice.items.size.toString(), weight = .1f)
                            // FIXME show details
                            Row(Modifier.weight(.07f)) {
                                IconButton(onClick = { println("Info") }) {
                                    Icon(imageVector = Icons.Filled.ArrowCircleRight, contentDescription = "Info")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
