package xyz.retrixe.salezy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(setTopBar: (Pair<String, (@Composable () -> Unit)>?) -> Unit) {
    setTopBar(null)

    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        Pair("Point Of Sale", Icons.Filled.PointOfSale),
        Pair("Inventory", Icons.Filled.Inventory),
        Pair("Customers", Icons.Filled.People),
        Pair("Gift Cards", Icons.Filled.CardGiftcard),
        Pair("Settings", Icons.Filled.Settings),
    )

    // FIXME improve padding between items, add rounded background
    Row(Modifier.fillMaxSize()) {
        NavigationRail(Modifier.padding(8.dp)) {
            items.forEachIndexed { index, item ->
                NavigationRailItem(
                    icon = { Icon(item.second, contentDescription = item.first) },
                    label = { Text(item.first) },
                    selected = selectedItem == index,
                    onClick = { selectedItem = index }
                )
            }
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
                Box(Modifier.wrapContentSize())
                NavigationRailItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout") },
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {}
                )
            }
        }
    }
}
