package com.ignacio.pokemonquizkotlin2.ui.gamerecords

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ignacio.pokemonquizkotlin2.db.GameRecord

class GameRecordsViewModelFactory(
    private val app : Application,
    private val gameRecord : GameRecord
) : ViewModelProvider.AndroidViewModelFactory(app) {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GameRecordsViewModel::class.java)) {
            return GameRecordsViewModel(app,gameRecord) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}