package com.ignacio.pokemonquizkotlin2.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ignacio.pokemonquizkotlin2.MyApplication
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.api.*
import com.ignacio.pokemonquizkotlin2.db.GameRecord
import com.ignacio.pokemonquizkotlin2.testutils.CoroutineTestRule
import com.ignacio.pokemonquizkotlin2.ui.gamerecords.GameRecordsViewModel
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.After
import java.util.*


@ExperimentalCoroutinesApi
class GameRecordsViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    companion object {
        val gameRecordList = listOf<GameRecord>(
            GameRecord(0,true, 10, 0.5f, 0.2f, Date()),
            GameRecord(0,true, 11, 0.6f, 0.3f, Date()),
            GameRecord(0,true, 12, 0.7f, 0.4f, Date()),
            GameRecord(0,true, 13, 0.8f, 0.5f, Date()),
            GameRecord(0,true, 14, 0.9f, 0.6f, Date())
        )
    }




    //val database : MyDatabase = mock()
    val repository : PokemonRepository = mock()
    val pokemonApiService : PokemonService = mock()
    val myApplication : MyApplication = mock()


    private lateinit var viewModel: GameRecordsViewModel

    @Before
    fun createViewModel() {
        //whenever(database.pokemonDao).doReturn(pokemonDao)
        //repository = PokemonRepository(database,pokemonApiService,coroutinesTestRule.testDispatcherProvider)

    }

    /*@Test
    fun saveRecordOnCreateTest() {
        //viewModel.saveRecord(gameRecordList[0])
        viewModel = GameRecordsViewModel(myApplication, gameRecordList[0],repository)
        coroutinesTestRule.testDispatcher.runBlockingTest {
            verify(repository,times(1)).saveRecord(gameRecordList[0])
        }
    }

    @Test
    fun hello() {
        //viewModel.saveRecord(gameRecordList[0])
        viewModel = GameRecordsViewModel(myApplication, gameRecordList[0],repository)
        viewModel.allRecords

        verify(repository,times(1)).getAllRecords()

    }*/

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

}

