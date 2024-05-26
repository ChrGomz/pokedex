package com.cjgt.pokedex.pantallas.router

import android.util.Log
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
import com.cjgt.pokedex.SharedPrefHandler
import com.cjgt.pokedex.pantallas.PantallaBuscador
import com.cjgt.pokedex.pantallas.PantallaInicio
import com.cjgt.pokedex.pantallas.PantallaPokemon
import com.cjgt.pokedex.pantallas.PantallaPokemonFav
import com.cjgt.pokedex.pantallas.PantallaPorTipo
import com.cjgt.pokedex.pantallas.PantallaSelectorTipo
import com.cjgt.pokedex.pantallas.TypeColors
import com.cjgt.pokedex.pantallas.buildImageLoader
import com.cjgt.pokedex.pantallas.fetchPokemonDetails
import com.cjgt.pokedex.pantallas.fetchPokemonList
import com.cjgt.pokedex.pantallas.login.PantallaLogin
import com.cjgt.pokedex.retrofit.pokeApi.Pokemon
import com.cjgt.pokedex.retrofit.pokeApi.PokemonRepository
import com.cjgt.pokedex.retrofit.teamApi.TeamRepository
import com.cjgt.pokedex.roomDB.LocalPokemonDB
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun Router(sharedPrefHandler: SharedPrefHandler) {
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

    val teamRepository = TeamRepository()

    var pokemonList by remember { mutableStateOf<List<Pokemon>?>(null) }

    LaunchedEffect(key1 = true) {
        withContext(Dispatchers.IO) {
            val pokemonListData = fetchPokemonList(pokemonRepository, 1302)
            val fetchedPokemonList = fetchPokemonDetails(pokemonRepository, pokemonListData)
            pokemonList = fetchedPokemonList
        }
    }

    if (currentRoute != null) {
        when (currentRoute) {
            Rutas.PantallaLogin.ruta, Rutas.PantallaBuscador.ruta -> {
                routerViewModel.hideBottomBar()
                routerViewModel.hideTopBar()
            }

            "PokemonPorTipo/{type}" -> {
                routerViewModel.showBottomBar()
                routerViewModel.showTopBar()
                routerViewModel.showSettings()
            }

            "Pokemon/{pokemonId}" -> {
                routerViewModel.showBottomBar()
                routerViewModel.hideTopBar()
            }

            else -> {
                routerViewModel.showBottomBar()
                routerViewModel.showTopBar()
                routerViewModel.showSettings()
            }
        }
    }

    val startDestination = remember {
        mutableStateOf(Rutas.PantallaLogin.ruta)
    }

    if (sharedPrefHandler.isLoggedIn()) {
        startDestination.value = Rutas.PantallaInicio.ruta
    }

    val gesturesEnabled = currentRoute != "Pokemon/{pokemonId}"

    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
            NavDrawer {
                navController.navigate("PokemonPorTipo/$it")
                drawerState.apply { scope.launch { close() } }
            }
        }, gesturesEnabled = gesturesEnabled
    ) {
        Scaffold(topBar = {
            if (isTopBarVisible) {
                TopBar(isLightTheme, isSettingsVisible, onExitClick = {
                    Firebase.auth.signOut()
                    sharedPrefHandler.setLoggedIn(false)
                    navController.navigate(Rutas.PantallaLogin.ruta)
                }) {
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
                    navController = navController, startDestination = startDestination.value
                ) {
                    composable(Rutas.PantallaLogin.ruta) {
                        PantallaLogin(navController, sharedPrefHandler)
                    }

                    composable(Rutas.PantallaInicio.ruta) {
                        PantallaInicio(pokemonRepository, imageLoader) { pokemon ->
                            navController.navigate("Pokemon/${pokemon.id}")
                        }
                    }

                    composable(Rutas.PantallaSelectorTipo.ruta) {
                        PantallaSelectorTipo(pokemonTypes, imageLoader) { tipo ->
                            navController.navigate("PokemonPorTipo/$tipo")
                        }
                    }

                    composable(Rutas.PantallaPokemonFav.ruta) {
                        PantallaPokemonFav(
                            teamRepository, pokemonRepository, imageLoader
                        ) { pokemon ->
                            navController.navigate("Pokemon/${pokemon.id}")
                        }
                    }

                    composable("PokemonPorTipo/{type}") {
                        val type = it.arguments?.getString("type")
                        if (type != null) {
                            Log.v("CHRIS_DEBUG", "Type: $type")
                            if (type == "favoritos") {
                                PantallaPokemonFav(
                                    teamRepository, pokemonRepository, imageLoader
                                ) { pokemon ->
                                    navController.navigate("Pokemon/${pokemon.id}")
                                }
                            } else {
                                PantallaPorTipo(pokemonRepository, imageLoader, type) { pokemon ->
                                    navController.navigate("Pokemon/${pokemon.id}")
                                }
                            }
                        } else {
                            PantallaInicio(pokemonRepository, imageLoader) { pokemon ->
                                navController.navigate("Pokemon/${pokemon.id}")
                            }
                        }
                    }

                    composable(Rutas.PantallaBuscador.ruta) {
                        pokemonList?.sortedBy { it.id }.let { sortedList ->
                            PantallaBuscador(sortedList, imageLoader) { pokemon ->
                                navController.navigate("Pokemon/${pokemon.id}")
                            }
                        }
                    }

                    composable("Pokemon/{pokemonId}") {
                        val pokemonId = it.arguments?.getString("pokemonId")
                        val pokemon = pokemonList?.find { it.id == pokemonId?.toInt() }
                        var showDialog by remember { mutableStateOf(false) }

                        if (pokemon != null) {
                            PantallaPokemon( pokemon, imageLoader, teamRepository)
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

val pokemonTypes = listOf(
    "Normal",
    "Fighting",
    "Flying",
    "Poison",
    "Ground",
    "Rock",
    "Bug",
    "Ghost",
    "Steel",
    "Fire",
    "Water",
    "Grass",
    "Electric",
    "Psychic",
    "Ice",
    "Dragon",
    "Dark",
    "Fairy"
)

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
fun TopBar(lightTheme: Boolean, settingsState: Boolean, onExitClick: () -> Unit, onNavClick: () -> Job) {
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
                    modifier = Modifier.size(48.dp)
                )
            }
        }, actions = {
            IconButton(onClick = {
                onExitClick()
            }) {
                Image(
                    painter = painterResource(
                        if (lightTheme) {
                            R.drawable.dark_singout
                        } else {
                            R.drawable.light_singout
                        }
                    ), contentDescription = "Boton de Salir", modifier = Modifier.size(30.dp)
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
fun NavDrawer(OnNavClick: (String) -> Unit) {
    ModalDrawerSheet {
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
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
                    .clickable { OnNavClick("favoritos") }
                    .fillMaxWidth(0.9f)
                    .padding(top = 8.dp, bottom = 8.dp, start = 24.dp)
                TypeMenu("Favoritos", Color(0xFFFC0000).copy(alpha = 1f), modifier)
            }
            item {
                Text(
                    text = "Types of Pokémon",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(pokemonTypes) { type ->
                val modifier = Modifier
                    .clip(CircleShape)
                    .clickable { OnNavClick(type.lowercase(Locale.getDefault())) }
                    .fillMaxWidth(0.9f)
                    .padding(top = 8.dp, bottom = 8.dp, start = 24.dp)
                val typeColor: Color = Color(TypeColors.getColorForType(type)).copy(alpha = 0.9f)
                TypeMenu(type, typeColor, modifier)
            }

        }
    }
}

@Composable
fun TypeMenu(type: String, typeColor: Color, modifier: Modifier) {
    Text(
        text = type,
        fontSize = 18.sp,
        color = typeColor,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
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
                onClick = { navController.navigate(Rutas.PantallaSelectorTipo.ruta) },
                lightTheme = lightTheme,
                darkIconId = R.drawable.poke_star,
                lightIconId = R.drawable.poke_star
            )
            BottomBarButton(
                onClick = { navController.navigate(Rutas.PantallaInicio.ruta) },
                lightTheme = lightTheme,
                darkIconId = R.drawable.pokeball,
                lightIconId = R.drawable.pokeball
            )
            BottomBarButton(
                onClick = { navController.navigate(Rutas.PantallaBuscador.ruta) },
                lightTheme = lightTheme,
                darkIconId = R.drawable.search,
                lightIconId = R.drawable.search
            )
        }
    }
}

@Composable
fun BottomBarButton(
    onClick: () -> Unit, lightTheme: Boolean, darkIconId: Int, lightIconId: Int
) {
    val imageModifier = Modifier.size(32.dp)
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