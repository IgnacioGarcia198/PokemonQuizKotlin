package com.ignacio.pokemonquizkotlin2.ui.gamerecords

import android.app.Application
import androidx.lifecycle.*
import com.ignacio.pokemonquizkotlin2.MyApplication
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import com.ignacio.pokemonquizkotlin2.db.GameRecord
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModel
import com.ignacio.pokemonquizkotlin2.utils.DefaultDispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.DispatcherProvider
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class GameRecordsViewModel @Inject constructor(
    app : Application,
    repository: PokemonRepositoryInterface,
    dispatchers: DispatcherProvider = DefaultDispatcherProvider()) : BaseViewModel(app,repository) {


    val allRecords = repository.getAllRecords()

    /*init {
        saveRecord(lastRecord)
    }*/
    private lateinit var lastRecord: GameRecord

    fun setRecord(record: GameRecord) {
        if(record != lastRecord) {
            lastRecord = record
            saveRecord(record)
        }
    }

    fun saveRecord(record : GameRecord) {
        println("saverecord called")
        viewModelScope.launch {
            try {
            repository.changeResponseState(PokemonResponseState.LOADING)
            repository.saveRecord(record)
            repository.changeResponseState(PokemonResponseState.DONE)
            }
            catch (e : IOException) {
                e.printStackTrace()
                repository.changeResponseState(PokemonResponseState.DB_ERROR)
            }
        }
    }




}