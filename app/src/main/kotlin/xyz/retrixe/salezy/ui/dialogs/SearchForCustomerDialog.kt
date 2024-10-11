package xyz.retrixe.salezy.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import me.xdrop.fuzzywuzzy.FuzzySearch
import xyz.retrixe.salezy.api.Api
import xyz.retrixe.salezy.api.entities.Customer
import xyz.retrixe.salezy.state.LocalSnackbarHostState
import xyz.retrixe.salezy.ui.components.HeadTableCell
import xyz.retrixe.salezy.ui.components.PlainTooltipBox
import xyz.retrixe.salezy.ui.components.SearchField
import xyz.retrixe.salezy.ui.components.TableCell

@Composable
fun SearchForCustomerDialog(
    open: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (Customer) -> Unit
) {
    val snackbarHostState = LocalSnackbarHostState.current

    var query by remember { mutableStateOf("") }
    var customers by remember { mutableStateOf<List<Customer>?>(null) }
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }

    LaunchedEffect(true) {
        customers = null
        selectedCustomer = null
        query = ""
        try {
            customers = Api.getCustomers().toList()
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar(
                message = "Failed to load customers! ${e.message}",
                actionLabel = "Hide",
                duration = SnackbarDuration.Indefinite)
        }
    }

    fun onSave() {
        selectedCustomer?.let { onSubmit(it) }
        onDismiss()
    }

    // TODO: Server side search
    val customersFiltered = if (customers != null && query.isNotBlank()) {
        FuzzySearch
            .extractSorted(query, customers, { "${it.id} ${it.phone} ${it.name}" }, 60)
            .map { it.referent }
    } else customers

    AnimatedVisibility(open) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier.height(640.dp).width(960.dp).padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Search for Customer", fontSize = 28.sp)
                    Box(Modifier.height(16.dp))

                    SearchField(
                        placeholder = "Search by name, ID or phone number",
                        query = query,
                        onQueryChange = { query = it })
                    Box(Modifier.height(16.dp))

                    if (customersFiltered == null) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Row(Modifier.fillMaxWidth()) {
                            HeadTableCell("", 40.dp)
                            HeadTableCell("Phone No", .15f)
                            HeadTableCell("Name", .15f)
                            HeadTableCell("Email", .15f)
                            HeadTableCell("Address", .3f)
                            HeadTableCell("Notes", .25f)
                        }
                        // TODO: Pagination
                        LazyColumn(Modifier.weight(1f)) {
                            items(customersFiltered) { customer ->
                                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)
                                Row(Modifier.fillMaxWidth()) {
                                    Row(Modifier.widthIn(40.dp)) {
                                        PlainTooltipBox("Select Customer") {
                                            RadioButton(
                                                selected = customer == selectedCustomer,
                                                onClick = { selectedCustomer = customer })
                                        }
                                    }
                                    TableCell(text = customer.phone, weight = .15f)
                                    TableCell(text = customer.name ?: "N/A", weight = .15f)
                                    TableCell(text = customer.email ?: "N/A", weight = .15f)
                                    TableCell(text = customer.address ?: "N/A", weight = .3f)
                                    TableCell(text = customer.notes ?: "N/A", weight = .25f)
                                }
                            }
                        }
                    }

                    Box(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(), //.padding(vertical = 12.dp, horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        FilledTonalButton(onClick = { onDismiss() }) { Text("Cancel") }
                        Button(onClick = ::onSave, enabled = selectedCustomer != null) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}
