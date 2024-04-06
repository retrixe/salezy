package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen() {
    var query by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Inventory", fontSize = 24.sp)
            ExtendedFloatingActionButton(
                onClick = { println("Add item") }, // FIXME
                icon = { Icon(imageVector = Icons.Filled.Add, "Add Item") },
                text = { Text("Add Item") }
            )
        }
        Box(Modifier.padding(8.dp))
        Card(Modifier.fillMaxSize()) {
            Column(Modifier.padding(24.dp)) {
                DockedSearchBar(query = query, onQueryChange = { query = it },
                    active = false, onActiveChange = {}, // No search result popout
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search") },
                    leadingIcon = { Icon(imageVector = Icons.Filled.Search, "Search") },
                    onSearch = { println("Perform search!") } // FIXME
                ) {}
                Box(Modifier.padding(8.dp))
                // FIXME Show items in a grid
                LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 200.dp)) {
                    items(9) { index ->
                        Card(Modifier.padding(8.dp)) {
                            Column(Modifier.padding(8.dp)) {
                                Text("Item $index")
                                Text("Description of item $index")
                            }
                        }
                    }
                }
            }
        }
    }
}
