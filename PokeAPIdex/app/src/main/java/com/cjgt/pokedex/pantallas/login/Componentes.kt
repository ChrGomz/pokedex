package com.cjgt.pokedex.pantallas.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cjgt.pokedex.R


@Composable
fun Logo() {
    Image(painter = painterResource(id = R.drawable.ic_launcher_background),
        contentDescription = "Logo",
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp)

    )
}

@Composable
fun TextoTitulo(size: TextUnit = 42.sp) {
    Text(
        text = AnnotatedString("Mi Reserva"),
        fontSize = size,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(vertical = 8.dp)
    )
}

@Composable
fun EntradaUsuario(viewModel: LoginViewModel) {

    val username = viewModel.username.collectAsState()
    val focusManager = viewModel.focusManager.collectAsState()

    OutlinedTextField(
        value = username.value,
        onValueChange = { viewModel.setUserName(it) },
        label = { Text(text = AnnotatedString("Nombre")) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.value!!.moveFocus(FocusDirection.Down)
        })
    )
}

@Composable
fun EntradaEmail(viewModel: LoginViewModel) {

    val email = viewModel.email.collectAsState()
    val focusManager = viewModel.focusManager.collectAsState()

    OutlinedTextField(
        value = email.value,
        onValueChange = { viewModel.setEmail(it) },
        label = { Text(text = AnnotatedString("Correo electronico")) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.value!!.moveFocus(FocusDirection.Down)
        })
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EntradaContrasenya(model: LoginViewModel, navController: NavController?) {

    val password = model.password.collectAsState()
    val keyboardController = model.keyboardController.collectAsState()

    OutlinedTextField(
        value = password.value,
        onValueChange = { model.setPassword(it) },
        label = { Text(text = AnnotatedString("Contraseña")) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(onDone = {
            loginUser(model.getEmail(), model.getPassword(), onConfirm = {
                keyboardController.value?.hide()
                navController?.navigate("testing")
            }, onError = {
                keyboardController.value?.hide()
                model.setErrorText("Correo o contraseña erroneos")
            })
        })
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegistrarContrasenya(
    model: LoginViewModel,
    signInAction: () -> Unit
) {

    val password = model.password.collectAsState()
    val confirmPassword = model.confirmPassword.collectAsState()
    val focusManager = model.focusManager.collectAsState()
    val keyboardController = model.keyboardController.collectAsState()

    OutlinedTextField(
        value = password.value,
        onValueChange = { model.setPassword(it) },
        label = { Text(text = AnnotatedString("Contraseña")) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next, keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.value!!.moveFocus(FocusDirection.Down)
        })
    )
    OutlinedTextField(
        value = confirmPassword.value,
        onValueChange = { model.setConfirmPassword(it) },
        label = { Text(text = AnnotatedString("Repetir Contraseña")) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController.value?.hide()
            signInAction()
        })
    )
}

@Composable
fun BotonEnviarFormulario(onClick: () -> Unit, textoBoton: String) {
    Button(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text = AnnotatedString(textoBoton), color = Color.White, fontSize = 16.sp)
    }
}

@Composable
fun TextoRedirigir(textoInformacion: String, textoRedirigir: String) {
    val loginViewModel: LoginViewModel = viewModel()
    val isLogin = loginViewModel.isLogin.collectAsState()
    Row {
        Text(text = AnnotatedString(textoInformacion), color = Color.DarkGray)
        Text(text = AnnotatedString(textoRedirigir),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { loginViewModel.changeLoginState(!isLogin.value) })
    }
}
