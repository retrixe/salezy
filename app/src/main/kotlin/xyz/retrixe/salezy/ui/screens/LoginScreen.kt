package xyz.retrixe.salezy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.system.exitProcess

@Composable
fun LoginScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val width = 320.dp
    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        OutlinedTextField(value = username, onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.width(width))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.width(width))
        Row(Modifier.width(width).padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { exitProcess(0) }) { Text("Quit") }
            Button(onClick = { /* FIXME */ print("Hi!") }) { Text("Login") }
        }
    }
}
