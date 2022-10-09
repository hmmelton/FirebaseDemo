package com.hmmelton.firebasedemo.ui.composables.screens

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmmelton.firebasedemo.ui.composables.views.OutlinedTextFieldWithErrorView
import com.hmmelton.firebasedemo.ui.theme.FirebaseDemoTheme
import com.hmmelton.firebasedemo.utils.AuthManager

@Composable
fun AuthScreen(
    onSignInClick: (String, String) -> Unit,
    onRegisterClick: (String, String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    // Flag if user entered invalid values
    var invalidEmail by rememberSaveable { mutableStateOf(false) }
    var invalidPassword by rememberSaveable { mutableStateOf(false) }

    val modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxWidth()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "FirebaseDemo", fontSize = 30.sp)
        Spacer(modifier = Modifier.padding(8.dp))
        EmailTextField(value = email, isError = invalidEmail) { newEntry ->
            email = newEntry
        }
        PasswordTextField(value = password, isError = invalidPassword) { newEntry ->
            password = newEntry
        }
        Button(
            modifier = modifier,
            onClick = {
                invalidEmail = !isValidEmail(email)
                invalidPassword = isValidPassword(password)

                // Only process click if email and password are valid
                if (!invalidEmail && !invalidPassword) {
                    onSignInClick(email, password)
                }
            }
        ) {
            Text("Sign In")
        }
        Button(
            modifier = modifier,
            onClick = {
                invalidEmail = !isValidEmail(email)
                invalidPassword = isValidPassword(password)

                // Only process click if email and password are valid
                if (!invalidEmail && !invalidPassword) {
                    onRegisterClick(email, password)
                }
            }
        ) {
            Text("Register")
        }
    }
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

private fun isValidEmail(email: String): Boolean {
    return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

/**
 * Function to check if given password matches
 */
private fun isValidPassword(password: String): Boolean {
    return password.length > AuthManager.PASSWORD_MIN_LENGTH
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AuthPreview() {
    FirebaseDemoTheme {
        AuthScreen(
            onSignInClick = {_, _ -> },
            onRegisterClick = {_, _ ->}
        )
    }
}