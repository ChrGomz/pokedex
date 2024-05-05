package com.cjgt.pokedex.pantallas.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PantallaLogin(navController: NavController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val loginViewModel: LoginViewModel = viewModel()
    loginViewModel.addUIFunctions(context, focusManager, keyboardController)
    val isLogin = loginViewModel.isLogin.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        if (isLogin.value) ContenidoLogin(model = loginViewModel, navController)
        else ContenidoSingIn(loginModel = loginViewModel, navController)
    }
}

@Composable
fun ContenidoLogin(model: LoginViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    val errorText = model.errorText.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Logo()
            TextoTitulo()
        }
        item {
            EntradaEmail(viewModel = model)
            EntradaContrasenya(model = model, navController)
            Text(
                text = AnnotatedString(errorText.value), color = Color.Red, fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            BotonEnviarFormulario(onClick = {
                if (model.isAllFieldFilled("login")) {
                    loginUser(model.getEmail(), model.getPassword(), onConfirm = {
                        navController.navigate("pantallainicio")
                    }, onError = {
                        model.setErrorText("Correo o contraseña erronea")
                    })
                    model.changeLoginState(true)
                } else {
                    model.setErrorText("Debe rellenar todos los campos")
                }
            }, "Iniciar sesion")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                TextoRedirigir("No tienes una cuenta?", "Registrame")
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContenidoSingIn(
    loginModel: LoginViewModel, navController: NavController
) {
    val scope = rememberCoroutineScope()

    val errorText = loginModel.errorText.collectAsState()
    val keyboardController = loginModel.keyboardController.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Logo()
            TextoTitulo()
        }
        item {
            EntradaUsuario(viewModel = loginModel)
            EntradaEmail(viewModel = loginModel)
            RegistrarContrasenya(model = loginModel) {
                signInAction(
                    loginModel,
                    navController,
                    scope,
                    keyboardController.value!!
                )
            }
            Text(
                text = AnnotatedString(errorText.value), color = Color.Red, fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            BotonEnviarFormulario(
                onClick = {
                    signInAction(
                        loginModel,
                        navController,
                        scope,
                        keyboardController.value!!
                    )
                }, "Registrarse"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                TextoRedirigir("Ya tengo una cuenta, ", "Iniciar sesion")
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun signInAction(
    loginModel: LoginViewModel,
    navController: NavController,
    scope: CoroutineScope,
    keyboardController: SoftwareKeyboardController
) {
    if (loginModel.isAllFieldFilled("sigin")) {
        if (loginModel.isContrasenyaConfirmed()) {
            registerUser(
                email = loginModel.getEmail(),
                password = loginModel.getPassword(),
                onConfirm = {
                    keyboardController.hide()
                    scope.launch {
                        navController.navigate("pantallaAutoRegistro")
                    }
                },
                onError = {
                    keyboardController.hide()
                    loginModel.setErrorText("Correo o contraseña inválidos")
                })
        } else {
            loginModel.setErrorText("Las contraseñas no coinciden")
            keyboardController.hide()
        }
    } else {
        loginModel.setErrorText("Debe rellenar todos los campos")
    }
}