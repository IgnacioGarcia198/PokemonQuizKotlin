package com.ignacio.pokemonquizkotlin2.data

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.ignacio.pokemonquizkotlin2.data.api.PokemonNetwork
import com.ignacio.pokemonquizkotlin2.data.api.PokemonService
import com.ignacio.pokemonquizkotlin2.db.MyDatabase
import timber.log.Timber

object ServiceLocator {
    const val PREFERENCE_FILE_NAME = "customPrefs.pref"



    /*@Volatile
    var pokemonRepository : PokemonRepository? = null
    @VisibleForTesting set

    val pokemonApiService = PokemonNetwork.createService()*/



    /*fun getRepository(context: Context) : PokemonRepository {
        if(pokemonRepository == null) {
            pokemonRepository = PokemonRepository(getDatabase(context))
        }
        Timber.i("repository is $pokemonRepository")
        return pokemonRepository!!
    }*/

    fun initializeServiceLocator(context: Context) {


    }



}