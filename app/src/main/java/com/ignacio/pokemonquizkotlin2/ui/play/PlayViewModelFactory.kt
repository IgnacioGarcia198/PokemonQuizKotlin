package com.ignacio.pokemonquizkotlin2.ui.play

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlayViewModelFactory(
    private val app : Application,
    private val questionsOrTime : Boolean,
    private val limitValue : Int
) : ViewModelProvider.AndroidViewModelFactory(app) {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PlayViewModel::class.java)) {
            return PlayViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}