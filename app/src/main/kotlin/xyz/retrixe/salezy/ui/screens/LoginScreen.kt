package xyz.retrixe.salezy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import xyz.retrixe.salezy.ui.components.PasswordTextField
import kotlin.system.exitProcess

fun login() = println("Hi!")

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(setTopBar: (String?) -> Unit) {
    setTopBar("Salezy ❯ Login")
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val (passwordFocus, loginButtonFocus) = remember { FocusRequester.createRefs() }

    val width = 320.dp
    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(value = username, onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
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
            label = { Text("Password") },
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
                onClick = { login() }) { Text("Login") }
        }
    }
}
