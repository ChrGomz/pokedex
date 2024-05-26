package com.cjgt.pokedex.retrofit.pokeApi

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItem>
)

data class PokemonListItem(
    val name: String,
    val url: String
)

data class TypeResponse(
    val pokemon: List<TypePokemon>
)

data class TypePokemon(
    val slot: Int,
    val pokemon: PokemonListItem
)
