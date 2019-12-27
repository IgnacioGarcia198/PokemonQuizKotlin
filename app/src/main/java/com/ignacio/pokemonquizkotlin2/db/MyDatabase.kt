package com.ignacio.pokemonquizkotlin2.db

import android.content.Context
import androidx.room.*

@Database(entities = [DatabasePokemon::class, GameRecord::class],version = 1,exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class MyDatabase : RoomDatabase() {
    abstract val pokemonDao : PokemonDao
    abstract val gameRecordDao : GameRecordDao
}
