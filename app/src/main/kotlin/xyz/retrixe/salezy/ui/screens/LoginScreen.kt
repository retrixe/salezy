package xyz.retrixe.salezy.ui.screens

import Screens
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import xyz.retrixe.salezy.api.Api
import xyz.retrixe.salezy.state.ConfigurationState
import xyz.retrixe.salezy.state.defaultConfiguration
import xyz.retrixe.salezy.ui.components.PasswordTextField
import xyz.retrixe.salezy.ui.components.PlainTooltipBox
import kotlin.system.exitProcess

@Composable
fun LoginScreen(
    setScreen: (Screens) -> Unit,
    setTopBar: (String, (@Composable () -> Unit)?) -> Unit,
    overrideInstanceUrl: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val (passwordFocus, loginButtonFocus) = remember { FocusRequester.createRefs() }
    var dialogOpen by remember { mutableStateOf(false) }
    var dialogValue by remember { mutableStateOf("") }
    var loadingOrError by remember { mutableStateOf<String?>("") }

    val scope = rememberCoroutineScope()
    fun login() = scope.launch {
        loadingOrError = null
        try {
            Api.instance.token = Api.instance.login(username, password)
            setScreen(Screens.DASHBOARD)
            loadingOrError = ""
        } catch (e: Exception) { loadingOrError = e.message }
    }

    val instanceUrl = ConfigurationState.current.instanceUrl
    LaunchedEffect(instanceUrl) {
        setTopBar("Salezy ❯ Login") { // TODO (low priority): This interferes with transitions.
            PlainTooltipBox("Settings") {
                IconButton(onClick = { dialogOpen = true; dialogValue = instanceUrl }) {
                    Icon(imageVector = Icons.Filled.Settings, "Settings")
                }
            }
        }
    }

    AnimatedVisibility(dialogOpen) {
        Dialog(onDismissRequest = { dialogOpen = false }) {
            fun onSubmit() { overrideInstanceUrl(dialogValue); dialogOpen = false }
            Card(
                modifier = Modifier.wrapContentSize().width(420.dp).padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Settings", fontSize = 28.sp, modifier = Modifier.padding(vertical = 16.dp))
                    OutlinedTextField(value = dialogValue, onValueChange = { dialogValue = it },
                        label = { Text("Instance URL") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        keyboardActions = KeyboardActions(onNext = { onSubmit() }),
                        modifier = Modifier.width(320.dp)
                            .onKeyEvent {
                                // KeyDown is bust on Linux
                                if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                                    onSubmit()
                                    true
                                } else false
                            })
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        TextButton(
                            onClick = { dialogValue = defaultConfiguration.instanceUrl },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Reset to Default")
                        }
                        TextButton(onClick = { onSubmit() }, modifier = Modifier.padding(8.dp)) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }

    val width = 320.dp
    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(value = username, onValueChange = { username = it },
            enabled = loadingOrError != null,
            label = { Text("Username") },
            singleLine = true,
            isError = loadingOrError?.isNotEmpty() == true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() }),
            modifier = Modifier.width(width)
                .onKeyEvent {
                    // KeyDown is bust on Linux
                    if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                        passwordFocus.requestFocus()
                        true
                    } else false
                })

        Spacer(Modifier.height(8.dp))

        PasswordTextField(value = password, onValueChange = { password = it },
            enabled = loadingOrError != null,
            label = { Text("Password") },
            supportingText = { if (loadingOrError?.isNotEmpty() == true) Text(loadingOrError!!) },
            isError = loadingOrError?.isNotEmpty() == true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = { login() }),
            modifier = Modifier.width(width)
                .focusRequester(passwordFocus)
                .focusProperties { next = loginButtonFocus }
                .onKeyEvent {
                    if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                        login()
                        true
                    } else false
                })

        Row(Modifier.width(width).padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { exitProcess(0) }) { Text("Quit") }

            Button(modifier = Modifier.focusRequester(loginButtonFocus),
                onClick = { login() },
                enabled = loadingOrError != null) { Text("Login") }
        }
    }
}
