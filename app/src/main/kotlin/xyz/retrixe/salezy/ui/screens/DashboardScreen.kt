package xyz.retrixe.salezy.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import xyz.retrixe.salezy.state.RemoteSettings
import xyz.retrixe.salezy.ui.screens.dashboard.*

enum class DashboardScreens(val title: String, val icon: ImageVector) {
    POINT_OF_SALE("Point Of Sale", Icons.Filled.PointOfSale),
    TXN_HISTORY("Txn History", Icons.Filled.History),
    INVENTORY("Inventory", Icons.Filled.Inventory),
    CUSTOMERS("Customers", Icons.Filled.People),
    GIFT_CARDS("Gift Cards", Icons.Filled.CardGiftcard),
    REPORTS("Reports", Icons.Filled.QueryStats),
    SETTINGS("Settings", Icons.Filled.Settings),
}

@Composable
fun DashboardScreen(
    logout: () -> Unit,
    setRemoteSettings: (RemoteSettings) -> Unit,
    setTopBar: (Pair<String, (@Composable () -> Unit)>?) -> Unit
) {
    setTopBar(null)

    var screen by remember { mutableStateOf(DashboardScreens.POINT_OF_SALE) }

    Row(Modifier.fillMaxSize()) {
        NavigationRail(Modifier.padding(8.dp)) {
            DashboardScreens.entries.forEach { item ->
                NavigationRailItem(
                    modifier = Modifier.padding(4.dp),
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = screen == item,
                    onClick = { screen = item }
                )
            }
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
                Box(Modifier.wrapContentSize())
                NavigationRailItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout") },
                    label = { Text("Logout") },
                    selected = false,
                    onClick = { logout() }
                )
            }
        }
        VerticalDivider()
        AnimatedContent(targetState = screen) { targetState -> when (targetState) {
            DashboardScreens.POINT_OF_SALE -> PointOfSaleScreen()
            DashboardScreens.TXN_HISTORY -> TxnHistoryScreen()
            DashboardScreens.INVENTORY -> InventoryScreen()
            DashboardScreens.CUSTOMERS -> CustomersScreen()
            DashboardScreens.GIFT_CARDS -> GiftCardsScreen()
            DashboardScreens.REPORTS -> ReportsScreen()
            DashboardScreens.SETTINGS -> SettingsScreen(setRemoteSettings)
        } }
    }
}
