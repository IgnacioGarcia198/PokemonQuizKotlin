package com.ignacio.pokemonquizkotlin2.di

import com.ignacio.pokemonquizkotlin2.ui.choosequiz.ChooseQuizFragment
import com.ignacio.pokemonquizkotlin2.ui.gamerecords.GameRecordsFragment
import com.ignacio.pokemonquizkotlin2.ui.home.HomeFragment
import com.ignacio.pokemonquizkotlin2.ui.play.PlayFragment
import com.ignacio.pokemonquizkotlin2.ui.pokemonlist.PokemonListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributePlayFragment(): PlayFragment

    @ContributesAndroidInjector
    abstract fun contributeGameRecordsFragment(): GameRecordsFragment

    @ContributesAndroidInjector
    abstract fun contributePokemonListFragment(): PokemonListFragment

    @ContributesAndroidInjector
    abstract fun contributeChooseQuizFragment(): ChooseQuizFragment
}