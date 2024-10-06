package xyz.retrixe.salezy.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.vinceglb.filekit.core.*
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import xyz.retrixe.salezy.api.Api
import xyz.retrixe.salezy.api.entities.InventoryItem
import xyz.retrixe.salezy.api.entities.ephemeral.EphemeralInventoryItem
import xyz.retrixe.salezy.state.LocalSnackbarHostState
import xyz.retrixe.salezy.utils.asDecimal
import xyz.retrixe.salezy.utils.toBase64String
import xyz.retrixe.salezy.utils.toDecimalLong

// TODO (low priority): Dedup add/edit dialog calls
@Composable
fun AddEditItemDialog(
    open: Boolean,
    label: String,
    initialValue: InventoryItem?,
    onDismiss: () -> Unit,
    onSubmit: (InventoryItem) -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current

    var name by remember { mutableStateOf(Pair("", "")) }
    var image by remember { mutableStateOf<PlatformFile?>(null) }
    var upc by remember { mutableStateOf(Pair("", "")) }
    var sku by remember { mutableStateOf(Pair("", "")) }
    var costPrice by remember { mutableStateOf(Pair("", "")) }
    var sellingPrice by remember { mutableStateOf(Pair("", "")) }
    var quantity by remember { mutableStateOf(Pair("", "")) }

    LaunchedEffect(open) {
        name = Pair(initialValue?.name ?: "", "")
        image = null
        upc = Pair(initialValue?.upc?.toString() ?: "", "")
        sku = Pair(initialValue?.sku ?: "", "")
        costPrice = Pair(initialValue?.costPrice?.asDecimal() ?: "", "")
        sellingPrice = Pair(initialValue?.sellingPrice?.asDecimal() ?: "", "")
        quantity = Pair(initialValue?.quantity?.toString() ?: "", "")
    }

    fun selectImage() = scope.launch {
        image = FileKit.pickFile(PickerType.Image, PickerMode.Single, "Pick an image")
    }

    fun onSave() = scope.launch {
        if (name.first.isBlank()) name = Pair(name.first, "No name provided!")
        if (upc.first.isBlank()) upc = Pair(upc.first, "No UPC provided!")
        if (sku.first.isBlank()) sku = Pair(sku.first, "No SKU provided!")
        if (costPrice.first.isBlank()) costPrice = Pair(costPrice.first, "No cost price provided!")
        if (sellingPrice.first.isBlank()) sellingPrice = Pair(sellingPrice.first, "No selling price provided!")
        if (quantity.first.isBlank()) quantity = Pair(quantity.first, "No quantity provided!")
        val ephemeralInventoryItem = EphemeralInventoryItem(
            if (name.second.isEmpty()) name.first else return@launch,
            initialValue?.imageId,
            image?.file?.readBytes()?.toBase64String(),
            if (upc.second.isEmpty()) upc.first.toLong() else return@launch,
            if (sku.second.isEmpty()) sku.first else return@launch,
            if (costPrice.second.isEmpty()) costPrice.first.toDecimalLong() else return@launch,
            if (sellingPrice.second.isEmpty()) sellingPrice.first.toDecimalLong() else return@launch,
            if (quantity.second.isEmpty()) quantity.first.toInt() else return@launch,
        )
        try {
            val inventoryItem =
                if (initialValue == null) Api.postInventoryItem(ephemeralInventoryItem)
                else Api.patchInventoryItem(initialValue.upc, ephemeralInventoryItem)
            onSubmit(inventoryItem)
            onDismiss()
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar(
                message = "Failed to save item! ${e.message}",
                actionLabel = "Hide",
                duration = SnackbarDuration.Long)
        }
    }

    AnimatedVisibility(open) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier.wrapContentSize().width(360.dp + 32.dp + 240.dp + (24.dp * 2)),
                shape = RoundedCornerShape(16.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    Text(label, fontSize = 28.sp, modifier = Modifier.padding(vertical = 16.dp))
                }
                Row(
                    Modifier.padding(vertical = 4.dp, horizontal = 24.dp).height(IntrinsicSize.Min)
                ) {
                    Column(
                        Modifier.width(360.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = name.first, onValueChange = { name = Pair(it, "") },
                            label = { Text("Name*") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            isError = name.second.isNotEmpty(),
                            supportingText = if (name.second.isNotEmpty()) {
                                @Composable { Text(name.second) }
                            } else null
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = upc.first,
                            onValueChange = {
                                if (it.isEmpty() || it.toLongOrNull() != null) upc = Pair(it, "")
                            },
                            label = { Text("UPC*") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = upc.second.isNotEmpty(),
                            supportingText = if (upc.second.isNotEmpty()) {
                                @Composable { Text(upc.second) }
                            } else null
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = sku.first, onValueChange = { sku = Pair(it, "") },
                            label = { Text("SKU*") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            isError = sku.second.isNotEmpty(),
                            supportingText = if (sku.second.isNotEmpty()) {
                                @Composable { Text(sku.second) }
                            } else null
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = costPrice.first,
                            onValueChange = {
                                val conv = it.toBigDecimalOrNull()
                                if (it.isEmpty() || (conv != null && conv.scale() <= 2)) {
                                    costPrice = Pair(it, "")
                                }
                            },
                            label = { Text("Cost Price*") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = costPrice.second.isNotEmpty(),
                            supportingText = if (costPrice.second.isNotEmpty()) {
                                @Composable { Text(costPrice.second) }
                            } else null
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = sellingPrice.first,
                            onValueChange = {
                                val conv = it.toBigDecimalOrNull()
                                if (it.isEmpty() || (conv != null && conv.scale() <= 2)) {
                                    sellingPrice = Pair(it, "")
                                }
                            },
                            label = { Text("Selling Price*") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = sellingPrice.second.isNotEmpty(),
                            supportingText = if (sellingPrice.second.isNotEmpty()) {
                                @Composable { Text(sellingPrice.second) }
                            } else null
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = quantity.first,
                            onValueChange = {
                                if (it.isEmpty() || it.toIntOrNull() != null) quantity = Pair(it, "")
                            },
                            label = { Text("Quantity*") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = quantity.second.isNotEmpty(),
                            supportingText = if (quantity.second.isNotEmpty()) {
                                @Composable { Text(quantity.second) }
                            } else null
                        )
                    }
                    VerticalDivider(Modifier.padding(horizontal = 16.dp))
                    Column(
                        Modifier.width(240.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Image", Modifier.padding(vertical =  4.dp), fontSize = 20.sp)
                        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = ::selectImage) {
                            Icon(Icons.Filled.Upload, "Upload")
                            Text("Select Image")
                        }
                        val imageFile = image?.file
                        val resource =
                            if (imageFile != null)
                                asyncPainterResource(data = imageFile)
                            else if (initialValue?.imageId != null)
                                asyncPainterResource(data = Api.getAssetUrl(initialValue.imageId))
                            else null
                        if (resource == null) Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text("No image selected!")
                        } else {
                            KamelImage(
                                modifier = Modifier.size(240.dp).align(Alignment.CenterHorizontally),
                                contentDescription = "Item Image",
                                resource = resource,
                                animationSpec = tween(),
                                onLoading = { _ -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                                    CircularProgressIndicator()
                                } },
                                onFailure = { ex -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                                    Text("Failed to load image!")
                                    ex.printStackTrace()
                                } }
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    FilledTonalButton(onClick = { onDismiss() }) { Text("Cancel") }
                    Button(onClick = ::onSave) { Text("Save") }
                }
            }
        }
    }
}
