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
import xyz.retrixe.salezy.api.entities.GiftCard
import xyz.retrixe.salezy.state.TempState
import xyz.retrixe.salezy.ui.components.HeadTableCell
import xyz.retrixe.salezy.ui.components.PlainTooltipBox
import xyz.retrixe.salezy.ui.components.SearchField
import xyz.retrixe.salezy.ui.components.TableCell
import xyz.retrixe.salezy.utils.asDecimal
import xyz.retrixe.salezy.utils.toInstant
import xyz.retrixe.salezy.utils.formatted

@Composable
fun GiftCardsScreen() {
    var query by remember { mutableStateOf("") }
    val giftCards by remember { mutableStateOf<List<GiftCard>?>(TempState.giftCards) }

    // TODO: Server side search
    val giftCardsFiltered = if (query.isNotBlank() && giftCards != null) {
        FuzzySearch.extractSorted(query, giftCards, { it.id }, 60).map { it.referent }
    } else giftCards

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Gift Cards", fontSize = 24.sp)
            if (giftCards != null) ExtendedFloatingActionButton(
                onClick = { println("Create Gift Card") }, // FIXME: Open add item dialog which calls back API
                icon = { Icon(imageVector = Icons.Filled.Add, "Create Gift Card") },
                text = { Text("Create Gift Card") }
            )
        }
        // FIXME search for gift cards based on expired or active?...
        Box(Modifier.padding(8.dp))
        Card(Modifier.fillMaxSize()) {
            Column(Modifier.padding(24.dp)) {
                SearchField(
                    placeholder = "Search by ID",
                    query = query,
                    onQueryChange = { query = it })

                Box(Modifier.padding(8.dp))

                if (giftCardsFiltered == null) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Row(Modifier.fillMaxWidth()) {
                        HeadTableCell("Actions", 96.dp)
                        HeadTableCell("ID", .2f)
                        HeadTableCell("Issued balance", .2f)
                        HeadTableCell("Current balance", .2f)
                        HeadTableCell("Issued on", .2f)
                        HeadTableCell("Expires on", .2f)
                    }
                    // TODO: Pagination
                    LazyColumn {
                        // item {}
                        items(giftCardsFiltered) { item ->
                            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)
                            Row(Modifier.fillMaxWidth()) {
                                Row(Modifier.widthIn(min = 96.dp)) {
                                    // FIXME do something
                                    PlainTooltipBox("Edit") {
                                        IconButton(onClick = { println("Edit") }) {
                                            Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                                        }
                                    }
                                    PlainTooltipBox("History") {
                                        IconButton(onClick = { println("History") }) {
                                            Icon(imageVector = Icons.Filled.History, contentDescription = "History")
                                        }
                                    }
                                }
                                TableCell(text = item.id, weight = .2f)
                                TableCell(text = item.issuedBalance.asDecimal(), weight = .2f)
                                TableCell(text = item.currentBalance.asDecimal(), weight = .2f)
                                TableCell(text = item.issuedOn.toInstant().formatted(), weight = .2f)
                                TableCell(text = item.expiresOn.toInstant().formatted(), weight = .2f)
                            }
                        }
                    }
                }
            }
        }
    }
}
