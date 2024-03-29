package com.ignacio.pokemonquizkotlin2.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(pokemons: List<DatabasePokemon>)

    @Query("SELECT * FROM pokemonTable")
    fun getAllPokemon(): LiveData<List<DatabasePokemon>>

    // Get the question pokemon
    @Query("SELECT * FROM pokemonTable WHERE usedAsQuestion = 0 ORDER BY RANDOM() LIMIT 1")
    fun getNextRoundQuestionPokemon(): DatabasePokemon?

    // Get the answer option names
    @Query("SELECT name FROM pokemonTable WHERE id <> :id ORDER BY RANDOM() LIMIT :limit")
    fun getNextRoundAnswerPokemonNames(id: Int, limit: Int): MutableList<String>

    // update usedAsQuestion
    @Query("UPDATE pokemonTable SET usedAsQuestion = :used WHERE id = :id")
    fun updateUsedAsQuestion(id: Int, used: Boolean)

    // reset all usedAsQuestion
    @Query("UPDATE pokemonTable SET usedAsQuestion = 0")
    fun resetUsedAsQuestion()

    @Delete
    fun delete(pokemon: DatabasePokemon)

    @Query("DELETE FROM pokemonTable")
    fun deleteAllPokemon()
    //==============================================================

    // for paging
    @Query("SELECT * FROM pokemonTable")
    fun getAllPokemonsFromRoom(): DataSource.Factory<Int, DatabasePokemon>

    @Query("SELECT * FROM pokemonTable WHERE name LIKE :name")
    fun findPokemonsByNameInRoom(name: String): DataSource.Factory<Int, DatabasePokemon>
}