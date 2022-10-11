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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hmmelton.firebasedemo.data.model.Error
import com.hmmelton.firebasedemo.ui.composables.views.OutlinedTextFieldWithErrorView
import com.hmmelton.firebasedemo.ui.theme.FirebaseDemoTheme
import com.hmmelton.firebasedemo.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxWidth()

    val snackbarHostState = remember { SnackbarHostState() }

    // Conflated channel ensures we never have more than 1 Snackbar at a time
    val channel = remember { Channel<String>(Channel.Factory.CONFLATED) }
    LaunchedEffect(channel) {
        channel.receiveAsFlow().collect { message ->
            // Snackbars here are used to notify user of auth error, so no need to read result
            snackbarHostState.showSnackbar(message = message)
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
                    isError = viewModel.invalidEmail
                ) { newEntry ->
                    viewModel.email = newEntry
                }
                PasswordTextField(
                    value = viewModel.password,
                    isError = viewModel.invalidPassword
                ) { newEntry ->
                    viewModel.password = newEntry
                }

                // Sign in button
                Button(
                    modifier = modifier,
                    onClick = {
                        viewModel.onSignInClick { response ->
                            if (response is Error) {
                                channel.trySend(response.message)
                            } else {
                                onAuthenticated()
                            }
                        }
                    }
                ) {
                    Text("Sign In")
                }

                // Registration button
                Button(
                    modifier = modifier,
                    onClick = {
                        viewModel.onRegistrationClick { response ->
                            if (response is Error) {
                                channel.trySend(response.message)
                            } else {
                                onAuthenticated()
                            }
                        }
                    }
                ) {
                    Text("Register")
                }
            }
        }
    )
}

@Composable
fun EmailTextField(
    value: String,
    isError: Boolean,
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
}

@Composable
fun PasswordTextField(
    value: String,
    isError: Boolean,
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AuthPreview() {
    FirebaseDemoTheme {
        AuthScreen(onAuthenticated = {})
    }
}