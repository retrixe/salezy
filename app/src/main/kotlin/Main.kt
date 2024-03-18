import androidx.compose.animation.AnimatedContent
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import xyz.retrixe.salezy.api.Api
import xyz.retrixe.salezy.generated.resources.Res
import xyz.retrixe.salezy.generated.resources.logo
import xyz.retrixe.salezy.state.ConfigurationState
import xyz.retrixe.salezy.state.defaultConfiguration
import xyz.retrixe.salezy.state.loadConfiguration
import xyz.retrixe.salezy.state.saveConfiguration
import xyz.retrixe.salezy.ui.screens.DashboardScreen
import xyz.retrixe.salezy.ui.screens.LoginScreen
import xyz.retrixe.salezy.ui.theme.AppTheme

enum class Screens {
    LOGIN,
    DASHBOARD
}

@Composable
@Preview
@OptIn(ExperimentalMaterial3Api::class)
fun App() {
    var screen by remember { mutableStateOf(Screens.LOGIN) }
    var topBar by remember { mutableStateOf<Pair<String, (@Composable () -> Unit)?>?>(null) }
    var configuration by remember { mutableStateOf(defaultConfiguration) }

    LaunchedEffect(configuration) {
        if (configuration === defaultConfiguration)
            configuration = loadConfiguration()
        else saveConfiguration(configuration)
    }
    SideEffect { Api.instance.url = configuration.instanceUrl }

    AppTheme {
        Scaffold(topBar = {
            if (topBar != null) TopAppBar(
                title = { Text(topBar!!.first) },
                actions = { topBar!!.second?.invoke() }
            )
        }) { innerPadding -> Box(modifier = Modifier.padding(innerPadding)) {
            CompositionLocalProvider(ConfigurationState provides configuration) {
                AnimatedContent(targetState = screen) { targetState -> when (targetState) {
                    Screens.LOGIN -> LoginScreen(
                        setTopBar = { title, action -> topBar = Pair(title, action) },
                        overrideInstanceUrl = {
                            configuration = configuration.copy(instanceUrl = it)
                        },
                        setScreen = { screen = it }
                    )
                    Screens.DASHBOARD -> DashboardScreen(
                        setTopBar = { topBar = it },
                        logout = { screen = Screens.LOGIN } // FIXME
                    )
                } }
            }
        } }
    }
}

@OptIn(ExperimentalResourceApi::class)
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
        icon = painterResource(Res.drawable.logo),
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
