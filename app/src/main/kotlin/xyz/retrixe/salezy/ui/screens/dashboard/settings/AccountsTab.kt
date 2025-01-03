package xyz.retrixe.salezy.ui.screens.dashboard.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import xyz.retrixe.salezy.api.Api
import xyz.retrixe.salezy.state.LocalSnackbarHostState
import xyz.retrixe.salezy.ui.components.PasswordTextField
import xyz.retrixe.salezy.ui.components.PlainTooltipBox
import xyz.retrixe.salezy.ui.components.TableCell

// TODO: Split out the dialogs into their own files
@Composable
fun AccountsTab() {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val accounts = remember { mutableStateListOf<String>() }
    var createAccount by remember { mutableStateOf<Pair<String, String>?>(null) }
    var changePassword by remember { mutableStateOf<Pair<String, String>?>(null) }
    var renameAccount by remember { mutableStateOf<Pair<String, String>?>(null) }
    var deleteAccount by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(true) {
        try {
            accounts.addAll(Api.getAccounts())
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar(
                message = "Failed to load accounts! ${e.message}",
                actionLabel = "Hide",
                duration = SnackbarDuration.Indefinite)
        }
    }

    if (accounts.isEmpty()) return LinearProgressIndicator(Modifier.fillMaxWidth())

    fun onCreateAccount() = coroutineScope.launch {
        val data = createAccount ?: return@launch
        createAccount = null
        try {
            Api.postAccount(Api.PostAccountRequestBody(data.first, data.second))
            accounts.add(data.first)
            snackbarHostState.showSnackbar(
                message = "Successfully created account \"${data.first}\"!",
                actionLabel = "Hide",
                duration = SnackbarDuration.Long)
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar(
                message = "Failed to create account \"${data.first}\"! ${e.message}",
                actionLabel = "Hide",
                duration = SnackbarDuration.Indefinite)
        }
    }

    fun onChangePassword() = coroutineScope.launch {
        val data = changePassword ?: return@launch
        changePassword = null
        try {
            Api.patchAccount(data.first, Api.PatchAccountRequestBody(password = data.second))
            snackbarHostState.showSnackbar(
                message = "Successfully changed password of \"${data.first}\"!",
                actionLabel = "Hide",
                duration = SnackbarDuration.Long)
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar(
                message = "Failed to change password of \"${data.first}\"! ${e.message}",
                actionLabel = "Hide",
                duration = SnackbarDuration.Indefinite)
        }
    }

    fun onRenameAccount() = coroutineScope.launch {
        val data = renameAccount ?: return@launch
        renameAccount = null
        try {
            Api.patchAccount(data.first, Api.PatchAccountRequestBody(username = data.second))
            accounts[accounts.indexOf(data.first)] = data.second
            snackbarHostState.showSnackbar(
                "Successfully renamed account from \"${data.first}\" to \"${data.second}\"!",
                actionLabel = "Hide",
                duration = SnackbarDuration.Long)
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar(
                message = "Failed to rename account \"${data.first}\"! ${e.message}",
                actionLabel = "Hide",
                duration = SnackbarDuration.Indefinite)
        }
    }

    fun onDeleteAccount() = coroutineScope.launch {
        val data = deleteAccount ?: return@launch
        deleteAccount = null
        try {
            Api.deleteAccount(data)
            accounts.remove(data)
            snackbarHostState.showSnackbar(
                message = "Successfully deleted account \"${data}\"!",
                actionLabel = "Hide",
                duration = SnackbarDuration.Long)
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar(
                message = "Failed to delete account \"${data}\"! ${e.message}",
                actionLabel = "Hide",
                duration = SnackbarDuration.Indefinite)
        }
    }

    AnimatedVisibility(createAccount != null) {
        Dialog(onDismissRequest = { createAccount = null }) {
            Card(
                modifier = Modifier.wrapContentSize().width(420.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp)
                ) {
                    val data = createAccount ?: Pair("", "")
                    Text(
                        "Create New Account",
                        fontSize = 28.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    // TODO: Could pull more things from the original login screen
                    OutlinedTextField(
                        value = data.first,
                        onValueChange = { createAccount = Pair(it, data.second) },
                        label = { Text("Username") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth())

                    Spacer(Modifier.height(8.dp))

                    PasswordTextField(
                        value = data.second,
                        onValueChange = { createAccount = Pair(data.first, it) },
                        label = { Text("Password") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        keyboardActions = KeyboardActions(onDone = {}),
                        modifier = Modifier.fillMaxWidth())

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        FilledTonalButton(onClick = { createAccount = null }) { Text("Cancel") }
                        Button(onClick = ::onCreateAccount) { Text("Create") }
                    }
                }
            }
        }
    }

    AnimatedVisibility(changePassword != null) {
        Dialog(onDismissRequest = { changePassword = null }) {
            Card(
                modifier = Modifier.wrapContentSize().width(420.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp)
                ) {
                    val data = changePassword ?: Pair("", "")
                    Text(
                        "Change Password of \"${data.first}\"",
                        fontSize = 28.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    PasswordTextField(
                        modifier = Modifier.fillMaxWidth()
                            .onKeyEvent {
                                // KeyDown is bust on Linux
                                if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                                    onChangePassword()
                                    true
                                } else false
                            },
                        value = data.second,
                        onValueChange = { changePassword = Pair(data.first, it) },
                        label = { Text("New Password") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        keyboardActions = KeyboardActions(onNext = { onChangePassword() }),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        FilledTonalButton(onClick = { changePassword = null }) { Text("Cancel") }
                        Button(onClick = ::onChangePassword) { Text("Change Password") }
                    }
                }
            }
        }
    }

    AnimatedVisibility(renameAccount != null) {
        Dialog(onDismissRequest = { renameAccount = null }) {
            Card(
                modifier = Modifier.wrapContentSize().width(420.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp)
                ) {
                    val data = renameAccount ?: Pair("", "")
                    Text(
                        "Rename Account \"${data.first}\"",
                        fontSize = 28.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth()
                            .onKeyEvent {
                                // KeyDown is bust on Linux
                                if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                                    onRenameAccount()
                                    true
                                } else false
                            },
                        value = data.second,
                        onValueChange = { renameAccount = Pair(data.first, it) },
                        label = { Text("New Username") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        keyboardActions = KeyboardActions(onNext = { onRenameAccount() }),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        FilledTonalButton(onClick = { renameAccount = null }) { Text("Cancel") }
                        Button(onClick = ::onRenameAccount) { Text("Rename") }
                    }
                }
            }
        }
    }

    AnimatedVisibility(deleteAccount != null) {
        Dialog(onDismissRequest = { deleteAccount = null }) {
            Card(
                modifier = Modifier.wrapContentSize().width(420.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp)
                ) {
                    Text(
                        "Are you sure you want to delete \"$deleteAccount\"?",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        FilledTonalButton(onClick = { deleteAccount = null }) { Text("No") }
                        Button(onClick = ::onDeleteAccount) { Text("Yes") }
                    }
                }
            }
        }
    }

    Column(Modifier.verticalScroll(rememberScrollState())) {
        Button(modifier = Modifier.padding(8.dp), onClick = { createAccount = Pair("", "") }) {
            Icon(Icons.Default.Add, "Create Account")
            Text("Create Account")
        }
        accounts.map { username ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = username,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                TableCell(username, 1f)
                Row {
                    PlainTooltipBox("Rename Account") {
                        IconButton(onClick = { renameAccount = Pair(username, "") }) {
                            Icon(
                                imageVector = Icons.Filled.DriveFileRenameOutline,
                                contentDescription = "Rename Account"
                            )
                        }
                    }
                    PlainTooltipBox("Change Password") {
                        IconButton(onClick = { changePassword = Pair(username, "") }) {
                            Icon(
                                imageVector = Icons.Filled.LockReset,
                                contentDescription = "Change Password"
                            )
                        }
                    }
                    PlainTooltipBox("Delete") {
                        IconButton(onClick = { deleteAccount = username }) {
                            Icon(
                                imageVector = Icons.Filled.DeleteForever,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            }
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.inverseSurface)
        }
    }
}
