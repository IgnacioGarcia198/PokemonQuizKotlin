package com.ignacio.pokemonquizkotlin2.db

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ignacio.pokemonquizkotlin2.data.model.Pokemon
import java.util.*

//===========================================
// POKEMON DB ENTITIES
//============================================
@Entity(tableName = "pokemonTable")
data class DatabasePokemon constructor (
    @PrimaryKey
    @NonNull
    val id : Int,
    val name : String,
    val flavorText : String = "",
    val usedAsQuestion : Boolean
)

/**
 * Map DatabasePokemon to Entity Pokemon
 */
fun List<DatabasePokemon>.asDomainModel() : List<Pokemon> {
    return map {
        Pokemon(it.id, it.name, it.flavorText)
    }
}

fun DatabasePokemon.asDomainModel() :Pokemon {
    return Pokemon(id,name,flavorText)
}

//=======================================================================
// GAME RECORDS DB ENTITIES
//=======================================================================

@Entity(tableName = "gameRecordTable")
data class GameRecord @Ignore constructor(
    val gameMode: Boolean = true, // true for number of questions and false for time limit
    val gameLength: Int = 0, // seconds or number of questions
    val questionsPerSecond: Float = 0f, // seconds for 1 answer
    val hitRate: Float = 0f, // right questions/total questions percentage
    val recordTime: Date = Date()

) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Int = 0
        private set

    constructor(
        id: Int,
        gameMode: Boolean,
        gameLength: Int,
        questionsPerSecond: Float,
        hitRate: Float,
        recordTime: Date
    ): this(
        gameMode,
        gameLength,
        questionsPerSecond,
        hitRate,
        recordTime
    ) {
        this.id = id
    }

    @Ignore
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readFloat(),
        Date(parcel.readLong())

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeByte(if (gameMode) 1 else 0)
        parcel.writeInt(gameLength)
        parcel.writeFloat(questionsPerSecond)
        parcel.writeFloat(hitRate)
        parcel.writeLong(recordTime.time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GameRecord> {
        override fun createFromParcel(parcel: Parcel): GameRecord {
            return GameRecord(parcel)
        }

        override fun newArray(size: Int): Array<GameRecord?> {
            return arrayOfNulls(size)
        }
    }
}

/*fun List<DataBaseGameRecord>.asDomainModel(defaultTitle : String) : List<GameRecord> {
    return map {
        GameRecord(
            it.gameMode,
            it.gameModeValue,
            it.questionsPerSecond,
            it.hitRate,
            it.recordTime,
            defaultTitle
        )
    }
}*/