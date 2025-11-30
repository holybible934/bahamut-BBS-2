package com.kota.Bahamut.pages.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Pure Jetpack Compose login page. No Android Views or XML usage.
 * This screen mirrors the essential UI of LoginPage.kt: username, password,
 * remember user, web sign-in, and a login action. VIP state can disable web sign-in.
 */
@Composable
fun ComposeLoginPage(
    username: String,
    password: String,
    rememberUser: Boolean,
    webSignIn: Boolean,
    isVip: Boolean,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onToggleRememberUser: (Boolean) -> Unit,
    onToggleWebSignIn: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
) {
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState: SnackbarHostState = scaffoldState.snackbarHostState

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "勇者登入") },
                navigationIcon = onBackClick?.let {
                    { IconButton(onClick = it) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Content(
            padding = innerPadding,
            username = username,
            password = password,
            rememberUser = rememberUser,
            webSignIn = webSignIn,
            isVip = isVip,
            isLoading = isLoading,
            onUsernameChange = onUsernameChange,
            onPasswordChange = onPasswordChange,
            onToggleRememberUser = onToggleRememberUser,
            onToggleWebSignIn = onToggleWebSignIn,
            onLoginClick = onLoginClick,
        )
    }
}

@Composable
private fun Content(
    padding: PaddingValues,
    username: String,
    password: String,
    rememberUser: Boolean,
    webSignIn: Boolean,
    isVip: Boolean,
    isLoading: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onToggleRememberUser: (Boolean) -> Unit,
    onToggleWebSignIn: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
) {
    val scroll = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("帳號") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("密碼") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onLoginClick() })
            )

            Spacer(Modifier.height(16.dp))

            // Remember user
            LabeledCheckbox(
                checked = rememberUser,
                onCheckedChange = onToggleRememberUser,
                label = "記住此帳號"
            )

            Spacer(Modifier.height(8.dp))

            // Web sign-in (exclusive to VIP users)
            LabeledCheckbox(
                checked = webSignIn && isVip,
                onCheckedChange = { if (isVip) onToggleWebSignIn(it) },
                label = "使用 Web 自動簽到",
                enabled = isVip
            )

            if (!isVip) {
                Spacer(Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            text = "VIP 可使用 Web 自動簽到。",
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onLoginClick,
                enabled = !isLoading && username.isNotBlank() && password.isNotBlank(),
            ) {
                Text(text = if (isLoading) "請稍候…" else "登入")
            }

            Spacer(Modifier.height(24.dp))

            Hint()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LabeledCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    enabled: Boolean = true
) {
    androidx.compose.material.ListItem(
        icon = { Icon(Icons.Default.Info, contentDescription = null) },
        text = { Text(label) },
        trailing = {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
        }
    )
}

@Composable
private fun Hint() {
    Text(
        text = "帳號與密碼不可為空。",
        style = MaterialTheme.typography.caption,
        modifier = Modifier.fillMaxWidth()
    )
}

// Simple state holder for previews or hoisting from callers if desired.
data class LoginComposeState(
    val username: String = "",
    val password: String = "",
    val rememberUser: Boolean = false,
    val webSignIn: Boolean = false,
    val isVip: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@Preview(showBackground = true, name = "Login (Normal)")
@Composable
private fun PreviewComposeLoginPageNormal() {
    val state = LoginComposeState(username = "user", password = "", rememberUser = true, webSignIn = false)
    ComposeLoginPage(
        username = state.username,
        password = state.password,
        rememberUser = state.rememberUser,
        webSignIn = state.webSignIn,
        isVip = state.isVip,
        isLoading = state.isLoading,
        errorMessage = state.errorMessage,
        onUsernameChange = {},
        onPasswordChange = {},
        onToggleRememberUser = {},
        onToggleWebSignIn = {},
        onLoginClick = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true, name = "Login (VIP)")
@Composable
private fun PreviewComposeLoginPageVip() {
    val state = LoginComposeState(username = "vip_user", password = "", rememberUser = false, webSignIn = true, isVip = true)
    ComposeLoginPage(
        username = state.username,
        password = state.password,
        rememberUser = state.rememberUser,
        webSignIn = state.webSignIn,
        isVip = state.isVip,
        isLoading = state.isLoading,
        errorMessage = state.errorMessage,
        onUsernameChange = {},
        onPasswordChange = {},
        onToggleRememberUser = {},
        onToggleWebSignIn = {},
        onLoginClick = {},
        onBackClick = {}
    )
}
