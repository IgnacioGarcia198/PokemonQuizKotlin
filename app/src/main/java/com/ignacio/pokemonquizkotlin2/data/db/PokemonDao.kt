package com.ignacio.pokemonquizkotlin2.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(pokemons: List<DatabasePokemon>)

    @Query("SELECT * FROM pokemonTable")
    fun getAllPokemon(): LiveData<List<DatabasePokemon>>

    // Get the question pokemon
    @Query("SELECT * FROM pokemonTable WHERE usedAsQuestion = 0 ORDER BY RANDOM() LIMIT 1")
    fun getNextRoundQuestionPokemon(): DatabasePokemon

    // Get the answer option names
    @Query("SELECT name FROM pokemonTable WHERE id <> :id ORDER BY RANDOM() LIMIT :limit")
    fun getNextRoundAnswerPokemonNames(id: Int, limit: Int): MutableList<String>

    // update usedAsQuestion
    @Query("UPDATE pokemonTable SET usedAsQuestion = :used WHERE id = :id")
    fun updateUsedAsQuestion(id: Int, used: Boolean)

    // reset all usedAsQuestion
    @Query("UPDATE pokemonTable SET usedAsQuestion = 0")
    fun resetUsedAsQuestion()
}