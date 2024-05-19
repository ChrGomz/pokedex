package com.cjgt.pokedex.pantallas

sealed class Rutas(var ruta: String) {
    object PantallaLogin: Rutas(ruta = "pantallalogin")

    object PantallaBuscador: Rutas(ruta = "pantallabuscador")

    object PantallaInicio: Rutas(ruta = "pantallainicio")

    object PantallaPokemon: Rutas(ruta = "pantallapokemon")

    object PantallaTesting: Rutas(ruta = "testing")


}