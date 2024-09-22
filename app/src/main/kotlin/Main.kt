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
import org.jetbrains.compose.resources.painterResource
import xyz.retrixe.salezy.api.Api
import xyz.retrixe.salezy.generated.resources.Res
import xyz.retrixe.salezy.generated.resources.logo
import xyz.retrixe.salezy.state.*
import xyz.retrixe.salezy.ui.screens.DashboardScreen
import xyz.retrixe.salezy.ui.screens.LoginScreen
import xyz.retrixe.salezy.ui.theme.AppTheme

enum class Screens {
    LOGIN,
    DASHBOARD
}

// FIXME: in inventory: make sure to add ability to spcifiy price details, such as cost of item, profit of item, etc, add these to reports
// FIXME: in reports separate the sales from profit, so the amount sold, vs profit from it

@Composable
@Preview
@OptIn(ExperimentalMaterial3Api::class)
fun App() {
    val snackbarHostState = remember { SnackbarHostState() }
    var screen by remember { mutableStateOf(Screens.LOGIN) }
    var topBar by remember { mutableStateOf<Pair<String, (@Composable () -> Unit)?>?>(null) }
    var remoteSettings by remember { mutableStateOf(RemoteSettings.default) }
    var localConfiguration by remember { mutableStateOf(LocalConfiguration.default) }

    LaunchedEffect(localConfiguration) {
        if (localConfiguration === LocalConfiguration.default)
            localConfiguration = loadConfiguration()
        else saveConfiguration(localConfiguration)
    }
    SideEffect { Api.instance.url = localConfiguration.instanceUrl }

    AppTheme {
        Scaffold(
            topBar = {
                if (topBar != null) TopAppBar(
                    title = { Text(topBar!!.first) },
                    actions = { topBar!!.second?.invoke() }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding -> Box(modifier = Modifier.padding(innerPadding)) {
            CompositionLocalProvider(values = arrayOf(
                RemoteSettingsState provides remoteSettings,
                LocalConfigurationState provides localConfiguration,
                LocalSnackbarHostState provides snackbarHostState
            )) {
                AnimatedContent(targetState = screen) { targetState -> when (targetState) {
                    Screens.LOGIN -> LoginScreen(
                        setTopBar = { title, action -> topBar = Pair(title, action) },
                        overrideInstanceUrl = {
                            localConfiguration = localConfiguration.copy(instanceUrl = it)
                        },
                        setRemoteSettings = { remoteSettings = it },
                        setScreen = { screen = it }
                    )
                    Screens.DASHBOARD -> DashboardScreen(
                        setTopBar = { topBar = it },
                        setRemoteSettings = { remoteSettings = it },
                        logout = {
                            Api.instance.token = ""
                            remoteSettings = RemoteSettings.default
                            screen = Screens.LOGIN
                        }
                    )
                } }
            }
        } }
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
        icon = painterResource(Res.drawable.logo),
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
