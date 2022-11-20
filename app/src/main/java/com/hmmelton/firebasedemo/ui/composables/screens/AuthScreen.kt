package com.hmmelton.firebasedemo.ui.composables.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hmmelton.firebasedemo.R
import com.hmmelton.firebasedemo.ui.composables.views.CircularProgressAnimated
import com.hmmelton.firebasedemo.ui.composables.views.OutlinedTextFieldWithErrorView
import com.hmmelton.firebasedemo.ui.theme.FirebaseDemoTheme
import com.hmmelton.firebasedemo.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.flow.filter

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    // TODO: test if this is working for circular loading indicator
    val isLoading = viewModel.uiState.isLoading
    val formUiState = viewModel.formUiState

    val snackbarHostState = remember { SnackbarHostState() }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnAuthenticated by rememberUpdatedState(onAuthenticated)

    LaunchedEffect(viewModel, lifecycle) {
        // Whenever the uiState changes, check if the user is logged in and
        // call the `onUserLogin` event when `lifecycle` is at least STARTED
        snapshotFlow { viewModel.uiState }
            .filter { it.isUserLoggedIn }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnAuthenticated()
            }
    }

    // Listen for error messages, and display them to the user
    viewModel.uiState.errorMessage?.let { errorMessageId ->
        val errorMessageString = stringResource(errorMessageId)
        LaunchedEffect(errorMessageId, lifecycle) {
            // Snackbars here are used to notify user of auth error, so no need to read result
            snackbarHostState.showSnackbar(message = errorMessageString)

            // Alert ViewModel when Snackbar has disappeared
            snapshotFlow { snackbarHostState.currentSnackbarData }
                .filter { it == null }
                .flowWithLifecycle(lifecycle)
                .collect {
                    viewModel.errorMessageShown()
                }
        }
    }

    Scaffold(
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(48.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.app_name), fontSize = 30.sp)
                Spacer(modifier = Modifier.padding(8.dp))

                // Credential inputs
                EmailTextField(
                    value = formUiState.email,
                    isError = formUiState.invalidEmail,
                    enabled = !isLoading
                ) { newEntry ->
                    viewModel.setEmail(newEntry)
                }
                PasswordTextField(
                    value = formUiState.password,
                    isError = formUiState.invalidPassword,
                    enabled = !isLoading
                ) { newEntry ->
                    viewModel.setPassword(newEntry)
                }

                // Sign in button
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    onClick = { viewModel.onSignInClick() },
                    enabled = !isLoading
                ) {
                    Text(stringResource(R.string.btn_sign_in))
                }

                // Registration button
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    onClick = { viewModel.onRegistrationClick() },
                    enabled = !isLoading
                ) {
                    Text(stringResource(R.string.btn_register))
                }
            }

            // Show progress indicator when waiting for network requests
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressAnimated(progressValue = 0.75f)
                }
            }
        }
    )
}

@Composable
fun EmailTextField(
    value: String,
    isError: Boolean,
    enabled: Boolean,
    onValueChanged: (String) -> Unit
) {
    OutlinedTextFieldWithErrorView(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = { onValueChanged(it.trim()) },
        label = { Text(stringResource(R.string.email_field_label)) },
        isError = isError,
        errorText = stringResource(R.string.invalid_email_error),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        enabled = enabled
    )
}

@Composable
fun PasswordTextField(
    value: String,
    isError: Boolean,
    enabled: Boolean,
    onValueChanged: (String) -> Unit
) {
    OutlinedTextFieldWithErrorView(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = { onValueChanged(it.trim()) },
        label = { Text(stringResource(R.string.password_field_label)) },
        isError = isError,
        errorText = stringResource(R.string.invalid_password_error),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation() ,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        enabled = enabled
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AuthPreview() {
    FirebaseDemoTheme {
        AuthScreen(onAuthenticated = {})
    }
}