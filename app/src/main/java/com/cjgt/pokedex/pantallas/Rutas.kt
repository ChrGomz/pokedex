package com.cjgt.pokedex.pantallas

sealed class Rutas(var ruta: String) {
    object PantallaLogin: Rutas(ruta = "pantallalogin")

    object PantallaInicio: Rutas(ruta = "pantallainicio")

    object PantallaAssets: Rutas(ruta = "pantallaassets")

    object PantallaTesting: Rutas(ruta = "testing")


}