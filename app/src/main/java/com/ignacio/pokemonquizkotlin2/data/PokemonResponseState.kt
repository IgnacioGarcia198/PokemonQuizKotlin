package com.ignacio.pokemonquizkotlin2.data

enum class PokemonResponseState {
    NETWORK_ERROR, DB_ERROR, LOADING, DONE;
    var isFatal : Boolean = false
    fun setFatal(fatal : Boolean) : PokemonResponseState {
        isFatal = fatal
        return this
    }
}

