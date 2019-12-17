package com.ignacio.pokemonquizkotlin2.data.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.paging.LimitOffsetDataSource
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ignacio.pokemonquizkotlin2.db.DatabasePokemon
import com.ignacio.pokemonquizkotlin2.db.MyDatabase
import com.ignacio.pokemonquizkotlin2.db.PokemonDao
import com.ignacio.pokemonquizkotlin2.testutils.observeOnce
import com.ignacio.pokemonquizkotlin2.testutils.test
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PokemonDaoTest {
    @get:Rule
    val testRule = InstantTaskExecutorRule()

    private lateinit var pokemonDao: PokemonDao
    private lateinit var db: MyDatabase
    //@Mock
    //private val viewStateObserver: Observer<List<DatabasePokemon>> = mockk

    companion object {
        val pokemonList = listOf<DatabasePokemon>(
            DatabasePokemon(1,"pokemon 1",usedAsQuestion = false),
            DatabasePokemon(2,"pokemon 2",usedAsQuestion = false),
            DatabasePokemon(3,"pokemon 3",usedAsQuestion = false),
            DatabasePokemon(4,"pokemon 4",usedAsQuestion = false),
            DatabasePokemon(5,"pokemon 5",usedAsQuestion = false)
        )
    }

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MyDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        pokemonDao = db.pokemonDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writePokemonAndReadInList() {

        pokemonDao.insertAll(pokemonList)
        val result = pokemonDao.getAllPokemon()
        result.test()
            .assertHasValue()
            .assertValue { it : List<DatabasePokemon> -> it == pokemonList }

        result.observeOnce {
            assertThat(it==pokemonList, `is`(true))
        }
    }

    @Test
    @Throws(Exception::class)
    fun getQuestionPokemonIdTest() {
        pokemonDao.insertAll(pokemonList)
        val nextQuestionPokemon = pokemonDao.getNextRoundQuestionPokemon()
        assertThat(pokemonList.contains(nextQuestionPokemon), `is`(true))

    }

    @Test
    @Throws(Exception::class)
    fun getNextRoundAnswersTest() {
        pokemonDao.insertAll(pokemonList)
        //val nextQuestionPokemon = pokemonDao.getNextRoundQuestionPokemon()
        val nextRoundAnswers = pokemonDao.getNextRoundAnswerPokemonNames(1,4)
        assertThat(pokemonList.map { it.name }.containsAll(nextRoundAnswers), `is`(true))
        assertThat(nextRoundAnswers.containsAll(pokemonList.takeLast(4).map { it.name }), `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun getAllPokemonsForPagingTest() {
        pokemonDao.insertAll(pokemonList)
        //val nextQuestionPokemon = pokemonDao.getNextRoundQuestionPokemon()
        val factory = pokemonDao.getAllPokemonsFromRoom()
        val allPokemon = (factory.create() as LimitOffsetDataSource).loadRange(0, 5)
        assertThat(allPokemon==pokemonList, `is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun findPokemonsForPagingTest() {
        pokemonDao.insertAll(pokemonList)
        //val nextQuestionPokemon = pokemonDao.getNextRoundQuestionPokemon()
        val factory = pokemonDao.findPokemonsByNameInRoom("pokemon 5")
        val foundPokemon = (factory.create() as LimitOffsetDataSource).loadRange(0, 5)
        assertThat(foundPokemon.size == 1 && foundPokemon[0].id == 5, `is`(true))
    }


}