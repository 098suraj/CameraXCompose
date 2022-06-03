package com.example.snigdhaapp.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import com.example.snigdhaapp.R
import com.example.snigdhaapp.ViewModel.AuthViewModel
import com.example.snigdhaapp.repo.AuthState
import com.example.snigdhaapp.ui.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi

private val showDialog = mutableStateOf(false)

@Composable
fun SignUP(
    viewModel: AuthViewModel,
    onNavigate: (Int) -> (Unit)
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.BackgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeText()
            PurposeImage()
            SignUpText_field(viewModel)
            Login(onNavigate)


        }
    }
}

@Composable
fun Login( onNavigate: (Int) -> (Unit)) {
    Text(
        text = "Login ",
        color = LightGreen1,
        modifier = Modifier
            .padding(top = 70.dp, start = 5.dp)
            .clickable {
                onNavigate(R.id.FirstFragment)
            },

        )
}






@Composable
fun SignUpText_field(viewModel: AuthViewModel) {
    var TextFieldEmailState by remember { mutableStateOf("") }
    var UserName by remember {
        mutableStateOf("")
    }
    TextField(
        value = UserName,
        onValueChange = { newInput -> UserName = newInput },
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_supervised_user_circle_24),
                contentDescription = "email"
            )
        },
        label = {
            Text(
                text = "Name",
                color = MaterialTheme.colors.TextFieldTextColor
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier
            .padding(top = 35.dp)
            .background(MaterialTheme.colors.TextFieldColor)
    )
    TextField(
        value = TextFieldEmailState,
        onValueChange = { newInput -> TextFieldEmailState = newInput },
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.email),
                contentDescription = "email"
            )
        },
        label = {
            Text(
                text = "E-mail address",
                color = MaterialTheme.colors.TextFieldTextColor
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier
            .padding(top = 35.dp)
            .background(MaterialTheme.colors.TextFieldColor)
    )

    var TextFieldPasswordState by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    TextField(
        value = TextFieldPasswordState,
        onValueChange = { newInput -> TextFieldPasswordState = newInput },
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.password),
                contentDescription = "password"
            )
        },
        label = {
            Text(
                text = "Password",
                color = MaterialTheme.colors.TextFieldTextColor
            )
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            // Please provide localized description for accessibility services
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = {passwordVisible = !passwordVisible}){
                Icon(imageVector  = image, description)
            }
        },
        modifier = Modifier
            .padding(top = 25.dp)
            .background(MaterialTheme.colors.TextFieldColor)
    )
    SignNUP(viewModel,UserName, TextFieldEmailState, TextFieldPasswordState)
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SignNUP(
    viewModel: AuthViewModel,
    Username:String,
    TextFieldEmailState: String,
    TextFieldPasswordState: String,

) {
    when (val state = viewModel.authState.collectAsState().value) {
        is AuthState.Success -> showDialog.value = true
    }


    Button(
        onClick = {
            viewModel.signUp(TextFieldEmailState, TextFieldPasswordState,Username)

        }, modifier = Modifier
            .padding(top = 25.dp)
            .requiredWidth(277.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = ButtonGreen)
    ) {
        Text(text = "Sign Up", color = Color.White)
    }
    if (showDialog.value) {

    }
}


