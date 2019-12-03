package com.ignacio.pokemonquizkotlin2.data.db

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ignacio.pokemonquizkotlin2.data.model.Pokemon

@Entity(tableName = "pokemonTable")
data class DatabasePokemon (
    @PrimaryKey
    @NonNull
    val id : Int,
    val name : String,
    val flavorText : String = "",
    val usedAsQuestion : Boolean
)

/**
 * Map DatabasePokemon to Entity Pokemon
 */
fun List<DatabasePokemon>.asDomainModel() : List<Pokemon> {
    return map {
        Pokemon(it.id, it.name, it.flavorText)
    }
}

fun DatabasePokemon.asDomainModel() :Pokemon {
    return Pokemon(id,name,flavorText)
}