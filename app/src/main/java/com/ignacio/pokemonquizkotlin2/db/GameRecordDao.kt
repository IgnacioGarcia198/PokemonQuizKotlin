package com.ignacio.pokemonquizkotlin2.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GameRecordDao {

    @get:Query("SELECT * FROM gameRecordTable")
    val allGameRecordsLiveData: LiveData<List<GameRecord>>

    @get:Query("SELECT * FROM gameRecordTable ORDER BY hitRate DESC, questionsPerSecond DESC")
    val allGameRecords: List<GameRecord>

    @get:Query("SELECT * FROM gameRecordTable LIMIT 1")
    val oneGameRecord: GameRecord

    @get:Query("SELECT AVG(hitRate) FROM gameRecordTable")
    val averageHitRateLiveData: LiveData<Float>

    @get:Query("SELECT AVG(hitRate) FROM gameRecordTable")
    val averageHitRate: Float

    @get:Query("SELECT AVG(questionsPerSecond) FROM gameRecordTable")
    val averageSpeedLiveData: LiveData<Float>

    @get:Query("SELECT AVG(questionsPerSecond) FROM gameRecordTable")
    val averageSpeed: Float

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(record: GameRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveVarious(gameRecords: List<GameRecord>)

    @Query("SELECT * FROM gameRecordTable ORDER BY hitRate DESC, questionsPerSecond DESC LIMIT :limit")
    fun getNGameRecordsLiveData(limit: Int): LiveData<List<GameRecord>>

    @Query("DELETE FROM gameRecordTable")
    fun deleteAllGameRecords()

    // delete a given entry from database
    @Delete
    fun delete(record: GameRecord)

    // delete a given list of entries from database
    @Delete
    fun deleteVarious(records: List<GameRecord>)

    //@Query("DELETE FROM SELECT * FROM gameRecordTable ORDER BY recordTime DESC LIMIT 10")
    //void deleteOldRecords();

    @Query("SELECT * FROM gameRecordTable ORDER BY recordTime DESC LIMIT :limit")
    fun getOldRecords(limit: Int): List<GameRecord>

    @Query("SELECT COUNT(*) FROM gameRecordTable")
    fun countRecords(): Int
}

