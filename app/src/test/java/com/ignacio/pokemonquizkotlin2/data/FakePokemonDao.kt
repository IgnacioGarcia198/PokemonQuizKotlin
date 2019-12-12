package com.ignacio.pokemonquizkotlin2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.ignacio.pokemonquizkotlin2.db.DatabasePokemon
import com.ignacio.pokemonquizkotlin2.db.PokemonDao
import java.util.*

class FakePokemonDao /*(val pokemonList : MutableList<DatabasePokemon>) : PokemonDao {
    override fun getAllPokemonsFromRoom(): DataSource.Factory<Int, DatabasePokemon> {

    }

    override fun findPokemonsByNameInRoom(name: String): DataSource.Factory<Int, DatabasePokemon> {

    }


    override fun insertAll(pokemons: List<DatabasePokemon>) {
        pokemonList.addAll(pokemons)
    }

    override fun getAllPokemon(): LiveData<List<DatabasePokemon>> {
        return MutableLiveData<List<DatabasePokemon>>(pokemonList)
    }

    // Get the question pokemon
    override fun getNextRoundQuestionPokemon(): DatabasePokemon? {
        val unusedPokemon = pokemonList.filter { !it.usedAsQuestion }
        if(unusedPokemon.size == 0) {return null}
        return unusedPokemon[Random().nextInt(unusedPokemon.size)]
    }

    // Get the answer option names
    //@Query("SELECT name FROM pokemonTable WHERE id <> :id ORDER BY RANDOM() LIMIT :limit")
    override fun getNextRoundAnswerPokemonNames(id: Int, limit: Int): MutableList<String> {
        var availablelist = pokemonList.filter { it.id != id } as MutableList
        val outPut : MutableList<String> = mutableListOf()
        for(i in 0 until limit-1) {
            val nextPos = Random().nextInt(availablelist.size)
            val nextName = availablelist.removeAt(nextPos).name
            outPut.add(nextName)
        }
        return outPut
    }

    // update usedAsQuestion
    override fun updateUsedAsQuestion(id: Int, used: Boolean) {
        val pokemon = pokemonList.find{it.id == id}
        //pokemonList.set()
        val newPokemon = DatabasePokemon(pokemon!!.id,pokemon.name,usedAsQuestion = used)
        pokemonList[pokemonList.indexOf(pokemon)] = newPokemon
    }


    // reset all usedAsQuestion
    //@Query("UPDATE pokemonTable SET usedAsQuestion = 0")
    override fun resetUsedAsQuestion() {
        val iterator = pokemonList.iterator()
        for(i in 0 until pokemonList.size-1) {
            val pokemon = pokemonList[i]
            pokemonList[i] = DatabasePokemon(pokemon.id,pokemon.name,usedAsQuestion = false)
        }
    }

    //==============================================================

    // for paging
    /*@Query("SELECT * FROM pokemonTable")
    abstract fun getAllPokemonsFromRoom(): DataSource.Factory<Int, DatabasePokemon>

    @Query("SELECT * FROM pokemonTable WHERE name LIKE :name")
    abstract fun findPokemonsByNameInRoom(name: String): DataSource.Factory<Int, DatabasePokemon>*/
}*/