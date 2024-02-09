import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import xyz.retrixe.salezy.ui.screens.LoginScreen
import xyz.retrixe.salezy.ui.theme.AppTheme

enum class Screens {
    LOGIN
}

@Composable
@Preview
@OptIn(ExperimentalMaterial3Api::class)
fun App() {
    var screen by remember { mutableStateOf(Screens.LOGIN) }

    AppTheme {
        Scaffold(topBar = { TopAppBar(title = { Text("Salezy") }) }) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (screen) {
                    Screens.LOGIN -> LoginScreen()
                }
            }
        }
    }
}

fun main() = application {
    val state = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        placement = WindowPlacement.Floating,
        isMinimized = false,
        width = 1280.dp,
        height = 720.dp
    )
    Window(
        title = "Salezy",
        state = state,
        resizable = true,
        // icon = painterResource("drawable/logo.png"),
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
