package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.retrixe.salezy.state.TempState

// FIXME: Replace with actual API call

@Composable
fun ReportsScreen() {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Reports", fontSize = 24.sp)
        }
        Box(Modifier.padding(8.dp))
        OutlinedCard(Modifier.fillMaxSize()) {
            // FIXME actual data and actual timeframe....
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FilterChip(label = { Text("Last week") }, selected = false, onClick = {})
                FilterChip(label = { Text("Last month") }, selected = true, onClick = {})
                FilterChip(label = { Text("Last 3 months") }, selected = false, onClick = {})
                FilterChip(label = { Text("Custom") }, selected = false, onClick = {})
            }
            LazyVerticalGrid(
                modifier = Modifier.padding(16.dp),
                columns = GridCells.Adaptive(minSize = 280.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item { Card { Column(Modifier.padding(16.dp)) {
                    Text("Net Revenue (excl tax)", fontSize = 24.sp)
                    Text(
                        "$${TempState.invoices
                            .fold(0L) { acc, invoice -> acc + invoice.beforeTaxCost }}",
                        fontSize = 40.sp)
                } } }
                item { Card { Column(Modifier.padding(16.dp)) {
                    Text("Total Items Sold", fontSize = 24.sp)
                    Text(TempState.invoices
                        .fold(0L) { acc, invoice -> acc + invoice.items
                            .fold(0) { acc2, item -> acc2 + item.count } }.toString(),
                        fontSize = 40.sp)
                } } }
                item { Card { Column(Modifier.padding(16.dp)) {
                    Text("Most Sold Item", fontSize = 24.sp)
                    Text("N/A", fontSize = 40.sp)
                } } }
            }
            Card(Modifier.fillMaxSize().padding(16.dp)) {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { Text("Graph here (WIP)") }
            }
        }
    }
}
