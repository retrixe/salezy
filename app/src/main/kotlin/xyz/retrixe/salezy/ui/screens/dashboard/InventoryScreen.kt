package xyz.retrixe.salezy.ui.screens.dashboard

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import xyz.retrixe.salezy.state.LocalSnackbarHostState
import xyz.retrixe.salezy.state.TempState
import xyz.retrixe.salezy.ui.components.SearchField
import xyz.retrixe.salezy.ui.dialogs.AddEditItemDialog

@Composable
fun InventoryScreen() {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current

    var query by remember { mutableStateOf("") }
    var inventoryItems by remember { mutableStateOf(TempState.inventoryItems) }

    val inventoryItemsFiltered = inventoryItems.filter {
        it.name.contains(query, ignoreCase = true) ||
                it.upc.toString().contains(query, ignoreCase = true)
    } // FIXME fuzzy search

    // FIXME single dialog call would be nice.
    var openNewItemDialog by remember { mutableStateOf(false) }
    AddEditItemDialog(
        open = openNewItemDialog,
        label = "Add New Item",
        initialValue = null,
        onDismiss = { openNewItemDialog = false },
        onSubmit = { TempState.inventoryItems.add(it); inventoryItems = TempState.inventoryItems })

    var openEditItemDialog by remember { mutableStateOf<Long?>(null) }
    if (openEditItemDialog != null) AddEditItemDialog( // FIXME ugh
        open = true,
        label = "Edit Item",
        initialValue = inventoryItems.find { it.upc == openEditItemDialog },
        onDismiss = { openEditItemDialog = null },
        onSubmit = { TempState.inventoryItems[TempState.inventoryItems.indexOfFirst {
            item -> item.upc == openEditItemDialog
        }] = it })

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Inventory", fontSize = 24.sp)
            ExtendedFloatingActionButton(
                onClick = { openNewItemDialog = true },
                icon = { Icon(imageVector = Icons.Filled.Add, "Add Item") },
                text = { Text("Add Item") }
            )
        }
        Box(Modifier.padding(8.dp))
        Card(Modifier.fillMaxSize()) {
            Column(Modifier.padding(24.dp)) {
                SearchField(
                    placeholder = "Search by name or UPC",
                    query = query,
                    onQueryChange = { query = it })

                Box(Modifier.padding(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) { items(inventoryItemsFiltered) { item ->
                    ElevatedCard(elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                        Column(Modifier.padding(8.dp).fillMaxWidth().clickable {
                            openEditItemDialog = item.upc
                        }) {
                            Column(Modifier.padding(8.dp)) {
                                Text(item.name, fontSize = 20.sp)
                                Text("UPC ${item.upc}", fontSize = 20.sp)
                                Text("$${item.price} | ${item.quantity} in stock")
                            }
                            if (item.imageUrl != null) KamelImage(
                                modifier = Modifier.size(160.dp)
                                    .align(Alignment.CenterHorizontally),
                                contentDescription = item.name,
                                resource = asyncPainterResource(data = item.imageUrl),
                                animationSpec = tween(),
                                onLoading = { _ -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                                    CircularProgressIndicator()
                                } },
                                onFailure = { exception -> coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = exception.message.toString(),
                                        actionLabel = "Hide",
                                        duration = SnackbarDuration.Short
                                    )
                                } }
                            ) else Box(Modifier.size(160.dp))
                        }
                    }
                } }
            }
        }
    }
}
