package com.ignacio.pokemonquizkotlin2.data

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.ignacio.pokemonquizkotlin2.data.api.PokemonNetwork
import com.ignacio.pokemonquizkotlin2.data.api.PokemonService
import com.ignacio.pokemonquizkotlin2.db.MyDatabase
import com.ignacio.pokemonquizkotlin2.db.getDatabase
import kotlinx.coroutines.runBlocking
import timber.log.Timber

object ServiceLocator {
    const val PREFERENCE_FILE_NAME = "customPrefs.pref"

    @Volatile
    var repository : PokemonRepositoryInterface? = null
    @VisibleForTesting set

    var pokemonApiService = PokemonNetwork.createService(PokemonService::class.java)
    @VisibleForTesting set

    var database : MyDatabase? = null

    fun provideRepository(context: Context) : PokemonRepositoryInterface {
        synchronized(this) {
            database = database?: getDatabase(context)
            return repository ?: PokemonRepository(database!!)

        }
    }

    val lock = Any()
    @VisibleForTesting
    fun resetRepository() {
        Timber.i("=====================================================================")
        Timber.i("cleaning db")
        synchronized(lock) {

            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            repository = null
        }
    }

}