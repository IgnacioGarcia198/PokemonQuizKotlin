package com.ignacio.pokemonquizkotlin2.data.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

// pokemon
data class Pokemon (
    val id : Int = 0,
    val name : String,
    val flavorText : String = ""
)

// game record
/*data class GameRecord(
    val gameMode: Boolean = false, // true for number of questions and false for time limit
    val gameModeValue: Int = 0, // seconds or number of questions
    val questionsPerSecond: Float = 0f, // seconds for 1 answer
    val hitRate: Float = 0f, // right questions/total questions percentage
    val recordTime: Date,
    val title : String
)*/

/*data class NetworkDetailPokemon {

}

data class MyPokemon {

}

data class MyDetailPokemon*/