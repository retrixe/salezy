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
import xyz.retrixe.salezy.ui.components.PlainTooltipBox
import xyz.retrixe.salezy.ui.components.SearchField
import xyz.retrixe.salezy.ui.components.TableCell
import xyz.retrixe.salezy.utils.asDecimal
import xyz.retrixe.salezy.utils.formatted
import xyz.retrixe.salezy.utils.toInstant
import me.xdrop.fuzzywuzzy.FuzzySearch
import xyz.retrixe.salezy.api.entities.Invoice

// FIXME: Remove TempState references
@Composable
fun TxnHistoryScreen() {
    var query by remember { mutableStateOf("") }
    val invoices by remember { mutableStateOf<List<Invoice>?>(TempState.invoices) }

    // TODO: Server side search
    val invoicesFiltered = if (query.isNotBlank() && invoices != null) {
        FuzzySearch
            .extractSorted(query, invoices, { "${it.id} ${it.customerId}" }, 60)
            .map { it.referent }
    } else invoices

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
                    placeholder = "Search by ID or customer ID",
                    query = query,
                    onQueryChange = { query = it })

                Box(Modifier.padding(8.dp))

                if (invoicesFiltered == null) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Row(Modifier.fillMaxWidth()) {
                        HeadTableCell("ID", .2f)
                        HeadTableCell("Customer ID", .2f)
                        HeadTableCell("Date/time", .2f)
                        HeadTableCell("Cost (pre-tax)", .15f)
                        HeadTableCell("Cost (post-tax)", .15f)
                        HeadTableCell("Item qty", .1f)
                        HeadTableCell("Details", 80.dp)
                    }
                    // TODO: Pagination
                    LazyColumn {
                        // item {}
                        items(invoicesFiltered) { invoice ->
                            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)
                            Row(Modifier.fillMaxWidth()) {
                                TableCell(text = invoice.id.toString(), weight = .2f)
                                TableCell(text = invoice.customerId.toString(), weight = .2f)
                                TableCell(text = invoice.issuedOn.toInstant().formatted(), weight = .2f)
                                TableCell(text = "\$${invoice.costPreTax.asDecimal()}", weight = .15f)
                                TableCell(text = "\$${invoice.costPostTax.asDecimal()}", weight = .15f)
                                TableCell(text = invoice.items.size.toString(), weight = .1f)
                                // FIXME show details
                                Row(Modifier.widthIn(80.dp)) {
                                    PlainTooltipBox("Info") {
                                        IconButton(onClick = { println("Info") }) {
                                            Icon(
                                                imageVector = Icons.Filled.ArrowCircleRight,
                                                contentDescription = "Info"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
