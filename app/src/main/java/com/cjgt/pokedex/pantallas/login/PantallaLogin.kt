package com.cjgt.pokedex.pantallas.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

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