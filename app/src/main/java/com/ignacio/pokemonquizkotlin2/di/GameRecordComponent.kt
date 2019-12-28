package com.ignacio.pokemonquizkotlin2.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent

@Subcomponent
interface GameRecordComponent {

    @Subcomponent.Builder
    interface Builder {
        fun build(): GameRecordComponent
    }
}