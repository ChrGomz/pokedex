package com.sukha.mireserva.pantallas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cjgt.pokedex.R
import com.sukha.mireserva.router.RouterViewModel
import com.sukha.mireserva.router.Rutas
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun Router() {
    //Controlador de navegacion
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val currentNavInput by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavInput?.destination?.route

    //Contexto de la aplicacion
    val context = LocalContext.current

    //ViewModel para el manejo de la barra de navegacion
    val routerViewModel: RouterViewModel = viewModel()

    //Estados de la barra de navegacion
    val isBottomBarVisible = routerViewModel.isBottomBarVisible.collectAsState().value
    val isTopBarVisible = routerViewModel.isTopBarVisible.collectAsState().value
    val isSettingsVisible = routerViewModel.isSettingsVisible.collectAsState().value

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    //Estado del tema
    val isLightTheme = !isSystemInDarkTheme()

    when (currentRoute) {
        Rutas.PantallaLogin.ruta -> {
            routerViewModel.hideBottomBar()
            routerViewModel.hideTopBar()
            routerViewModel.hideSettings()
        }

        else -> {
            routerViewModel.showBottomBar()
            routerViewModel.showTopBar()
            routerViewModel.showSettings()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { NavDrawer(navController) },
        gesturesEnabled = true
    ) {
        Scaffold(topBar = {
            if (isTopBarVisible) {
                TopBar(isLightTheme, isSettingsVisible) {
                    scope.launch {
                        drawerState.apply { if (isOpen) close() else open() }
                    }
                }
            }
        }, bottomBar = {
            if (isBottomBarVisible) {
                BottomBar(isLightTheme, navController)
            }
        }, content = { paddingValues ->
            Surface(modifier = Modifier.padding(paddingValues)) {
                NavHost(
                    navController = navController, startDestination = Rutas.PantallaLogin.ruta
                ) {
                    composable(Rutas.PantallaLogin.ruta) {
                        Rutas.PantallaLogin(dbViewModel, navController)
                    }

                    composable(Rutas.PantallaTesting.ruta) {
                        val searchBarViewModel =
                            SearchBarModel(dbViewModel.listAssets.collectAsState().value)
                        PantallaPrueba(dbViewModel, searchBarViewModel, navController)
                    }
                }
            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(lightTheme: Boolean, settingsState: Boolean, onNavClick: () -> Job) {
    val context = LocalContext.current

    if (settingsState) {
        TopAppBar(title = {
            Text(text = "Mi Reserva")
        }, navigationIcon = {
            IconButton(onClick = {
                onNavClick()
            }) {
                Image(
                    painter = painterResource(
                        if (lightTheme) {
                            R.drawable.dark_menu
                        } else {
                            R.drawable.light_menu
                        }
                    ),
                    contentDescription = "Boton de menu lateral",
                    modifier = Modifier.size(30.dp)
                )
            }
        }, actions = {
            IconButton(onClick = {
                Toast.makeText(context, "Ajustes", Toast.LENGTH_SHORT).show()
            }) {
                Image(
                    painter = painterResource(
                        if (lightTheme) {
                            R.drawable.dark_settings
                        } else {
                            R.drawable.light_settings
                        }
                    ),
                    contentDescription = "Boton de ajustes",
                    modifier = Modifier.size(30.dp)
                )
            }
        })
    } else {
        TopAppBar(title = {
            Text(text = "Mi Reserva")
        })
    }
}

@Composable
fun NavDrawer(navController: NavHostController) {
    val categories = listOf("", "", "", "")
    ModalDrawerSheet {
        LazyColumn(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
        ) {
            items(categories) { category ->
                CategoryMenu(category, navController)
                //HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun CategoryMenu(category: String, navController: NavHostController) {
    val options = mapOf(
        "Crear" to listOf(
        R.drawable.dark_plus,
        R.drawable.light_plus
    ), "Editar" to listOf(
        R.drawable.dark_edit,
        R.drawable.light_edit
    ), "Eliminar" to listOf(
        R.drawable.dark_trash,
        R.drawable.light_trash
    ))
    val categoryTextModifier = Modifier
        .padding(16.dp)
    val categoryFontSize = 18.sp
    Text(
        text = category,
        fontSize = categoryFontSize,
        modifier = categoryTextModifier
    )
    Column(modifier = Modifier.padding(start = 16.dp)) {
        options.forEach { option ->
            OptionMenu(option.key, option.value) {
                //navController.navigate("Admin${category}${option}")
            }
        }
    }
}

@Composable
fun OptionMenu(option: String, icons: List<Int>, onNavClick: () -> Unit) {
    val optionTextModifier = Modifier
        .padding(8.dp)
    val optionFontSize = 18.sp
    val isLightTheme = !isSystemInDarkTheme()
    Row (verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
        .fillMaxWidth(fraction = 0.8f)
        .clickable { onNavClick() }) {
        Image(
            painter = painterResource(
                if(isLightTheme)
                    icons.first()
                else
                    icons.last()
            ),
            contentDescription = "$option icon",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = option,
            fontSize = optionFontSize,
            modifier = optionTextModifier
        )
    }
}

@Composable
fun BottomBar(lightTheme: Boolean, navController: NavHostController) {
    BottomAppBar {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            //Box(modifier = Modifier.size(76.dp, 1.dp)) {}
            BottomBarButton(
                onClick = {  },
                lightTheme = lightTheme,
                darkIconId = R.drawable.dark_plus,
                lightIconId = R.drawable.light_plus
            )
            BottomBarButton(
                onClick = { },
                lightTheme = lightTheme,
                darkIconId = R.drawable.dark_manage_user,
                lightIconId = R.drawable.light_manage_user
            )
            BottomBarButton(
                onClick = {  },
                lightTheme = lightTheme,
                darkIconId = R.drawable.dark_home,
                lightIconId = R.drawable.light_home
            )
            BottomBarButton(
                onClick = {  },
                lightTheme = lightTheme,
                darkIconId = R.drawable.dark_business,
                lightIconId = R.drawable.light_business
            )
            BottomBarButton(
                onClick = {  },
                lightTheme = lightTheme,
                darkIconId = R.drawable.dark_plus,
                lightIconId = R.drawable.light_plus
            )
        }
    }
}

@Composable
fun BottomBarButton(
    onClick: () -> Unit, lightTheme: Boolean, darkIconId: Int, lightIconId: Int
) {
    val imageModifier = Modifier.size(28.dp)
    androidx.compose.material3.Button(
        onClick = onClick,
        shape = CircleShape,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Image(
            painter = painterResource(
                id = if (lightTheme) darkIconId else lightIconId
            ), contentDescription = null, modifier = imageModifier
        )
    }
}