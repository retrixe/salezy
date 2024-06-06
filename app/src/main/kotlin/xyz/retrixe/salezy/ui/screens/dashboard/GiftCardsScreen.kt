package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.foundation.background
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
import xyz.retrixe.salezy.api.entities.GiftCard
import xyz.retrixe.salezy.ui.components.SearchField
import java.time.Instant

// FIXME: Replace with actual API call
val giftCards = listOf(
    GiftCard("EUI283HUFVEU", 10, 10, 0, 0),
    GiftCard("EUI283HUFVEU", 20, 20, 0, 0),
    GiftCard("EUI283HUFVEU", 30, 30, 0, 0),
    GiftCard("EUI283HUFVEU", 40, 40, 0, 0),
    GiftCard("EUI283HUFVEU", 50, 50, 0, 0),
    GiftCard("EUI283HUFVEU", 60, 60, 0, 0),
    GiftCard("EUI283HUFVEU", 70, 70, 0, 0),
    GiftCard("EUI283HUFVEU", 80, 80, 0, 0),
    GiftCard("EUI283HUFVEU", 90, 90, 0, 0)
)

@Composable
fun RowScope.HeadTableCell(text: String, weight: Float) =
    Text(text = text, Modifier
        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp))
        .weight(weight)
        .padding(8.dp))

@Composable
fun RowScope.TableCell(text: String, weight: Float) =
    Text(text = text, Modifier.weight(weight).padding(8.dp))

@Composable
fun GiftCardsScreen() {
    var query by remember { mutableStateOf("") }
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
        Box(Modifier.padding(8.dp))
        Card(Modifier.fillMaxSize()) {
            Column(Modifier.padding(24.dp)) {
                SearchField(query = query, onQueryChange = { query = it })

                Box(Modifier.padding(8.dp))

                // FIXME: Edit, History and Delete options
                Row(Modifier.fillMaxWidth()) {
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
                                IconButton(onClick = { println("Delete") }) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
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
