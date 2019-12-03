package com.ignacio.pokemonquizkotlin2.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Pokemon (
    val id : Int,
    val name : String,
    val flavorText : String = ""
)

/*data class NetworkDetailPokemon {

}

data class MyPokemon {

}

data class MyDetailPokemon*/