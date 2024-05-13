package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.retrixe.salezy.api.entities.GiftCard
import xyz.retrixe.salezy.ui.components.SearchField

// FIXME: Replace with actual API call
val giftCards = listOf(
    GiftCard("Vase", 10.0),
    GiftCard("cHOROCOLATE", 20.0),
    GiftCard("THING", 30.0),
    GiftCard("minecrasft", 40.0),
    GiftCard("house", 50.0),
    GiftCard("life", 60.0),
    GiftCard("nextrjs", 70.0),
    GiftCard("jwt", 80.0),
    GiftCard("Phone",  90.0)
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
        }
        Box(Modifier.padding(8.dp))
        Card(Modifier.fillMaxSize()) {
            Column(Modifier.padding(24.dp)) {
                SearchField(query = query, onQueryChange = { query = it })

                Box(Modifier.padding(8.dp))

                LazyColumn {
                    item {
                        Row(Modifier.fillMaxWidth()) {
                            HeadTableCell("ID", .7f)
                            HeadTableCell("Balance", .3f)
                        }
                    }
                    items(giftCardsFiltered) { item ->
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)
                        Row(Modifier.fillMaxWidth()) {
                            TableCell(text = item.id, weight = .7f)
                            TableCell(text = item.balance.toString(), weight = .3f)
                        }
                    }
                }
            }
        }
    }
}
