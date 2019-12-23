/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ignacio.pokemonquizkotlin2.ui

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ignacio.pokemonquizkotlin2.MyApplication
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.data.model.Pokemon
import com.ignacio.pokemonquizkotlin2.db.GameRecord
import com.ignacio.pokemonquizkotlin2.ui.choosequiz.ChooseQuizFragment
import com.ignacio.pokemonquizkotlin2.ui.choosequiz.ChooseQuizViewModel
import com.ignacio.pokemonquizkotlin2.ui.gamerecords.GameRecordsFragment
import com.ignacio.pokemonquizkotlin2.ui.gamerecords.GameRecordsViewModel
import com.ignacio.pokemonquizkotlin2.ui.home.HomeFragment
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel
import com.ignacio.pokemonquizkotlin2.ui.play.PlayFragment
import com.ignacio.pokemonquizkotlin2.ui.play.PlayViewModel
import com.ignacio.pokemonquizkotlin2.ui.pokemonlist.PokemonListFragment
import com.ignacio.pokemonquizkotlin2.ui.pokemonlist.PokemonListViewModel
import com.ignacio.pokemonquizkotlin2.utils.sharedPreferences

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class BaseViewModelFactory constructor(
    private val app : Application,
    private val repository: PokemonRepositoryInterface = PokemonRepository.getDefaultRepository(app),
    private val sharedPref: SharedPreferences = sharedPreferences
) : ViewModelProvider.AndroidViewModelFactory(app) {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(app,repository,sharedPref)
                isAssignableFrom(PokemonListViewModel::class.java) ->
                    PokemonListViewModel(app, repository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}


@Suppress("UNCHECKED_CAST")
class GameRecordsViewModelFactory constructor(
    private val app : Application,
    private val repository: PokemonRepositoryInterface = PokemonRepository.getDefaultRepository(app),
    private val lastRecord : GameRecord
    ) : ViewModelProvider.AndroidViewModelFactory(app) {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(GameRecordsViewModel::class.java) ->
                    GameRecordsViewModel(app, lastRecord, repository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}

@Suppress("UNCHECKED_CAST")
class PlayViewModelFactory constructor(
    private val app : Application,
    private val repository: PokemonRepositoryInterface = PokemonRepository.getDefaultRepository(app),
    private val sharedPref: SharedPreferences = sharedPreferences,
    private val questionsOrTime : Boolean,
    private val limitValue : Int
) : ViewModelProvider.AndroidViewModelFactory(app) {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(PlayViewModel::class.java) ->
                    PlayViewModel(app,questionsOrTime,limitValue, repository,sharedPref)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}



