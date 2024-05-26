package com.cjgt.pokedex.pantallas.router

sealed class Rutas(var ruta: String) {
    object PantallaLogin: Rutas(ruta = "pantallalogin")

    object PantallaBuscador: Rutas(ruta = "pantallabuscador")

    object PantallaInicio: Rutas(ruta = "pantallainicio")

    object PantallaSelectorTipo: Rutas(ruta = "pantallaselectortipo")

    object PantallaPokemonFav: Rutas(ruta = "pantallapokemonfav")

    object PantallaPokemon: Rutas(ruta = "Pokemon/{pokemonId}")

    object PantallaTesting: Rutas(ruta = "testing")


}