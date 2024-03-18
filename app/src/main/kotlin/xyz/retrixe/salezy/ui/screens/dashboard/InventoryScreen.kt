package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen() {
    var query by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Inventory", fontSize = 24.sp)
        Box(Modifier.padding(8.dp))
        Card(Modifier.fillMaxSize()) {
            Column(Modifier.padding(24.dp)) {
                Row {
                    DockedSearchBar(
                        query = query,
                        onQueryChange = { query = it },
                        onSearch = {},
                        onActiveChange = {},
                        active = false,
                        content = {}
                    )
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Filled.Add, "Create Item")
                    }
                }
                HorizontalDivider()
                // FIXME Show items in a grid
                Text("Hello!")
            }
        }
    }
}
