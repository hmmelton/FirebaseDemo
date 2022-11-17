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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hmmelton.firebasedemo.data.model.Error
import com.hmmelton.firebasedemo.ui.composables.views.CircularProgressAnimated
import com.hmmelton.firebasedemo.ui.composables.views.OutlinedTextFieldWithErrorView
import com.hmmelton.firebasedemo.ui.theme.FirebaseDemoTheme
import com.hmmelton.firebasedemo.ui.viewmodels.AuthViewModel

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    val snackbarHostState = remember { SnackbarHostState() }
    var isLoaderVisible by rememberSaveable { mutableStateOf(false) }

    uiState.response?.let { response ->
        // Hide progress indicator when new status is received
        isLoaderVisible = false

        val currentOnAuthenticated by rememberUpdatedState(onAuthenticated)
        LaunchedEffect(uiState) {
            if (response is Error) {
                // Snackbars here are used to notify user of auth error, so no need to read result
                snackbarHostState.showSnackbar(message = response.message)
            } else {
                currentOnAuthenticated()
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
                Text(text = "FirebaseDemo", fontSize = 30.sp)
                Spacer(modifier = Modifier.padding(8.dp))

                // Credential inputs
                EmailTextField(
                    value = viewModel.email,
                    isError = viewModel.invalidEmail,
                    enabled = !uiState.isLoading()
                ) { newEntry ->
                    viewModel.email = newEntry
                }
                PasswordTextField(
                    value = viewModel.password,
                    isError = viewModel.invalidPassword,
                    enabled = !uiState.isLoading()
                ) { newEntry ->
                    viewModel.password = newEntry
                }

                // Sign in button
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    onClick = {
                        isLoaderVisible = true
                        viewModel.onSignInClick()
                    },
                    enabled = !uiState.isLoading()
                ) {
                    Text("Sign In")
                }

                // Registration button
                Button(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    onClick = {
                        isLoaderVisible = true
                        viewModel.onRegistrationClick()
                    },
                    enabled = !uiState.isLoading()
                ) {
                    Text("Register")
                }
            }

            // Show progress indicator when waiting for network requests
            if (isLoaderVisible) {
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
        label = { Text("Enter email") },
        isError = isError,
        errorText = "Enter valid email",
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
        label = { Text("Enter password") },
        isError = isError,
        errorText = "Enter valid password",
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