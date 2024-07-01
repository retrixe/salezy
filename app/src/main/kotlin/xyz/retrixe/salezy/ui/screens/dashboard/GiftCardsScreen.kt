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
import java.time.Instant

@Composable
fun GiftCardsScreen() {
    var query by remember { mutableStateOf("") }
    val giftCards by remember { mutableStateOf(TempState.giftCards) }

    val giftCardsFiltered = giftCards.filter { it.id.contains(query, ignoreCase = true) } // FIXME fuzzy search

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Gift Cards", fontSize = 24.sp)
            ExtendedFloatingActionButton(
                onClick = { println("Create Gift Card") }, // FIXME: Open add item dialog which calls back API
                icon = { Icon(imageVector = Icons.Filled.Add, "Create Gift Card") },
                text = { Text("Create Gift Card") }
            )
        }
        // FIXME search for gift cards based on expired or active?...
        Box(Modifier.padding(8.dp))
        Card(Modifier.fillMaxSize()) {
            Column(Modifier.padding(24.dp)) {
                SearchField(query = query, onQueryChange = { query = it })

                Box(Modifier.padding(8.dp))

                Row(Modifier.fillMaxWidth()) {
                    // FIXME: Actions should have max width
                    HeadTableCell("Actions", .15f)
                    HeadTableCell("ID", .15f)
                    HeadTableCell("Issued balance", .15f)
                    HeadTableCell("Current balance", .15f)
                    HeadTableCell("Issued on", .2f)
                    HeadTableCell("Expires on", .2f)
                }
                LazyColumn {
                    // item {}
                    items(giftCardsFiltered) { item ->
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)
                        Row(Modifier.fillMaxWidth()) {
                            Row(Modifier.weight(.15f)) {
                                // FIXME do something
                                IconButton(onClick = { println("Edit") }) {
                                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = { println("History") }) {
                                    Icon(imageVector = Icons.Filled.History, contentDescription = "History")
                                }
                            }
                            TableCell(text = item.id, weight = .15f)
                            // FIXME long to decimal
                            TableCell(text = item.issuedBalance.toString(), weight = .15f)
                            TableCell(text = item.currentBalance.toString(), weight = .15f)
                            // FIXME better formatting
                            TableCell(text = Instant.ofEpochMilli(item.issuedOn).toString(), weight = .2f)
                            TableCell(text = Instant.ofEpochMilli(item.expiresOn).toString(), weight = .2f)
                        }
                    }
                }
            }
        }
    }
}
