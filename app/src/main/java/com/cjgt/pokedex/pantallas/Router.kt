package com.cjgt.pokedex.pantallas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cjgt.pokedex.R
import com.cjgt.pokedex.pantallas.login.PantallaLogin
import com.cjgt.pokedex.pantallas.testing.PantallaTesting
import com.cjgt.pokedex.retrofit.Pokemon
import com.cjgt.pokedex.retrofit.PokemonRepository
import com.cjgt.pokedex.retrofit.TypeColors
import com.cjgt.pokedex.roomDB.LocalPokemonDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    val database = LocalPokemonDB.getDatabase(context)
    val pokemonDao = database.pokemonDao()
    val pokemonRepository = PokemonRepository(pokemonDao)
    val imageLoader = buildImageLoader(
        context
    )

    var pokemonList by remember { mutableStateOf<List<Pokemon>?>(null) }

    LaunchedEffect(key1 = true) {
        withContext(Dispatchers.IO) {
            val pokemonListData = fetchPokemonList(pokemonRepository)
            val fetchedPokemonList = fetchPokemonDetails(pokemonRepository, pokemonListData)
            pokemonList = fetchedPokemonList
        }
    }

    when (currentRoute) {
        Rutas.PantallaLogin.ruta -> {
            routerViewModel.hideBottomBar()
            routerViewModel.hideTopBar()
        }

        else -> {
            routerViewModel.showBottomBar()
            routerViewModel.showTopBar()
            routerViewModel.showSettings()
        }
    }

    val gesturesEnabled = currentRoute != "Pokemon/{pokemonId}"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { NavDrawer(navController) },
        gesturesEnabled = gesturesEnabled
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
            Surface(
                modifier = Modifier.padding(paddingValues)
            ) {
                NavHost(
                    navController = navController, startDestination = Rutas.PantallaInicio.ruta
                ) {
                    composable(Rutas.PantallaLogin.ruta) {
                        routerViewModel.hideTopBar()
                        routerViewModel.hideBottomBar()
                        PantallaLogin(navController)
                    }

                    composable(Rutas.PantallaTesting.ruta) {
                        PantallaTesting(navController)
                    }

                    composable(Rutas.PantallaInicio.ruta) {
                        routerViewModel.showTopBar()
                        routerViewModel.showBottomBar()
                        routerViewModel.showSettings()
                        PantallaInicio(pokemonRepository, imageLoader) { pokemon ->
                            navController.navigate("Pokemon/${pokemon.id}")
                        }
                    }

                    composable(Rutas.PantallaBuscador.ruta) {
                        routerViewModel.hideTopBar()
                        routerViewModel.hideBottomBar()
                        pokemonList?.sortedBy { it.id }.let { sortedList ->
                            PantallaBuscador(sortedList, imageLoader) { pokemon ->
                                navController.navigate("Pokemon/${pokemon.id}")
                            }
                        }
                    }

                    composable("Pokemon/{pokemonId}") {
                        routerViewModel.showBottomBar()
                        routerViewModel.hideTopBar()
                        val pokemonId = it.arguments?.getString("pokemonId")
                        val pokemon = pokemonList?.find { it.id == pokemonId?.toInt() }
                        var showDialog by remember { mutableStateOf(false) }

                        if (pokemon != null) {
                            PantallaPokemon(pokemon, imageLoader)
                        } else {
                            showDialog = true
                        }

                        ShowErrorDialog(showDialog, {
                            showDialog = false
                            navController.popBackStack()
                        }, {
                            showDialog = false
                            navController.popBackStack()
                        })
                    }
                }
            }
        })
    }
}

@Composable
fun ShowErrorDialog(showDialog: Boolean, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    if (showDialog) {
        AlertDialog(onDismissRequest = onDismiss,
            title = { Text("Error") },
            text = { Text("Pokémon no encontrado") },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("Volver")
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(lightTheme: Boolean, settingsState: Boolean, onNavClick: () -> Job) {
    val context = LocalContext.current

    if (settingsState) {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.primaryContainer.copy(
                alpha = 0.2f
            )
        ), title = {
            Text(text = "Pokedex")
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
                    ), contentDescription = "Boton de ajustes", modifier = Modifier.size(30.dp)
                )
            }
        })
    } else {
        TopAppBar(title = {
            Text(text = "Pokedex")
        })
    }
}

@Composable
fun NavDrawer(navController: NavHostController) {
    val pokemonTypes = listOf(
        "Fuego",
        "Agua",
        "Tierra",
        "Lucha",
        "Volador",
        "Eléctrico",
        "Roca",
        "Hielo",
        "Bicho",
        "Fantasma",
        "Acero",
        "Dragón",
        "Siniestro",
        "Hada"
    )
    ModalDrawerSheet {
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            item {
                Text(
                    text = "Tipos de Pokémon",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(pokemonTypes) { type ->
                val modifier = Modifier
                    .clip(CircleShape)
                    .clickable { }
                    .fillMaxWidth(0.9f)
                    .padding(top = 8.dp, bottom = 8.dp, start = 24.dp)
                val typeColor: Color = Color(TypeColors.getColorForType(type)).copy(alpha = 0.9f)
                TypeMenu(type, navController, typeColor, modifier)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Mis Favoritos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                val modifier = Modifier
                    .clip(CircleShape)
                    .clickable { }
                    .fillMaxWidth(0.9f)
                    .padding(top = 8.dp, bottom = 8.dp, start = 24.dp)
                TypeMenu("Favoritos", navController, Color(0xFFFC0000).copy(alpha = 1f), modifier)
            }
        }
    }
}

@Composable
fun TypeMenu(type: String, navController: NavHostController, typeColor: Color, modifier: Modifier) {
    Text(
        text = type,
        fontSize = 18.sp,
        color = typeColor,
        fontWeight = FontWeight.Bold,
        modifier = modifier)
}

@Composable
fun BottomBar(lightTheme: Boolean, navController: NavHostController) {
    BottomAppBar(containerColor = colorScheme.primaryContainer.copy(alpha = 0.2f)) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            BottomBarButton(
                onClick = { },
                lightTheme = lightTheme,
                darkIconId = R.drawable.dark_plus,
                lightIconId = R.drawable.light_plus
            )
            BottomBarButton(
                onClick = { navController.navigate(Rutas.PantallaInicio.ruta) },
                lightTheme = lightTheme,
                darkIconId = R.drawable.dark_home,
                lightIconId = R.drawable.light_home
            )
            BottomBarButton(
                onClick = { navController.navigate(Rutas.PantallaBuscador.ruta) },
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
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = colorScheme.primaryContainer
        )
    ) {
        Image(
            painter = painterResource(
                id = if (lightTheme) darkIconId else lightIconId
            ), contentDescription = null, modifier = imageModifier
        )
    }
}