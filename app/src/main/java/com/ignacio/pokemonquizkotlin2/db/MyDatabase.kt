package com.ignacio.pokemonquizkotlin2.db

import android.content.Context
import androidx.room.*

@Database(entities = [DatabasePokemon::class, GameRecord::class],version = 1,exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class MyDatabase : RoomDatabase() {
    abstract val pokemonDao : PokemonDao
    abstract val gameRecordDao : GameRecordDao
}

private lateinit var INSTANCE : MyDatabase
fun getDatabase(context: Context) : MyDatabase {
    synchronized(MyDatabase::class.java) {
        if(!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                MyDatabase::class.java, "videos.db").build()
        }
    }
    return INSTANCE
}
