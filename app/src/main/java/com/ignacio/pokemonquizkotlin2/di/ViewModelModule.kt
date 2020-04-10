/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.ignacio.pokemonquizkotlin2.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ignacio.pokemonquizkotlin2.ui.PokemonViewModelFactory
import com.ignacio.pokemonquizkotlin2.ui.choosequiz.ChooseQuizViewModel
import com.ignacio.pokemonquizkotlin2.ui.gamerecords.GameRecordsViewModel
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel
import com.ignacio.pokemonquizkotlin2.ui.play.PlayViewModel
import com.ignacio.pokemonquizkotlin2.ui.pokemonlist.PokemonListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel : HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChooseQuizViewModel::class)
    abstract fun bindChooseQuizViewModel(chooseQuizViewModel: ChooseQuizViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PokemonListViewModel::class)
    abstract fun bindPokemonListViewModel(pokemonListViewModel : PokemonListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlayViewModel::class)
    abstract fun bindPlayViewModel(playViewModel : PlayViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameRecordsViewModel::class)
    abstract fun bindGameRecordsViewModel(gameRecordsViewModel: GameRecordsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: PokemonViewModelFactory): ViewModelProvider.Factory
}
