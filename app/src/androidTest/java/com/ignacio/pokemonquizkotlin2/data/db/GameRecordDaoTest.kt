package com.ignacio.pokemonquizkotlin2.data.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ignacio.pokemonquizkotlin2.db.*
import com.ignacio.pokemonquizkotlin2.testutils.observeOnce
import com.ignacio.pokemonquizkotlin2.testutils.test
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class GameRecordDaoTest {

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    private lateinit var gameRecordDao : GameRecordDao
    private lateinit var db: MyDatabase
    //@Mock
    //private val viewStateObserver: Observer<List<DatabasePokemon>> = mockk

    companion object {
        val gameRecordList = listOf<GameRecord>(
            GameRecord(0,true, 10, 0.5f, 0.2f, Date()),
            GameRecord(0,true, 11, 0.6f, 0.3f, Date()),
            GameRecord(0,true, 12, 0.7f, 0.4f, Date()),
            GameRecord(0,true, 13, 0.8f, 0.5f, Date()),
            GameRecord(0,true, 14, 0.9f, 0.6f, Date())
        )
    }

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MyDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        gameRecordDao = db.gameRecordDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeRecordsAndReadInList() {

        gameRecordDao.saveVarious(gameRecordList)
        val result = gameRecordDao.allGameRecordsLiveData

        result.test()
            .assertHasValue()
            .assertValue {
                var b = true
                gameRecordList.forEachIndexed { index, gameRecord ->
                    val current = it[index]
                    b = b && current.gameMode == gameRecord.gameMode
                            && current.gameLength == gameRecord.gameLength
                            && current.hitRate == gameRecord.hitRate
                            && current.questionsPerSecond == gameRecord.questionsPerSecond
                            && current.recordTime == gameRecord.recordTime
                }
                b
            }

        result.observeOnce {
            Timber.i("original records: $gameRecordList")
            Timber.i("records: $it")
            var b = true
            gameRecordList.forEachIndexed { index, gameRecord ->
                val current = it[index]
                b = b && current.gameMode == gameRecord.gameMode
                        && current.gameLength == gameRecord.gameLength
                        && current.hitRate == gameRecord.hitRate
                        && current.questionsPerSecond == gameRecord.questionsPerSecond
                        && current.recordTime == gameRecord.recordTime
            }
            assert(b)
            //assertThat(it == gameRecordList, `is`(true))
            //assert(it[0].id == 1 && it[4].id == 5)
        }
    }

    @Test
    @Throws(Exception::class)
    fun getAverages() {
        gameRecordDao.saveVarious(gameRecordList)
        val averages = Pair(gameRecordDao.averageSpeed,gameRecordDao.averageHitRate)
        assertThat(averages == Pair(gameRecordList.map { it.questionsPerSecond }.toFloatArray().average().toFloat(),
            gameRecordList.map { it.hitRate }.toFloatArray().average().toFloat()), `is`(true))

    }

}