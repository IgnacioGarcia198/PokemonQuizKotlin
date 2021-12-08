package com.ignacio.pokemonquizkotlin2.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ignacio.pokemonquizkotlin2.MyApplication
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.api.NetworkPokemonContainer
import com.ignacio.pokemonquizkotlin2.data.api.PokemonService
import com.ignacio.pokemonquizkotlin2.data.api.speciesdetail.NetworkSpeciesDetail
import com.ignacio.pokemonquizkotlin2.db.DatabasePokemon
import com.ignacio.pokemonquizkotlin2.testutils.CoroutineTestRule
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel
import com.ignacio.pokemonquizkotlin2.ui.play.PlayViewModel
import com.ignacio.pokemonquizkotlin2.utils.LAST_DB_REFRESH
import com.ignacio.pokemonquizkotlin2.utils.LAST_PAGING_POKEMON_ID_KEY
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import java.util.*


@ExperimentalCoroutinesApi
class PlayViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()


    //val database : MyDatabase = mock()
    val repository: PokemonRepository = mock()
    val pokemonApiService: PokemonService = mock()
    val myApplication: MyApplication = mock()

    val mockPreferences: SharedPreferences = mock()
    val mockEditor: SharedPreferences.Editor = mock()

    private lateinit var viewModel: PlayViewModel

    fun stubSharedPrefInitial() {
        whenever(mockPreferences.edit()).doReturn(mockEditor)
        whenever(mockPreferences.getLong(LAST_DB_REFRESH, 0)).doReturn(0)
        whenever(mockEditor.putLong(any(), any())).doReturn(mockEditor)
        whenever(mockEditor.putInt(any(), any())).doReturn(mockEditor)
    }

    fun stubSharedPrefSavedValueNow() {
        whenever(mockPreferences.edit()).doReturn(mockEditor)
        whenever(mockPreferences.getLong(LAST_DB_REFRESH, 0)).doReturn(0)
        whenever(
            mockPreferences.getInt(
                LAST_PAGING_POKEMON_ID_KEY,
                0
            )
        ).doReturn(HomeViewModel.DOWNLOAD_SIZE)
        whenever(mockEditor.putLong(any(), any())).doReturn(mockEditor)
        whenever(mockEditor.putInt(any(), any())).doReturn(mockEditor)
        whenever(mockPreferences.getLong(LAST_DB_REFRESH, 0)).doReturn(Date().time / 60000)
    }

    fun stubSharedPrefSavedValueAMonthAgo() {
        whenever(mockPreferences.edit()).doReturn(mockEditor)
        whenever(mockPreferences.getLong(LAST_DB_REFRESH, 0)).doReturn(0)
        whenever(mockEditor.putLong(any(), any())).doReturn(mockEditor)
        whenever(mockEditor.putInt(any(), any())).doReturn(mockEditor)
        whenever(
            mockPreferences.getLong(
                LAST_DB_REFRESH,
                0
            )
        ).doReturn(Date().time / 60000 - 30 * 24 * 60)
    }

    /*fun stubAllPokemonInRepository() {
        whenever(repository.pokemons).doReturn (
            MutableLiveData(allPokemonsResponse!!.asDatabaseModel(0,-1).asDomainModel())
        )
    }*/

    fun stubInternetIsOk() {
        val connectivityManager: ConnectivityManager = mock()
        val networkInfo: NetworkInfo = mock()

        whenever(myApplication.getSystemService(eq(Context.CONNECTIVITY_SERVICE))).doReturn(
            connectivityManager
        )
        whenever(connectivityManager.activeNetworkInfo).doReturn(networkInfo)
        whenever(networkInfo.isConnectedOrConnecting).doReturn(true)
    }

    fun stubInternetIsDown() {
        val connectivityManager: ConnectivityManager = mock()
        val networkInfo: NetworkInfo = mock()

        whenever(myApplication.getSystemService(eq(Context.CONNECTIVITY_SERVICE))).doReturn(
            connectivityManager
        )
        whenever(connectivityManager.activeNetworkInfo).doReturn(networkInfo)
        whenever(networkInfo.isConnectedOrConnecting).doReturn(false)
    }

    @Before
    fun createViewModel() {
        //whenever(database.pokemonDao).doReturn(pokemonDao)
        //repository = PokemonRepository(database,pokemonApiService,coroutinesTestRule.testDispatcherProvider)
        //viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
    }

    /*@Suppress("UNCHECKED_CAST")
    @Test
    fun refreshPokemonCallsItsMethods() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)

        stubSharedPrefInitial()
        whenever(myApplication.getString(any())).doReturn("Refreshing pokemon…")

        stubAllPokemonInRepository()


        whenever(
            repository.refreshPokemonPlay(
                eq(0),
                eq(-1),
                any())
        ).doAnswer {
            println("arguments size: ${it.arguments.size}")
            val argument = it.arguments[2]
            val thecall = argument as (() ->Unit)
            thecall.invoke()
            ////viewModel.onFlavorTextAndNameResult(theversions, theflavorAndName)
        }

        //viewModel.refreshPokemon()

        val spy = spy(viewModel)
        spy.refreshPokemon(0,-1)
        verify(spy).initGame()

        //viewModel.refreshPokemon(0,-1)
        spy.imageVisible.observeOnce {
            assert(it == View.INVISIBLE)
        }
        spy.progressbarVisible.observeOnce {
            assert(it == View.VISIBLE)
        }
        spy.progressbarText.observeOnce {
            assert(it == "Refreshing pokemon…")
        }

        //verify(spy)
        // 2 times because the changeResponseState(PokemonResponseState.LOADING) in startQuestionsGame is called
        verify(repository, times(2)).changeResponseState(PokemonResponseState.LOADING)
        verify(repository).changeResponseState(PokemonResponseState.DONE)

    }

    @Test
    fun refreshPokemonCalledOnStartup() {
        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
        stubSharedPrefInitial()
        val spy = spy(viewModel)
        spy.initForUnitTest()
        verify(spy).refreshPokemon()
    }

    @Test
    fun noNeedToRefreshPokemonOnStartup() {
        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
        stubSharedPrefSavedValueNow()
        val spy = spy(viewModel)
        spy.initForUnitTest()
        verify(spy, never()).refreshPokemon()
        verify(spy).initGame()
        verify(spy).startQuestionsGame()
    }

    @Test
    fun initGameCallsStartQuestionsGame() {
        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
        //stubSharedPrefSavedValueNow()
        val spy = spy(viewModel)
        spy.initGame()
        //verify(spy, never()).refreshPokemon()
        //verify(spy).initGame()
        verify(spy).startQuestionsGame()
        verify(spy, never()).startTimeGame()
    }

    @Test
    fun initGameCallsStartTimeGame() {
        viewModel = PlayViewModel(myApplication,false,10,repository,mockPreferences)
        //stubSharedPrefSavedValueNow()
        val spy = spy(viewModel)
        spy.initGame()
        //verify(spy, never()).refreshPokemon()
        //verify(spy).initGame()
        verify(spy).startTimeGame()
        verify(spy, never()).startQuestionsGame()
    }

    //================================
    // Test for Questions game
    //================================
    @Test
    fun startQuestionsgameTest() {
        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
        //stubSharedPrefSavedValueNow()
        //viewModel.startQuestionsGame()
        val spy = spy(viewModel)
        spy.startQuestionsGame()

        assert(spy.timer is CountUpTimer) // timer is created as a CountUp timer
        assert(spy.timer !is CountUpDownTimer) // assert the other case is not happening
        assert(spy.timer.interval == 100L) // assert correct interval time given for the timer
        verify(spy).nextRound() // nextRound() is called

    }

    @Test
    fun nextRoundTest() {
        whenever(myApplication.getString(eq(R.string.loading))).doReturn("Loading…") // mock app.getString
        coroutinesTestRule.testDispatcher.runBlockingTest {
            whenever(repository.getNextRoundQuestionPokemon()).doReturn(pokemonList[0])
        }

        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
        val spy = spy(viewModel)
        spy.nextRound()

        spy.radiogroupEnabled.observeOnce { //_radiogroup is disabled
            assert(!it)
        }
        spy.imageVisible.observeOnce { // the image is hidden
            assert(it == View.INVISIBLE)
        }
        spy.progressbarVisible.observeOnce { // the progressbar is shown
            assert(it == View.VISIBLE)
        }
        val captor1 = argumentCaptor<Int>()

        verify(myApplication).getString(captor1.capture()) // capture argument for app.getString
        assert(captor1.firstValue == R.string.loading) // assert we are getting right string from resources
        spy.progressbarText.observeOnce { // the progressbar is shown
            assert(it == "Loading…") // we show right text
        }

        coroutinesTestRule.testDispatcher.runBlockingTest {
            verify(repository).changeResponseState(PokemonResponseState.LOADING)
            verify(repository).getNextRoundQuestionPokemon()
            //verify(verify(repository).getNextRoundQuestionPokemon())?.asDomainModel()
            verify(repository, never()).resetUsedAsQuestionPlain()
            verify(repository).updateUsedAsQuestion(eq(1), eq(true))
            assert(spy.questionPokemonId == 1)
            spy.nextRoundQuestionPokemonId.observeOnce {
                assert(it == 1)
            }
            verify(repository).getNextRoundAnswers(eq(1), eq(NUMBER_OF_ANSWERS-1))
            verify(repository).changeResponseState(PokemonResponseState.DONE)
        }

        assert(spy.rightAnswerIndex >= 0 && spy.rightAnswerIndex < NUMBER_OF_ANSWERS) // check right answer index is right
        spy.nextRoundAnswers.observeOnce {// check we got a right lisst
            var b : Boolean = true
            it.forEach {
                b = b && pokemonList.takeLast(4).map { it.name }.contains(it)
            }
            assert(b)
        }

    }

    @Test
    fun onAnimationMaxedTest() {
        viewModel = PlayViewModel(myApplication,false,10,repository,mockPreferences)
        val spy = spy(viewModel)
        spy.onAnimationMaxed()

        coroutinesTestRule.testDispatcher.runBlockingTest {
            verify(spy).dispatchers.default()
            delay(100)
            verify(spy).dispatchers.main()
            //verify(spy).resetAnimation()
            //verify(spy).onAnswerChosen(eq(-1))
            //withContext(coroutinesTestRule.testDispatcherProvider.default()) {
                //delay(100)

                //withContext(coroutinesTestRule.testDispatcherProvider.main()) {

                //}

            //}
        }



    }

    @Test
    fun resetAnimationTest() {
        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
        viewModel.animationLevel.observeOnce {
            assert(it == 0f)
        }
        assert(viewModel.currentAnimationTime == 0L)
    }

    @Test
    fun onLoadImageFailedTest() {

        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
        //val spy = spy(viewModel)
        viewModel.timer = mock()
        //spy.startQuestionsGame()
        viewModel.onLoadImageFailed()

        verify(viewModel.timer).stop() // timer.stop() called
        viewModel.progressbarVisible.observeOnce {
            assert(it == View.INVISIBLE) // progressbar is hidden
        }
        viewModel.showError.observeOnce {
            assert(it) // error is shown
        }

    }

    @Test
    fun onLoadImageSuccessTest() {
        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
        val spy = spy(viewModel)
        viewModel.timer = mock()
        viewModel.onLoadImageSuccess()
        viewModel.progressbarVisible.observeOnce {
            assert(it == View.INVISIBLE)
        }
        viewModel.imageVisible.observeOnce {
            assert(it == View.VISIBLE)
        }
        viewModel.radiogroupEnabled.observeOnce {
            assert(it)
        }

        verify(viewModel.timer).start()
    }

    @Test
    fun onAnswerChosenRightTest() {
        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
        val spy = spy(viewModel)
        spy.timer = mock()
        spy.rightAnswerIndex = 1
        spy.onAnswerChosen(1)


        verify(spy.timer).pause()
        verify(spy).resetAnimation()
        spy.rightAnswersCount.observeOnce {
            assert(it == 1)
        }
        spy.lastResult.observeOnce {
            assert(it!!)
        }

    }

    @Test
    fun onAnswerChosenWrongTest() {
        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
        val spy = spy(viewModel)
        spy.timer = mock()
        spy.rightAnswerIndex = 2
        spy.onAnswerChosen(1)


        verify(spy.timer).pause()
        verify(spy).resetAnimation()
        spy.wrongAnswersCount.observeOnce {
            assert(it == 1)
        }
        spy.lastResult.observeOnce {
            assert(!it!!)
        }

    }

    @Test
    fun onResultShownQuestionsContinueGameTest() {
        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
        val spy = spy(viewModel)
        spy.roundNumber = 1
        spy.onResultShown()
        assert(spy.roundNumber == 2)
        verify(spy).nextRound()
        // reset everything for next round

    }

    @Test
    fun onResultShownQuestionsFinishGameTest() {
        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
        val spy = spy(viewModel)
        spy.timer = mock()
        spy.roundNumber = 9
        spy.onResultShown()
        assert(spy.roundNumber == 10)
        verify(spy).finishGame()

    }

    @Test
    fun onResultShownTimeContinueGameTest() {
        viewModel = PlayViewModel(myApplication,false,10,repository,mockPreferences)
        val spy = spy(viewModel)
        spy.roundNumber = 9
        spy.onResultShown()
        assert(spy.roundNumber == 10)
        verify(spy).nextRound()
        verify(spy, never()).finishGame()
        // reset everything for next round

    }

    @Test
    fun finishGameTimeTest() {
        viewModel = PlayViewModel(myApplication,false,10,repository,mockPreferences)
        viewModel.timer = mock()
        viewModel.roundNumber = 10
        viewModel._rightAnswersCount.value = 5
        viewModel.finishGame()

        verify(viewModel.timer).stop()
        viewModel.radiogroupEnabled.observeOnce {
            assert(!it)
        }
        val speed = viewModel.roundNumber.toFloat() / 10
        val hitRate = viewModel.rightAnswersCount.value!!.toFloat() / viewModel.roundNumber
        viewModel.showRecords.observeOnce {
            assert(
                !it!!.gameMode &&
                        it.gameLength == 10 &&
                        it.questionsPerSecond == speed &&
                        it.hitRate == hitRate &&
                        Date().time - it.recordTime.time < 1000
            )
        }

    }

    @Test
    fun finishGameQuestionsTest() {
        viewModel = PlayViewModel(myApplication,true,10,repository,mockPreferences)
        viewModel.timer = mock()
        whenever(viewModel.timer.elapsedTime).doReturn(10000L)
        viewModel.roundNumber = 10
        viewModel._rightAnswersCount.value = 5
        viewModel.finishGame()

        verify(viewModel.timer).stop()
        viewModel.radiogroupEnabled.observeOnce {
            assert(!it)
        }
        val speed = viewModel.roundNumber.toFloat() / viewModel.timer.elapsedTime
        val hitRate = viewModel.rightAnswersCount.value!!.toFloat() / viewModel.roundNumber
        viewModel.showRecords.observeOnce {
            assert(
                it!!.gameMode &&
                        it.gameLength == 10 &&
                        it.questionsPerSecond == speed &&
                        it.hitRate == hitRate &&
                        Date().time - it.recordTime.time < 1000
            )
        }

    }
    */


    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

    companion object {
        val speciesDetail: NetworkSpeciesDetail = Json.decodeFromString(
            "{\"base_happiness\":70,\"capture_rate\":45,\"color\":{\"name\":\"green\",\"url\":\"https://pokeapi.co/api/v2/pokemon-color/5/\"},\"egg_groups\":[{\"name\":\"plant\",\"url\":\"https://pokeapi.co/api/v2/egg-group/7/\"},{\"name\":\"monster\",\"url\":\"https://pokeapi.co/api/v2/egg-group/1/\"}],\"evolution_chain\":{\"url\":\"https://pokeapi.co/api/v2/evolution-chain/1/\"},\"evolves_from_species\":null,\"flavor_text_entries\":[{\"flavor_text\":\"日なたで　昼寝を　する　姿を　見かける。\\n太陽の　光を　いっぱい　浴びることで\\n背中の　タネが　大きく　育つのだ。\",\"language\":{\"name\":\"ja\",\"url\":\"https://pokeapi.co/api/v2/language/11/\"},\"version\":{\"name\":\"alpha-sapphire\",\"url\":\"https://pokeapi.co/api/v2/version/26/\"}},{\"flavor_text\":\"Bulbasaur can be seen napping in bright sunlight.\\nThere is a seed on its back. By soaking up the sun’s rays,\\nthe seed grows progressively larger.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"alpha-sapphire\",\"url\":\"https://pokeapi.co/api/v2/version/26/\"}},{\"flavor_text\":\"È possibile vedere Bulbasaur mentre schiaccia un pisolino\\nsotto il sole. Ha un seme piantato sulla schiena. Grazie ai\\nraggi solari il seme cresce ingrandendosi progressivamente.\",\"language\":{\"name\":\"it\",\"url\":\"https://pokeapi.co/api/v2/language/8/\"},\"version\":{\"name\":\"alpha-sapphire\",\"url\":\"https://pokeapi.co/api/v2/version/26/\"}},{\"flavor_text\":\"A Bulbasaur es fácil verle echándose una siesta al sol.\\nLa semilla que tiene en el lomo va creciendo cada vez más\\na medida que absorbe los rayos del sol.\",\"language\":{\"name\":\"es\",\"url\":\"https://pokeapi.co/api/v2/language/7/\"},\"version\":{\"name\":\"alpha-sapphire\",\"url\":\"https://pokeapi.co/api/v2/version/26/\"}},{\"flavor_text\":\"Bisasam macht gern einmal ein Nickerchen im\\nSonnenschein. Auf seinem Rücken trägt es einen\\nSamen. Indem es Sonnenstrahlen aufsaugt,\\nwird er zunehmend größer.\",\"language\":{\"name\":\"de\",\"url\":\"https://pokeapi.co/api/v2/language/6/\"},\"version\":{\"name\":\"alpha-sapphire\",\"url\":\"https://pokeapi.co/api/v2/version/26/\"}},{\"flavor_text\":\"Bulbizarre passe son temps à faire la sieste sous le soleil.\\nIl y a une graine sur son dos. Il absorbe les rayons du soleil\\npour faire doucement pousser la graine.\",\"language\":{\"name\":\"fr\",\"url\":\"https://pokeapi.co/api/v2/language/5/\"},\"version\":{\"name\":\"alpha-sapphire\",\"url\":\"https://pokeapi.co/api/v2/version/26/\"}},{\"flavor_text\":\"양지에서 낮잠 자는 모습을 볼 수 있다.\\n태양의 빛을 많이 받으면\\n등의 씨앗이 크게 자란다.\",\"language\":{\"name\":\"ko\",\"url\":\"https://pokeapi.co/api/v2/language/3/\"},\"version\":{\"name\":\"alpha-sapphire\",\"url\":\"https://pokeapi.co/api/v2/version/26/\"}},{\"flavor_text\":\"ひなたで　ひるねを　する　すがたを　 かける。\\nたいようの　ひかりを　いっぱい　あびることで\\nせなかの　タネが　おおきく　そだつのだ。\",\"language\":{\"name\":\"ja-Hrkt\",\"url\":\"https://pokeapi.co/api/v2/language/1/\"},\"version\":{\"name\":\"alpha-sapphire\",\"url\":\"https://pokeapi.co/api/v2/version/26/\"}},{\"flavor_text\":\"日なたで　昼寝を　する　姿を　見かける。\\n太陽の　光を　いっぱい　浴びることで\\n背中の　タネが　大きく　育つのだ。\",\"language\":{\"name\":\"ja\",\"url\":\"https://pokeapi.co/api/v2/language/11/\"},\"version\":{\"name\":\"omega-ruby\",\"url\":\"https://pokeapi.co/api/v2/version/25/\"}},{\"flavor_text\":\"Bulbasaur can be seen napping in bright sunlight.\\nThere is a seed on its back. By soaking up the sun’s rays,\\nthe seed grows progressively larger.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"omega-ruby\",\"url\":\"https://pokeapi.co/api/v2/version/25/\"}},{\"flavor_text\":\"È possibile vedere Bulbasaur mentre schiaccia un pisolino\\nsotto il sole. Ha un seme piantato sulla schiena. Grazie ai\\nraggi solari il seme cresce ingrandendosi progressivamente.\",\"language\":{\"name\":\"it\",\"url\":\"https://pokeapi.co/api/v2/language/8/\"},\"version\":{\"name\":\"omega-ruby\",\"url\":\"https://pokeapi.co/api/v2/version/25/\"}},{\"flavor_text\":\"A Bulbasaur es fácil verle echándose una siesta al sol.\\nLa semilla que tiene en el lomo va creciendo cada vez más\\na medida que absorbe los rayos del sol.\",\"language\":{\"name\":\"es\",\"url\":\"https://pokeapi.co/api/v2/language/7/\"},\"version\":{\"name\":\"omega-ruby\",\"url\":\"https://pokeapi.co/api/v2/version/25/\"}},{\"flavor_text\":\"Bisasam macht gern einmal ein Nickerchen im\\nSonnenschein. Auf seinem Rücken trägt es einen\\nSamen. Indem es Sonnenstrahlen aufsaugt,\\nwird der Samen zunehmend größer.\",\"language\":{\"name\":\"de\",\"url\":\"https://pokeapi.co/api/v2/language/6/\"},\"version\":{\"name\":\"omega-ruby\",\"url\":\"https://pokeapi.co/api/v2/version/25/\"}},{\"flavor_text\":\"Bulbizarre passe son temps à faire la sieste sous le soleil.\\nIl y a une graine sur son dos. Il absorbe les rayons du soleil\\npour faire doucement pousser la graine.\",\"language\":{\"name\":\"fr\",\"url\":\"https://pokeapi.co/api/v2/language/5/\"},\"version\":{\"name\":\"omega-ruby\",\"url\":\"https://pokeapi.co/api/v2/version/25/\"}},{\"flavor_text\":\"양지에서 낮잠 자는 모습을 볼 수 있다.\\n태양의 빛을 많이 받으면\\n등의 씨앗이 크게 자란다.\",\"language\":{\"name\":\"ko\",\"url\":\"https://pokeapi.co/api/v2/language/3/\"},\"version\":{\"name\":\"omega-ruby\",\"url\":\"https://pokeapi.co/api/v2/version/25/\"}},{\"flavor_text\":\"ひなたで　ひるねを　する　すがたを　 かける。\\nたいようの　ひかりを　いっぱい　あびることで\\nせなかの　タネが　おおきく　そだつのだ。\",\"language\":{\"name\":\"ja-Hrkt\",\"url\":\"https://pokeapi.co/api/v2/language/1/\"},\"version\":{\"name\":\"omega-ruby\",\"url\":\"https://pokeapi.co/api/v2/version/25/\"}},{\"flavor_text\":\"生まれてから　しばらくの　あいだは\\n背中の　タネから　栄養を　もらって\\n大きく　育つ。\",\"language\":{\"name\":\"ja\",\"url\":\"https://pokeapi.co/api/v2/language/11/\"},\"version\":{\"name\":\"y\",\"url\":\"https://pokeapi.co/api/v2/version/24/\"}},{\"flavor_text\":\"For some time after its birth, it grows by gaining\\nnourishment from the seed on its back.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"y\",\"url\":\"https://pokeapi.co/api/v2/version/24/\"}},{\"flavor_text\":\"Dopo la nascita, cresce traendo nutrimento dal seme\\npiantato sul suo dorso.\",\"language\":{\"name\":\"it\",\"url\":\"https://pokeapi.co/api/v2/language/8/\"},\"version\":{\"name\":\"y\",\"url\":\"https://pokeapi.co/api/v2/version/24/\"}},{\"flavor_text\":\"Después de nacer, crece alimentándose de las\\nsemillas de su lomo.\",\"language\":{\"name\":\"es\",\"url\":\"https://pokeapi.co/api/v2/language/7/\"},\"version\":{\"name\":\"y\",\"url\":\"https://pokeapi.co/api/v2/version/24/\"}},{\"flavor_text\":\"Nach der Geburt nimmt es für eine Weile Nährstoffe\\nüber den Samen auf seinem Rücken auf.\",\"language\":{\"name\":\"de\",\"url\":\"https://pokeapi.co/api/v2/language/6/\"},\"version\":{\"name\":\"y\",\"url\":\"https://pokeapi.co/api/v2/version/24/\"}},{\"flavor_text\":\"Au matin de sa vie, la graine sur son dos lui fournit\\nles éléments dont il a besoin pour grandir.\",\"language\":{\"name\":\"fr\",\"url\":\"https://pokeapi.co/api/v2/language/5/\"},\"version\":{\"name\":\"y\",\"url\":\"https://pokeapi.co/api/v2/version/24/\"}},{\"flavor_text\":\"태어나서부터 얼마 동안은\\n등의 씨앗으로부터 영양을\\n공급받아 크게 성장한다.\",\"language\":{\"name\":\"ko\",\"url\":\"https://pokeapi.co/api/v2/language/3/\"},\"version\":{\"name\":\"y\",\"url\":\"https://pokeapi.co/api/v2/version/24/\"}},{\"flavor_text\":\"うまれてから　しばらくの　あいだは\\nせなかの　タネから　えいようを\\nもらって　おおきく　そだつ。\",\"language\":{\"name\":\"ja-Hrkt\",\"url\":\"https://pokeapi.co/api/v2/language/1/\"},\"version\":{\"name\":\"y\",\"url\":\"https://pokeapi.co/api/v2/version/24/\"}},{\"flavor_text\":\"生まれたときから　背中に\\n不思議な　タネが　植えてあって\\n体と　ともに　育つという。\",\"language\":{\"name\":\"ja\",\"url\":\"https://pokeapi.co/api/v2/language/11/\"},\"version\":{\"name\":\"x\",\"url\":\"https://pokeapi.co/api/v2/version/23/\"}},{\"flavor_text\":\"A strange seed was planted on its back at birth.\\nThe plant sprouts and grows with this Pokémon.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"x\",\"url\":\"https://pokeapi.co/api/v2/version/23/\"}},{\"flavor_text\":\"Alla nascita gli è stato piantato sulla schiena un seme\\nraro. La pianta sboccia e cresce con lui.\",\"language\":{\"name\":\"it\",\"url\":\"https://pokeapi.co/api/v2/language/8/\"},\"version\":{\"name\":\"x\",\"url\":\"https://pokeapi.co/api/v2/version/23/\"}},{\"flavor_text\":\"Una rara semilla le fue plantada en el lomo al nacer.\\nLa planta brota y crece con este Pokémon.\",\"language\":{\"name\":\"es\",\"url\":\"https://pokeapi.co/api/v2/language/7/\"},\"version\":{\"name\":\"x\",\"url\":\"https://pokeapi.co/api/v2/version/23/\"}},{\"flavor_text\":\"Dieses Pokémon trägt von Geburt an einen Samen\\nauf dem Rücken, der mit ihm keimt und wächst.\",\"language\":{\"name\":\"de\",\"url\":\"https://pokeapi.co/api/v2/language/6/\"},\"version\":{\"name\":\"x\",\"url\":\"https://pokeapi.co/api/v2/version/23/\"}},{\"flavor_text\":\"Il a une étrange graine plantée sur son dos.\\nElle grandit avec lui depuis sa naissance.\",\"language\":{\"name\":\"fr\",\"url\":\"https://pokeapi.co/api/v2/language/5/\"},\"version\":{\"name\":\"x\",\"url\":\"https://pokeapi.co/api/v2/version/23/\"}},{\"flavor_text\":\"태어났을 때부터 등에\\n이상한 씨앗이 심어져 있으며\\n몸과 함께 자란다고 한다.\",\"language\":{\"name\":\"ko\",\"url\":\"https://pokeapi.co/api/v2/language/3/\"},\"version\":{\"name\":\"x\",\"url\":\"https://pokeapi.co/api/v2/version/23/\"}},{\"flavor_text\":\"うまれたときから　せなかに\\nふしぎな　タネが　うえてあって\\nからだと　ともに　そだつという。\",\"language\":{\"name\":\"ja-Hrkt\",\"url\":\"https://pokeapi.co/api/v2/language/1/\"},\"version\":{\"name\":\"x\",\"url\":\"https://pokeapi.co/api/v2/version/23/\"}},{\"flavor_text\":\"For some time after its birth, it\\ngrows by gaining nourishment from\\nthe seed on its back.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"white-2\",\"url\":\"https://pokeapi.co/api/v2/version/22/\"}},{\"flavor_text\":\"For some time after its birth, it\\ngrows by gaining nourishment from\\nthe seed on its back.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"black-2\",\"url\":\"https://pokeapi.co/api/v2/version/21/\"}},{\"flavor_text\":\"For some time after its birth, it\\ngrows by gaining nourishment from\\nthe seed on its back.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"white\",\"url\":\"https://pokeapi.co/api/v2/version/18/\"}},{\"flavor_text\":\"Au matin de sa vie, la graine sur\\nson dos lui fournit les éléments\\ndont il a besoin pour grandir.\",\"language\":{\"name\":\"fr\",\"url\":\"https://pokeapi.co/api/v2/language/5/\"},\"version\":{\"name\":\"white\",\"url\":\"https://pokeapi.co/api/v2/version/18/\"}},{\"flavor_text\":\"For some time after its birth, it\\ngrows by gaining nourishment from\\nthe seed on its back.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"black\",\"url\":\"https://pokeapi.co/api/v2/version/17/\"}},{\"flavor_text\":\"Au matin de sa vie, la graine sur\\nson dos lui fournit les éléments\\ndont il a besoin pour grandir.\",\"language\":{\"name\":\"fr\",\"url\":\"https://pokeapi.co/api/v2/language/5/\"},\"version\":{\"name\":\"black\",\"url\":\"https://pokeapi.co/api/v2/version/17/\"}},{\"flavor_text\":\"It carries a seed on its back right\\nfrom birth. As it grows older, the\\nseed also grows larger.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"soulsilver\",\"url\":\"https://pokeapi.co/api/v2/version/16/\"}},{\"flavor_text\":\"The seed on its back is filled\\nwith nutrients. The seed grows\\nsteadily larger as its body grows.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"heartgold\",\"url\":\"https://pokeapi.co/api/v2/version/15/\"}},{\"flavor_text\":\"For some time after its birth, it\\ngrows by gaining nourishment from\\nthe seed on its back.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"platinum\",\"url\":\"https://pokeapi.co/api/v2/version/14/\"}},{\"flavor_text\":\"For some time after its birth, it\\ngrows by gaining nourishment from\\nthe seed on its back.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"pearl\",\"url\":\"https://pokeapi.co/api/v2/version/13/\"}},{\"flavor_text\":\"For some time after its birth, it\\ngrows by gaining nourishment from\\nthe seed on its back.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"diamond\",\"url\":\"https://pokeapi.co/api/v2/version/12/\"}},{\"flavor_text\":\"A strange seed was planted on its back at\\nbirth. The plant sprouts and grows with\\nthis POKéMON.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"leafgreen\",\"url\":\"https://pokeapi.co/api/v2/version/11/\"}},{\"flavor_text\":\"There is a plant seed on its back right\\nfrom the day this POKéMON is born.\\nThe seed slowly grows larger.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"firered\",\"url\":\"https://pokeapi.co/api/v2/version/10/\"}},{\"flavor_text\":\"BULBASAUR can be seen napping in bright\\nsunlight. There is a seed on its back.\\nBy soaking up the sun’s rays, the seed\\ngrows progressively larger.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"emerald\",\"url\":\"https://pokeapi.co/api/v2/version/9/\"}},{\"flavor_text\":\"BULBASAUR can be seen napping in\\nbright sunlight.\\nThere is a seed on its back.\\fBy soaking up the sun’s rays, the seed\\ngrows progressively larger.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"sapphire\",\"url\":\"https://pokeapi.co/api/v2/version/8/\"}},{\"flavor_text\":\"BULBASAUR can be seen napping in\\nbright sunlight.\\nThere is a seed on its back.\\fBy soaking up the sun’s rays, the seed\\ngrows progressively larger.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"ruby\",\"url\":\"https://pokeapi.co/api/v2/version/7/\"}},{\"flavor_text\":\"While it is young,\\nit uses the\\nnutrients that are\\fstored in the\\nseeds on its back\\nin order to grow.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"crystal\",\"url\":\"https://pokeapi.co/api/v2/version/6/\"}},{\"flavor_text\":\"It carries a seed\\non its back right\\nfrom birth. As it\\fgrows older, the\\nseed also grows\\nlarger.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"silver\",\"url\":\"https://pokeapi.co/api/v2/version/5/\"}},{\"flavor_text\":\"The seed on its\\nback is filled\\nwith nutrients.\\fThe seed grows\\nsteadily larger as\\nits body grows.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"gold\",\"url\":\"https://pokeapi.co/api/v2/version/4/\"}},{\"flavor_text\":\"It can go for days\\nwithout eating a\\nsingle morsel.\\fIn the bulb on\\nits back, it\\nstores energy.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"yellow\",\"url\":\"https://pokeapi.co/api/v2/version/3/\"}},{\"flavor_text\":\"A strange seed was\\nplanted on its\\nback at birth.\\fThe plant sprouts\\nand grows with\\nthis POKéMON.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"blue\",\"url\":\"https://pokeapi.co/api/v2/version/2/\"}},{\"flavor_text\":\"A strange seed was\\nplanted on its\\nback at birth.\\fThe plant sprouts\\nand grows with\\nthis POKéMON.\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"version\":{\"name\":\"red\",\"url\":\"https://pokeapi.co/api/v2/version/1/\"}}],\"form_descriptions\":[],\"forms_switchable\":false,\"gender_rate\":1,\"genera\":[{\"genus\":\"种子宝可梦\",\"language\":{\"name\":\"zh-Hans\",\"url\":\"https://pokeapi.co/api/v2/language/12/\"}},{\"genus\":\"たねポケモン\",\"language\":{\"name\":\"ja\",\"url\":\"https://pokeapi.co/api/v2/language/11/\"}},{\"genus\":\"Seed Pokémon\",\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"}},{\"genus\":\"Pokémon Seme\",\"language\":{\"name\":\"it\",\"url\":\"https://pokeapi.co/api/v2/language/8/\"}},{\"genus\":\"Pokémon Semilla\",\"language\":{\"name\":\"es\",\"url\":\"https://pokeapi.co/api/v2/language/7/\"}},{\"genus\":\"Samen\",\"language\":{\"name\":\"de\",\"url\":\"https://pokeapi.co/api/v2/language/6/\"}},{\"genus\":\"Pokémon Graine\",\"language\":{\"name\":\"fr\",\"url\":\"https://pokeapi.co/api/v2/language/5/\"}},{\"genus\":\"種子寶可夢\",\"language\":{\"name\":\"zh-Hant\",\"url\":\"https://pokeapi.co/api/v2/language/4/\"}},{\"genus\":\"씨앗포켓몬\",\"language\":{\"name\":\"ko\",\"url\":\"https://pokeapi.co/api/v2/language/3/\"}},{\"genus\":\"たねポケモン\",\"language\":{\"name\":\"ja-Hrkt\",\"url\":\"https://pokeapi.co/api/v2/language/1/\"}}],\"generation\":{\"name\":\"generation-i\",\"url\":\"https://pokeapi.co/api/v2/generation/1/\"},\"growth_rate\":{\"name\":\"medium-slow\",\"url\":\"https://pokeapi.co/api/v2/growth-rate/4/\"},\"habitat\":{\"name\":\"grassland\",\"url\":\"https://pokeapi.co/api/v2/pokemon-habitat/3/\"},\"has_gender_differences\":false,\"hatch_counter\":20,\"id\":1,\"is_baby\":false,\"name\":\"bulbasaur\",\"names\":[{\"language\":{\"name\":\"zh-Hans\",\"url\":\"https://pokeapi.co/api/v2/language/12/\"},\"name\":\"妙蛙种子\"},{\"language\":{\"name\":\"ja\",\"url\":\"https://pokeapi.co/api/v2/language/11/\"},\"name\":\"フシギダネ\"},{\"language\":{\"name\":\"en\",\"url\":\"https://pokeapi.co/api/v2/language/9/\"},\"name\":\"Bulbasaur\"},{\"language\":{\"name\":\"it\",\"url\":\"https://pokeapi.co/api/v2/language/8/\"},\"name\":\"Bulbasaur\"},{\"language\":{\"name\":\"es\",\"url\":\"https://pokeapi.co/api/v2/language/7/\"},\"name\":\"Bulbasaur\"},{\"language\":{\"name\":\"de\",\"url\":\"https://pokeapi.co/api/v2/language/6/\"},\"name\":\"Bisasam\"},{\"language\":{\"name\":\"fr\",\"url\":\"https://pokeapi.co/api/v2/language/5/\"},\"name\":\"Bulbizarre\"},{\"language\":{\"name\":\"zh-Hant\",\"url\":\"https://pokeapi.co/api/v2/language/4/\"},\"name\":\"妙蛙種子\"},{\"language\":{\"name\":\"ko\",\"url\":\"https://pokeapi.co/api/v2/language/3/\"},\"name\":\"이상해씨\"},{\"language\":{\"name\":\"roomaji\",\"url\":\"https://pokeapi.co/api/v2/language/2/\"},\"name\":\"Fushigidane\"},{\"language\":{\"name\":\"ja-Hrkt\",\"url\":\"https://pokeapi.co/api/v2/language/1/\"},\"name\":\"フシギダネ\"}],\"order\":1,\"pal_park_encounters\":[{\"area\":{\"name\":\"field\",\"url\":\"https://pokeapi.co/api/v2/pal-park-area/2/\"},\"base_score\":50,\"rate\":30}],\"pokedex_numbers\":[{\"entry_number\":80,\"pokedex\":{\"name\":\"kalos-central\",\"url\":\"https://pokeapi.co/api/v2/pokedex/12/\"}},{\"entry_number\":231,\"pokedex\":{\"name\":\"updated-johto\",\"url\":\"https://pokeapi.co/api/v2/pokedex/7/\"}},{\"entry_number\":226,\"pokedex\":{\"name\":\"original-johto\",\"url\":\"https://pokeapi.co/api/v2/pokedex/3/\"}},{\"entry_number\":1,\"pokedex\":{\"name\":\"kanto\",\"url\":\"https://pokeapi.co/api/v2/pokedex/2/\"}},{\"entry_number\":1,\"pokedex\":{\"name\":\"national\",\"url\":\"https://pokeapi.co/api/v2/pokedex/1/\"}}],\"shape\":{\"name\":\"quadruped\",\"url\":\"https://pokeapi.co/api/v2/pokemon-shape/8/\"},\"varieties\":[{\"is_default\":true,\"pokemon\":{\"name\":\"bulbasaur\",\"url\":\"https://pokeapi.co/api/v2/pokemon/1/\"}}]}"
        )
        val theversions = listOf(
            "alpha-sapphire",
            "omega-ruby",
            "y",
            "x",
            "white-2",
            "black-2",
            "white",
            "black",
            "soulsilver",
            "heartgold",
            "platinum",
            "pearl",
            "diamond",
            "leafgreen",
            "firered",
            "emerald",
            "sapphire",
            "ruby",
            "crystal",
            "silver",
            "gold",
            "yellow",
            "blue",
            "red"
        )
        val theflavorAndName: Pair<String, String> = Pair(
            "Bulbasaur can be seen napping in bright sunlight. There is a seed on its back. By soaking up the sun’s rays, the seed grows progressively larger.",
            "Bulbasaur"
        )
        val theflavorTexts =
            "A strange seed was planted on its back at birth. The plant sprouts and grows with this POKéMON."
        val flavorsMap = mapOf(
            "alpha-sapphire" to "Bulbasaur can be seen napping in bright sunlight. There is a seed on its back. By soaking up the sun’s rays, the seed grows progressively larger.",
            "omega-ruby" to "Bulbasaur can be seen napping in bright sunlight. There is a seed on its back. By soaking up the sun’s rays, the seed grows progressively larger.",
            "y" to "For some time after its birth, it grows by gaining nourishment from the seed on its back.",
            "x" to "A strange seed was planted on its back at birth. The plant sprouts and grows with this Pokémon.",
            "white-2" to "For some time after its birth, it grows by gaining nourishment from the seed on its back.",
            "black-2" to "For some time after its birth, it grows by gaining nourishment from the seed on its back.",
            "white" to "For some time after its birth, it grows by gaining nourishment from the seed on its back.",
            "black" to "For some time after its birth, it grows by gaining nourishment from the seed on its back.",
            "soulsilver" to "It carries a seed on its back right from birth. As it grows older, the seed also grows larger.",
            "heartgold" to "The seed on its back is filled with nutrients. The seed grows steadily larger as its body grows.",
            "platinum" to "For some time after its birth, it grows by gaining nourishment from the seed on its back.",
            "pearl" to "For some time after its birth, it grows by gaining nourishment from the seed on its back.",
            "diamond" to "For some time after its birth, it grows by gaining nourishment from the seed on its back.",
            "leafgreen" to "A strange seed was planted on its back at birth. The plant sprouts and grows with this POKéMON.",
            "firered" to "There is a plant seed on its back right from the day this POKéMON is born. The seed slowly grows larger.",
            "emerald" to "BULBASAUR can be seen napping in bright sunlight. There is a seed on its back. By soaking up the sun’s rays, the seed grows progressively larger.",
            "sapphire" to "BULBASAUR can be seen napping in bright sunlight. There is a seed on its back. By soaking up the sun’s rays, the seed grows progressively larger.",
            "ruby" to "BULBASAUR can be seen napping in bright sunlight. There is a seed on its back. By soaking up the sun’s rays, the seed grows progressively larger.",
            "crystal" to "While it is young, it uses the nutrients that are stored in the seeds on its back in order to grow.",
            "silver" to "It carries a seed on its back right from birth. As it grows older, the seed also grows larger.",
            "gold" to "The seed on its back is filled with nutrients. The seed grows steadily larger as its body grows.",
            "yellow" to "It can go for days without eating a single morsel. In the bulb on its back, it stores energy.",
            "blue" to "A strange seed was planted on its back at birth. The plant sprouts and grows with this POKéMON.",
            "red" to "A strange seed was planted on its back at birth. The plant sprouts and grows with this POKéMON."
        )
        val allPokemonsResponse: NetworkPokemonContainer = Json.decodeFromString(
            "{\"count\":807,\"next\":null,\"previous\":null,\"results\":[{\"name\":\"bulbasaur\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/1/\"},{\"name\":\"ivysaur\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/2/\"},{\"name\":\"venusaur\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/3/\"},{\"name\":\"charmander\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/4/\"},{\"name\":\"charmeleon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/5/\"},{\"name\":\"charizard\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/6/\"},{\"name\":\"squirtle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/7/\"},{\"name\":\"wartortle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/8/\"},{\"name\":\"blastoise\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/9/\"},{\"name\":\"caterpie\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/10/\"},{\"name\":\"metapod\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/11/\"},{\"name\":\"butterfree\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/12/\"},{\"name\":\"weedle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/13/\"},{\"name\":\"kakuna\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/14/\"},{\"name\":\"beedrill\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/15/\"},{\"name\":\"pidgey\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/16/\"},{\"name\":\"pidgeotto\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/17/\"},{\"name\":\"pidgeot\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/18/\"},{\"name\":\"rattata\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/19/\"},{\"name\":\"raticate\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/20/\"},{\"name\":\"spearow\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/21/\"},{\"name\":\"fearow\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/22/\"},{\"name\":\"ekans\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/23/\"},{\"name\":\"arbok\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/24/\"},{\"name\":\"pikachu\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/25/\"},{\"name\":\"raichu\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/26/\"},{\"name\":\"sandshrew\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/27/\"},{\"name\":\"sandslash\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/28/\"},{\"name\":\"nidoran-f\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/29/\"},{\"name\":\"nidorina\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/30/\"},{\"name\":\"nidoqueen\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/31/\"},{\"name\":\"nidoran-m\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/32/\"},{\"name\":\"nidorino\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/33/\"},{\"name\":\"nidoking\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/34/\"},{\"name\":\"clefairy\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/35/\"},{\"name\":\"clefable\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/36/\"},{\"name\":\"vulpix\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/37/\"},{\"name\":\"ninetales\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/38/\"},{\"name\":\"jigglypuff\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/39/\"},{\"name\":\"wigglytuff\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/40/\"},{\"name\":\"zubat\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/41/\"},{\"name\":\"golbat\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/42/\"},{\"name\":\"oddish\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/43/\"},{\"name\":\"gloom\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/44/\"},{\"name\":\"vileplume\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/45/\"},{\"name\":\"paras\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/46/\"},{\"name\":\"parasect\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/47/\"},{\"name\":\"venonat\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/48/\"},{\"name\":\"venomoth\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/49/\"},{\"name\":\"diglett\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/50/\"},{\"name\":\"dugtrio\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/51/\"},{\"name\":\"meowth\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/52/\"},{\"name\":\"persian\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/53/\"},{\"name\":\"psyduck\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/54/\"},{\"name\":\"golduck\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/55/\"},{\"name\":\"mankey\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/56/\"},{\"name\":\"primeape\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/57/\"},{\"name\":\"growlithe\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/58/\"},{\"name\":\"arcanine\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/59/\"},{\"name\":\"poliwag\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/60/\"},{\"name\":\"poliwhirl\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/61/\"},{\"name\":\"poliwrath\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/62/\"},{\"name\":\"abra\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/63/\"},{\"name\":\"kadabra\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/64/\"},{\"name\":\"alakazam\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/65/\"},{\"name\":\"machop\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/66/\"},{\"name\":\"machoke\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/67/\"},{\"name\":\"machamp\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/68/\"},{\"name\":\"bellsprout\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/69/\"},{\"name\":\"weepinbell\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/70/\"},{\"name\":\"victreebel\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/71/\"},{\"name\":\"tentacool\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/72/\"},{\"name\":\"tentacruel\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/73/\"},{\"name\":\"geodude\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/74/\"},{\"name\":\"graveler\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/75/\"},{\"name\":\"golem\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/76/\"},{\"name\":\"ponyta\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/77/\"},{\"name\":\"rapidash\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/78/\"},{\"name\":\"slowpoke\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/79/\"},{\"name\":\"slowbro\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/80/\"},{\"name\":\"magnemite\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/81/\"},{\"name\":\"magneton\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/82/\"},{\"name\":\"farfetchd\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/83/\"},{\"name\":\"doduo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/84/\"},{\"name\":\"dodrio\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/85/\"},{\"name\":\"seel\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/86/\"},{\"name\":\"dewgong\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/87/\"},{\"name\":\"grimer\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/88/\"},{\"name\":\"muk\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/89/\"},{\"name\":\"shellder\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/90/\"},{\"name\":\"cloyster\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/91/\"},{\"name\":\"gastly\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/92/\"},{\"name\":\"haunter\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/93/\"},{\"name\":\"gengar\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/94/\"},{\"name\":\"onix\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/95/\"},{\"name\":\"drowzee\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/96/\"},{\"name\":\"hypno\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/97/\"},{\"name\":\"krabby\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/98/\"},{\"name\":\"kingler\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/99/\"},{\"name\":\"voltorb\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/100/\"},{\"name\":\"electrode\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/101/\"},{\"name\":\"exeggcute\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/102/\"},{\"name\":\"exeggutor\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/103/\"},{\"name\":\"cubone\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/104/\"},{\"name\":\"marowak\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/105/\"},{\"name\":\"hitmonlee\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/106/\"},{\"name\":\"hitmonchan\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/107/\"},{\"name\":\"lickitung\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/108/\"},{\"name\":\"koffing\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/109/\"},{\"name\":\"weezing\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/110/\"},{\"name\":\"rhyhorn\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/111/\"},{\"name\":\"rhydon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/112/\"},{\"name\":\"chansey\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/113/\"},{\"name\":\"tangela\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/114/\"},{\"name\":\"kangaskhan\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/115/\"},{\"name\":\"horsea\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/116/\"},{\"name\":\"seadra\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/117/\"},{\"name\":\"goldeen\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/118/\"},{\"name\":\"seaking\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/119/\"},{\"name\":\"staryu\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/120/\"},{\"name\":\"starmie\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/121/\"},{\"name\":\"mr-mime\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/122/\"},{\"name\":\"scyther\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/123/\"},{\"name\":\"jynx\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/124/\"},{\"name\":\"electabuzz\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/125/\"},{\"name\":\"magmar\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/126/\"},{\"name\":\"pinsir\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/127/\"},{\"name\":\"tauros\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/128/\"},{\"name\":\"magikarp\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/129/\"},{\"name\":\"gyarados\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/130/\"},{\"name\":\"lapras\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/131/\"},{\"name\":\"ditto\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/132/\"},{\"name\":\"eevee\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/133/\"},{\"name\":\"vaporeon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/134/\"},{\"name\":\"jolteon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/135/\"},{\"name\":\"flareon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/136/\"},{\"name\":\"porygon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/137/\"},{\"name\":\"omanyte\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/138/\"},{\"name\":\"omastar\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/139/\"},{\"name\":\"kabuto\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/140/\"},{\"name\":\"kabutops\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/141/\"},{\"name\":\"aerodactyl\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/142/\"},{\"name\":\"snorlax\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/143/\"},{\"name\":\"articuno\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/144/\"},{\"name\":\"zapdos\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/145/\"},{\"name\":\"moltres\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/146/\"},{\"name\":\"dratini\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/147/\"},{\"name\":\"dragonair\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/148/\"},{\"name\":\"dragonite\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/149/\"},{\"name\":\"mewtwo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/150/\"},{\"name\":\"mew\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/151/\"},{\"name\":\"chikorita\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/152/\"},{\"name\":\"bayleef\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/153/\"},{\"name\":\"meganium\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/154/\"},{\"name\":\"cyndaquil\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/155/\"},{\"name\":\"quilava\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/156/\"},{\"name\":\"typhlosion\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/157/\"},{\"name\":\"totodile\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/158/\"},{\"name\":\"croconaw\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/159/\"},{\"name\":\"feraligatr\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/160/\"},{\"name\":\"sentret\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/161/\"},{\"name\":\"furret\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/162/\"},{\"name\":\"hoothoot\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/163/\"},{\"name\":\"noctowl\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/164/\"},{\"name\":\"ledyba\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/165/\"},{\"name\":\"ledian\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/166/\"},{\"name\":\"spinarak\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/167/\"},{\"name\":\"ariados\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/168/\"},{\"name\":\"crobat\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/169/\"},{\"name\":\"chinchou\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/170/\"},{\"name\":\"lanturn\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/171/\"},{\"name\":\"pichu\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/172/\"},{\"name\":\"cleffa\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/173/\"},{\"name\":\"igglybuff\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/174/\"},{\"name\":\"togepi\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/175/\"},{\"name\":\"togetic\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/176/\"},{\"name\":\"natu\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/177/\"},{\"name\":\"xatu\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/178/\"},{\"name\":\"mareep\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/179/\"},{\"name\":\"flaaffy\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/180/\"},{\"name\":\"ampharos\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/181/\"},{\"name\":\"bellossom\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/182/\"},{\"name\":\"marill\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/183/\"},{\"name\":\"azumarill\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/184/\"},{\"name\":\"sudowoodo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/185/\"},{\"name\":\"politoed\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/186/\"},{\"name\":\"hoppip\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/187/\"},{\"name\":\"skiploom\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/188/\"},{\"name\":\"jumpluff\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/189/\"},{\"name\":\"aipom\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/190/\"},{\"name\":\"sunkern\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/191/\"},{\"name\":\"sunflora\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/192/\"},{\"name\":\"yanma\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/193/\"},{\"name\":\"wooper\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/194/\"},{\"name\":\"quagsire\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/195/\"},{\"name\":\"espeon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/196/\"},{\"name\":\"umbreon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/197/\"},{\"name\":\"murkrow\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/198/\"},{\"name\":\"slowking\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/199/\"},{\"name\":\"misdreavus\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/200/\"},{\"name\":\"unown\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/201/\"},{\"name\":\"wobbuffet\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/202/\"},{\"name\":\"girafarig\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/203/\"},{\"name\":\"pineco\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/204/\"},{\"name\":\"forretress\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/205/\"},{\"name\":\"dunsparce\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/206/\"},{\"name\":\"gligar\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/207/\"},{\"name\":\"steelix\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/208/\"},{\"name\":\"snubbull\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/209/\"},{\"name\":\"granbull\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/210/\"},{\"name\":\"qwilfish\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/211/\"},{\"name\":\"scizor\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/212/\"},{\"name\":\"shuckle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/213/\"},{\"name\":\"heracross\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/214/\"},{\"name\":\"sneasel\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/215/\"},{\"name\":\"teddiursa\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/216/\"},{\"name\":\"ursaring\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/217/\"},{\"name\":\"slugma\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/218/\"},{\"name\":\"magcargo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/219/\"},{\"name\":\"swinub\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/220/\"},{\"name\":\"piloswine\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/221/\"},{\"name\":\"corsola\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/222/\"},{\"name\":\"remoraid\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/223/\"},{\"name\":\"octillery\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/224/\"},{\"name\":\"delibird\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/225/\"},{\"name\":\"mantine\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/226/\"},{\"name\":\"skarmory\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/227/\"},{\"name\":\"houndour\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/228/\"},{\"name\":\"houndoom\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/229/\"},{\"name\":\"kingdra\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/230/\"},{\"name\":\"phanpy\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/231/\"},{\"name\":\"donphan\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/232/\"},{\"name\":\"porygon2\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/233/\"},{\"name\":\"stantler\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/234/\"},{\"name\":\"smeargle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/235/\"},{\"name\":\"tyrogue\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/236/\"},{\"name\":\"hitmontop\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/237/\"},{\"name\":\"smoochum\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/238/\"},{\"name\":\"elekid\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/239/\"},{\"name\":\"magby\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/240/\"},{\"name\":\"miltank\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/241/\"},{\"name\":\"blissey\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/242/\"},{\"name\":\"raikou\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/243/\"},{\"name\":\"entei\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/244/\"},{\"name\":\"suicune\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/245/\"},{\"name\":\"larvitar\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/246/\"},{\"name\":\"pupitar\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/247/\"},{\"name\":\"tyranitar\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/248/\"},{\"name\":\"lugia\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/249/\"},{\"name\":\"ho-oh\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/250/\"},{\"name\":\"celebi\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/251/\"},{\"name\":\"treecko\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/252/\"},{\"name\":\"grovyle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/253/\"},{\"name\":\"sceptile\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/254/\"},{\"name\":\"torchic\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/255/\"},{\"name\":\"combusken\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/256/\"},{\"name\":\"blaziken\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/257/\"},{\"name\":\"mudkip\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/258/\"},{\"name\":\"marshtomp\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/259/\"},{\"name\":\"swampert\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/260/\"},{\"name\":\"poochyena\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/261/\"},{\"name\":\"mightyena\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/262/\"},{\"name\":\"zigzagoon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/263/\"},{\"name\":\"linoone\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/264/\"},{\"name\":\"wurmple\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/265/\"},{\"name\":\"silcoon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/266/\"},{\"name\":\"beautifly\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/267/\"},{\"name\":\"cascoon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/268/\"},{\"name\":\"dustox\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/269/\"},{\"name\":\"lotad\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/270/\"},{\"name\":\"lombre\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/271/\"},{\"name\":\"ludicolo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/272/\"},{\"name\":\"seedot\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/273/\"},{\"name\":\"nuzleaf\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/274/\"},{\"name\":\"shiftry\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/275/\"},{\"name\":\"taillow\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/276/\"},{\"name\":\"swellow\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/277/\"},{\"name\":\"wingull\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/278/\"},{\"name\":\"pelipper\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/279/\"},{\"name\":\"ralts\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/280/\"},{\"name\":\"kirlia\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/281/\"},{\"name\":\"gardevoir\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/282/\"},{\"name\":\"surskit\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/283/\"},{\"name\":\"masquerain\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/284/\"},{\"name\":\"shroomish\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/285/\"},{\"name\":\"breloom\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/286/\"},{\"name\":\"slakoth\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/287/\"},{\"name\":\"vigoroth\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/288/\"},{\"name\":\"slaking\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/289/\"},{\"name\":\"nincada\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/290/\"},{\"name\":\"ninjask\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/291/\"},{\"name\":\"shedinja\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/292/\"},{\"name\":\"whismur\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/293/\"},{\"name\":\"loudred\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/294/\"},{\"name\":\"exploud\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/295/\"},{\"name\":\"makuhita\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/296/\"},{\"name\":\"hariyama\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/297/\"},{\"name\":\"azurill\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/298/\"},{\"name\":\"nosepass\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/299/\"},{\"name\":\"skitty\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/300/\"},{\"name\":\"delcatty\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/301/\"},{\"name\":\"sableye\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/302/\"},{\"name\":\"mawile\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/303/\"},{\"name\":\"aron\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/304/\"},{\"name\":\"lairon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/305/\"},{\"name\":\"aggron\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/306/\"},{\"name\":\"meditite\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/307/\"},{\"name\":\"medicham\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/308/\"},{\"name\":\"electrike\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/309/\"},{\"name\":\"manectric\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/310/\"},{\"name\":\"plusle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/311/\"},{\"name\":\"minun\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/312/\"},{\"name\":\"volbeat\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/313/\"},{\"name\":\"illumise\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/314/\"},{\"name\":\"roselia\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/315/\"},{\"name\":\"gulpin\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/316/\"},{\"name\":\"swalot\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/317/\"},{\"name\":\"carvanha\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/318/\"},{\"name\":\"sharpedo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/319/\"},{\"name\":\"wailmer\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/320/\"},{\"name\":\"wailord\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/321/\"},{\"name\":\"numel\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/322/\"},{\"name\":\"camerupt\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/323/\"},{\"name\":\"torkoal\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/324/\"},{\"name\":\"spoink\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/325/\"},{\"name\":\"grumpig\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/326/\"},{\"name\":\"spinda\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/327/\"},{\"name\":\"trapinch\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/328/\"},{\"name\":\"vibrava\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/329/\"},{\"name\":\"flygon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/330/\"},{\"name\":\"cacnea\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/331/\"},{\"name\":\"cacturne\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/332/\"},{\"name\":\"swablu\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/333/\"},{\"name\":\"altaria\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/334/\"},{\"name\":\"zangoose\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/335/\"},{\"name\":\"seviper\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/336/\"},{\"name\":\"lunatone\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/337/\"},{\"name\":\"solrock\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/338/\"},{\"name\":\"barboach\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/339/\"},{\"name\":\"whiscash\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/340/\"},{\"name\":\"corphish\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/341/\"},{\"name\":\"crawdaunt\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/342/\"},{\"name\":\"baltoy\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/343/\"},{\"name\":\"claydol\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/344/\"},{\"name\":\"lileep\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/345/\"},{\"name\":\"cradily\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/346/\"},{\"name\":\"anorith\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/347/\"},{\"name\":\"armaldo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/348/\"},{\"name\":\"feebas\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/349/\"},{\"name\":\"milotic\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/350/\"},{\"name\":\"castform\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/351/\"},{\"name\":\"kecleon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/352/\"},{\"name\":\"shuppet\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/353/\"},{\"name\":\"banette\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/354/\"},{\"name\":\"duskull\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/355/\"},{\"name\":\"dusclops\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/356/\"},{\"name\":\"tropius\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/357/\"},{\"name\":\"chimecho\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/358/\"},{\"name\":\"absol\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/359/\"},{\"name\":\"wynaut\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/360/\"},{\"name\":\"snorunt\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/361/\"},{\"name\":\"glalie\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/362/\"},{\"name\":\"spheal\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/363/\"},{\"name\":\"sealeo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/364/\"},{\"name\":\"walrein\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/365/\"},{\"name\":\"clamperl\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/366/\"},{\"name\":\"huntail\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/367/\"},{\"name\":\"gorebyss\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/368/\"},{\"name\":\"relicanth\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/369/\"},{\"name\":\"luvdisc\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/370/\"},{\"name\":\"bagon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/371/\"},{\"name\":\"shelgon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/372/\"},{\"name\":\"salamence\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/373/\"},{\"name\":\"beldum\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/374/\"},{\"name\":\"metang\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/375/\"},{\"name\":\"metagross\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/376/\"},{\"name\":\"regirock\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/377/\"},{\"name\":\"regice\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/378/\"},{\"name\":\"registeel\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/379/\"},{\"name\":\"latias\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/380/\"},{\"name\":\"latios\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/381/\"},{\"name\":\"kyogre\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/382/\"},{\"name\":\"groudon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/383/\"},{\"name\":\"rayquaza\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/384/\"},{\"name\":\"jirachi\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/385/\"},{\"name\":\"deoxys\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/386/\"},{\"name\":\"turtwig\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/387/\"},{\"name\":\"grotle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/388/\"},{\"name\":\"torterra\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/389/\"},{\"name\":\"chimchar\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/390/\"},{\"name\":\"monferno\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/391/\"},{\"name\":\"infernape\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/392/\"},{\"name\":\"piplup\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/393/\"},{\"name\":\"prinplup\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/394/\"},{\"name\":\"empoleon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/395/\"},{\"name\":\"starly\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/396/\"},{\"name\":\"staravia\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/397/\"},{\"name\":\"staraptor\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/398/\"},{\"name\":\"bidoof\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/399/\"},{\"name\":\"bibarel\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/400/\"},{\"name\":\"kricketot\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/401/\"},{\"name\":\"kricketune\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/402/\"},{\"name\":\"shinx\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/403/\"},{\"name\":\"luxio\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/404/\"},{\"name\":\"luxray\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/405/\"},{\"name\":\"budew\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/406/\"},{\"name\":\"roserade\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/407/\"},{\"name\":\"cranidos\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/408/\"},{\"name\":\"rampardos\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/409/\"},{\"name\":\"shieldon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/410/\"},{\"name\":\"bastiodon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/411/\"},{\"name\":\"burmy\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/412/\"},{\"name\":\"wormadam\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/413/\"},{\"name\":\"mothim\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/414/\"},{\"name\":\"combee\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/415/\"},{\"name\":\"vespiquen\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/416/\"},{\"name\":\"pachirisu\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/417/\"},{\"name\":\"buizel\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/418/\"},{\"name\":\"floatzel\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/419/\"},{\"name\":\"cherubi\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/420/\"},{\"name\":\"cherrim\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/421/\"},{\"name\":\"shellos\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/422/\"},{\"name\":\"gastrodon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/423/\"},{\"name\":\"ambipom\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/424/\"},{\"name\":\"drifloon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/425/\"},{\"name\":\"drifblim\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/426/\"},{\"name\":\"buneary\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/427/\"},{\"name\":\"lopunny\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/428/\"},{\"name\":\"mismagius\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/429/\"},{\"name\":\"honchkrow\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/430/\"},{\"name\":\"glameow\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/431/\"},{\"name\":\"purugly\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/432/\"},{\"name\":\"chingling\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/433/\"},{\"name\":\"stunky\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/434/\"},{\"name\":\"skuntank\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/435/\"},{\"name\":\"bronzor\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/436/\"},{\"name\":\"bronzong\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/437/\"},{\"name\":\"bonsly\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/438/\"},{\"name\":\"mime-jr\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/439/\"},{\"name\":\"happiny\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/440/\"},{\"name\":\"chatot\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/441/\"},{\"name\":\"spiritomb\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/442/\"},{\"name\":\"gible\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/443/\"},{\"name\":\"gabite\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/444/\"},{\"name\":\"garchomp\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/445/\"},{\"name\":\"munchlax\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/446/\"},{\"name\":\"riolu\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/447/\"},{\"name\":\"lucario\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/448/\"},{\"name\":\"hippopotas\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/449/\"},{\"name\":\"hippowdon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/450/\"},{\"name\":\"skorupi\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/451/\"},{\"name\":\"drapion\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/452/\"},{\"name\":\"croagunk\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/453/\"},{\"name\":\"toxicroak\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/454/\"},{\"name\":\"carnivine\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/455/\"},{\"name\":\"finneon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/456/\"},{\"name\":\"lumineon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/457/\"},{\"name\":\"mantyke\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/458/\"},{\"name\":\"snover\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/459/\"},{\"name\":\"abomasnow\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/460/\"},{\"name\":\"weavile\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/461/\"},{\"name\":\"magnezone\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/462/\"},{\"name\":\"lickilicky\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/463/\"},{\"name\":\"rhyperior\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/464/\"},{\"name\":\"tangrowth\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/465/\"},{\"name\":\"electivire\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/466/\"},{\"name\":\"magmortar\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/467/\"},{\"name\":\"togekiss\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/468/\"},{\"name\":\"yanmega\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/469/\"},{\"name\":\"leafeon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/470/\"},{\"name\":\"glaceon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/471/\"},{\"name\":\"gliscor\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/472/\"},{\"name\":\"mamoswine\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/473/\"},{\"name\":\"porygon-z\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/474/\"},{\"name\":\"gallade\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/475/\"},{\"name\":\"probopass\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/476/\"},{\"name\":\"dusknoir\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/477/\"},{\"name\":\"froslass\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/478/\"},{\"name\":\"rotom\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/479/\"},{\"name\":\"uxie\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/480/\"},{\"name\":\"mesprit\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/481/\"},{\"name\":\"azelf\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/482/\"},{\"name\":\"dialga\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/483/\"},{\"name\":\"palkia\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/484/\"},{\"name\":\"heatran\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/485/\"},{\"name\":\"regigigas\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/486/\"},{\"name\":\"giratina\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/487/\"},{\"name\":\"cresselia\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/488/\"},{\"name\":\"phione\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/489/\"},{\"name\":\"manaphy\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/490/\"},{\"name\":\"darkrai\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/491/\"},{\"name\":\"shaymin\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/492/\"},{\"name\":\"arceus\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/493/\"},{\"name\":\"victini\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/494/\"},{\"name\":\"snivy\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/495/\"},{\"name\":\"servine\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/496/\"},{\"name\":\"serperior\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/497/\"},{\"name\":\"tepig\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/498/\"},{\"name\":\"pignite\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/499/\"},{\"name\":\"emboar\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/500/\"},{\"name\":\"oshawott\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/501/\"},{\"name\":\"dewott\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/502/\"},{\"name\":\"samurott\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/503/\"},{\"name\":\"patrat\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/504/\"},{\"name\":\"watchog\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/505/\"},{\"name\":\"lillipup\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/506/\"},{\"name\":\"herdier\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/507/\"},{\"name\":\"stoutland\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/508/\"},{\"name\":\"purrloin\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/509/\"},{\"name\":\"liepard\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/510/\"},{\"name\":\"pansage\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/511/\"},{\"name\":\"simisage\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/512/\"},{\"name\":\"pansear\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/513/\"},{\"name\":\"simisear\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/514/\"},{\"name\":\"panpour\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/515/\"},{\"name\":\"simipour\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/516/\"},{\"name\":\"munna\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/517/\"},{\"name\":\"musharna\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/518/\"},{\"name\":\"pidove\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/519/\"},{\"name\":\"tranquill\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/520/\"},{\"name\":\"unfezant\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/521/\"},{\"name\":\"blitzle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/522/\"},{\"name\":\"zebstrika\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/523/\"},{\"name\":\"roggenrola\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/524/\"},{\"name\":\"boldore\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/525/\"},{\"name\":\"gigalith\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/526/\"},{\"name\":\"woobat\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/527/\"},{\"name\":\"swoobat\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/528/\"},{\"name\":\"drilbur\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/529/\"},{\"name\":\"excadrill\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/530/\"},{\"name\":\"audino\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/531/\"},{\"name\":\"timburr\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/532/\"},{\"name\":\"gurdurr\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/533/\"},{\"name\":\"conkeldurr\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/534/\"},{\"name\":\"tympole\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/535/\"},{\"name\":\"palpitoad\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/536/\"},{\"name\":\"seismitoad\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/537/\"},{\"name\":\"throh\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/538/\"},{\"name\":\"sawk\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/539/\"},{\"name\":\"sewaddle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/540/\"},{\"name\":\"swadloon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/541/\"},{\"name\":\"leavanny\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/542/\"},{\"name\":\"venipede\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/543/\"},{\"name\":\"whirlipede\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/544/\"},{\"name\":\"scolipede\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/545/\"},{\"name\":\"cottonee\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/546/\"},{\"name\":\"whimsicott\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/547/\"},{\"name\":\"petilil\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/548/\"},{\"name\":\"lilligant\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/549/\"},{\"name\":\"basculin\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/550/\"},{\"name\":\"sandile\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/551/\"},{\"name\":\"krokorok\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/552/\"},{\"name\":\"krookodile\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/553/\"},{\"name\":\"darumaka\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/554/\"},{\"name\":\"darmanitan\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/555/\"},{\"name\":\"maractus\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/556/\"},{\"name\":\"dwebble\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/557/\"},{\"name\":\"crustle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/558/\"},{\"name\":\"scraggy\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/559/\"},{\"name\":\"scrafty\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/560/\"},{\"name\":\"sigilyph\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/561/\"},{\"name\":\"yamask\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/562/\"},{\"name\":\"cofagrigus\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/563/\"},{\"name\":\"tirtouga\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/564/\"},{\"name\":\"carracosta\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/565/\"},{\"name\":\"archen\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/566/\"},{\"name\":\"archeops\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/567/\"},{\"name\":\"trubbish\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/568/\"},{\"name\":\"garbodor\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/569/\"},{\"name\":\"zorua\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/570/\"},{\"name\":\"zoroark\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/571/\"},{\"name\":\"minccino\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/572/\"},{\"name\":\"cinccino\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/573/\"},{\"name\":\"gothita\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/574/\"},{\"name\":\"gothorita\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/575/\"},{\"name\":\"gothitelle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/576/\"},{\"name\":\"solosis\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/577/\"},{\"name\":\"duosion\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/578/\"},{\"name\":\"reuniclus\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/579/\"},{\"name\":\"ducklett\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/580/\"},{\"name\":\"swanna\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/581/\"},{\"name\":\"vanillite\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/582/\"},{\"name\":\"vanillish\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/583/\"},{\"name\":\"vanilluxe\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/584/\"},{\"name\":\"deerling\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/585/\"},{\"name\":\"sawsbuck\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/586/\"},{\"name\":\"emolga\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/587/\"},{\"name\":\"karrablast\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/588/\"},{\"name\":\"escavalier\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/589/\"},{\"name\":\"foongus\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/590/\"},{\"name\":\"amoonguss\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/591/\"},{\"name\":\"frillish\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/592/\"},{\"name\":\"jellicent\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/593/\"},{\"name\":\"alomomola\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/594/\"},{\"name\":\"joltik\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/595/\"},{\"name\":\"galvantula\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/596/\"},{\"name\":\"ferroseed\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/597/\"},{\"name\":\"ferrothorn\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/598/\"},{\"name\":\"klink\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/599/\"},{\"name\":\"klang\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/600/\"},{\"name\":\"klinklang\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/601/\"},{\"name\":\"tynamo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/602/\"},{\"name\":\"eelektrik\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/603/\"},{\"name\":\"eelektross\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/604/\"},{\"name\":\"elgyem\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/605/\"},{\"name\":\"beheeyem\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/606/\"},{\"name\":\"litwick\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/607/\"},{\"name\":\"lampent\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/608/\"},{\"name\":\"chandelure\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/609/\"},{\"name\":\"axew\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/610/\"},{\"name\":\"fraxure\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/611/\"},{\"name\":\"haxorus\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/612/\"},{\"name\":\"cubchoo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/613/\"},{\"name\":\"beartic\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/614/\"},{\"name\":\"cryogonal\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/615/\"},{\"name\":\"shelmet\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/616/\"},{\"name\":\"accelgor\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/617/\"},{\"name\":\"stunfisk\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/618/\"},{\"name\":\"mienfoo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/619/\"},{\"name\":\"mienshao\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/620/\"},{\"name\":\"druddigon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/621/\"},{\"name\":\"golett\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/622/\"},{\"name\":\"golurk\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/623/\"},{\"name\":\"pawniard\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/624/\"},{\"name\":\"bisharp\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/625/\"},{\"name\":\"bouffalant\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/626/\"},{\"name\":\"rufflet\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/627/\"},{\"name\":\"braviary\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/628/\"},{\"name\":\"vullaby\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/629/\"},{\"name\":\"mandibuzz\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/630/\"},{\"name\":\"heatmor\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/631/\"},{\"name\":\"durant\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/632/\"},{\"name\":\"deino\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/633/\"},{\"name\":\"zweilous\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/634/\"},{\"name\":\"hydreigon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/635/\"},{\"name\":\"larvesta\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/636/\"},{\"name\":\"volcarona\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/637/\"},{\"name\":\"cobalion\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/638/\"},{\"name\":\"terrakion\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/639/\"},{\"name\":\"virizion\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/640/\"},{\"name\":\"tornadus\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/641/\"},{\"name\":\"thundurus\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/642/\"},{\"name\":\"reshiram\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/643/\"},{\"name\":\"zekrom\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/644/\"},{\"name\":\"landorus\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/645/\"},{\"name\":\"kyurem\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/646/\"},{\"name\":\"keldeo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/647/\"},{\"name\":\"meloetta\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/648/\"},{\"name\":\"genesect\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/649/\"},{\"name\":\"chespin\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/650/\"},{\"name\":\"quilladin\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/651/\"},{\"name\":\"chesnaught\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/652/\"},{\"name\":\"fennekin\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/653/\"},{\"name\":\"braixen\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/654/\"},{\"name\":\"delphox\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/655/\"},{\"name\":\"froakie\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/656/\"},{\"name\":\"frogadier\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/657/\"},{\"name\":\"greninja\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/658/\"},{\"name\":\"bunnelby\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/659/\"},{\"name\":\"diggersby\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/660/\"},{\"name\":\"fletchling\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/661/\"},{\"name\":\"fletchinder\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/662/\"},{\"name\":\"talonflame\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/663/\"},{\"name\":\"scatterbug\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/664/\"},{\"name\":\"spewpa\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/665/\"},{\"name\":\"vivillon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/666/\"},{\"name\":\"litleo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/667/\"},{\"name\":\"pyroar\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/668/\"},{\"name\":\"flabebe\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/669/\"},{\"name\":\"floette\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/670/\"},{\"name\":\"florges\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/671/\"},{\"name\":\"skiddo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/672/\"},{\"name\":\"gogoat\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/673/\"},{\"name\":\"pancham\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/674/\"},{\"name\":\"pangoro\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/675/\"},{\"name\":\"furfrou\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/676/\"},{\"name\":\"espurr\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/677/\"},{\"name\":\"meowstic\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/678/\"},{\"name\":\"honedge\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/679/\"},{\"name\":\"doublade\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/680/\"},{\"name\":\"aegislash\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/681/\"},{\"name\":\"spritzee\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/682/\"},{\"name\":\"aromatisse\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/683/\"},{\"name\":\"swirlix\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/684/\"},{\"name\":\"slurpuff\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/685/\"},{\"name\":\"inkay\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/686/\"},{\"name\":\"malamar\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/687/\"},{\"name\":\"binacle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/688/\"},{\"name\":\"barbaracle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/689/\"},{\"name\":\"skrelp\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/690/\"},{\"name\":\"dragalge\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/691/\"},{\"name\":\"clauncher\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/692/\"},{\"name\":\"clawitzer\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/693/\"},{\"name\":\"helioptile\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/694/\"},{\"name\":\"heliolisk\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/695/\"},{\"name\":\"tyrunt\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/696/\"},{\"name\":\"tyrantrum\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/697/\"},{\"name\":\"amaura\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/698/\"},{\"name\":\"aurorus\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/699/\"},{\"name\":\"sylveon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/700/\"},{\"name\":\"hawlucha\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/701/\"},{\"name\":\"dedenne\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/702/\"},{\"name\":\"carbink\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/703/\"},{\"name\":\"goomy\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/704/\"},{\"name\":\"sliggoo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/705/\"},{\"name\":\"goodra\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/706/\"},{\"name\":\"klefki\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/707/\"},{\"name\":\"phantump\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/708/\"},{\"name\":\"trevenant\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/709/\"},{\"name\":\"pumpkaboo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/710/\"},{\"name\":\"gourgeist\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/711/\"},{\"name\":\"bergmite\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/712/\"},{\"name\":\"avalugg\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/713/\"},{\"name\":\"noibat\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/714/\"},{\"name\":\"noivern\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/715/\"},{\"name\":\"xerneas\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/716/\"},{\"name\":\"yveltal\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/717/\"},{\"name\":\"zygarde\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/718/\"},{\"name\":\"diancie\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/719/\"},{\"name\":\"hoopa\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/720/\"},{\"name\":\"volcanion\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/721/\"},{\"name\":\"rowlet\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/722/\"},{\"name\":\"dartrix\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/723/\"},{\"name\":\"decidueye\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/724/\"},{\"name\":\"litten\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/725/\"},{\"name\":\"torracat\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/726/\"},{\"name\":\"incineroar\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/727/\"},{\"name\":\"popplio\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/728/\"},{\"name\":\"brionne\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/729/\"},{\"name\":\"primarina\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/730/\"},{\"name\":\"pikipek\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/731/\"},{\"name\":\"trumbeak\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/732/\"},{\"name\":\"toucannon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/733/\"},{\"name\":\"yungoos\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/734/\"},{\"name\":\"gumshoos\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/735/\"},{\"name\":\"grubbin\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/736/\"},{\"name\":\"charjabug\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/737/\"},{\"name\":\"vikavolt\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/738/\"},{\"name\":\"crabrawler\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/739/\"},{\"name\":\"crabominable\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/740/\"},{\"name\":\"oricorio\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/741/\"},{\"name\":\"cutiefly\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/742/\"},{\"name\":\"ribombee\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/743/\"},{\"name\":\"rockruff\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/744/\"},{\"name\":\"lycanroc\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/745/\"},{\"name\":\"wishiwashi\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/746/\"},{\"name\":\"mareanie\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/747/\"},{\"name\":\"toxapex\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/748/\"},{\"name\":\"mudbray\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/749/\"},{\"name\":\"mudsdale\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/750/\"},{\"name\":\"dewpider\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/751/\"},{\"name\":\"araquanid\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/752/\"},{\"name\":\"fomantis\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/753/\"},{\"name\":\"lurantis\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/754/\"},{\"name\":\"morelull\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/755/\"},{\"name\":\"shiinotic\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/756/\"},{\"name\":\"salandit\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/757/\"},{\"name\":\"salazzle\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/758/\"},{\"name\":\"stufful\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/759/\"},{\"name\":\"bewear\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/760/\"},{\"name\":\"bounsweet\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/761/\"},{\"name\":\"steenee\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/762/\"},{\"name\":\"tsareena\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/763/\"},{\"name\":\"comfey\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/764/\"},{\"name\":\"oranguru\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/765/\"},{\"name\":\"passimian\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/766/\"},{\"name\":\"wimpod\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/767/\"},{\"name\":\"golisopod\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/768/\"},{\"name\":\"sandygast\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/769/\"},{\"name\":\"palossand\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/770/\"},{\"name\":\"pyukumuku\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/771/\"},{\"name\":\"type-null\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/772/\"},{\"name\":\"silvally\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/773/\"},{\"name\":\"minior\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/774/\"},{\"name\":\"komala\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/775/\"},{\"name\":\"turtonator\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/776/\"},{\"name\":\"togedemaru\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/777/\"},{\"name\":\"mimikyu\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/778/\"},{\"name\":\"bruxish\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/779/\"},{\"name\":\"drampa\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/780/\"},{\"name\":\"dhelmise\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/781/\"},{\"name\":\"jangmo-o\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/782/\"},{\"name\":\"hakamo-o\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/783/\"},{\"name\":\"kommo-o\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/784/\"},{\"name\":\"tapu-koko\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/785/\"},{\"name\":\"tapu-lele\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/786/\"},{\"name\":\"tapu-bulu\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/787/\"},{\"name\":\"tapu-fini\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/788/\"},{\"name\":\"cosmog\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/789/\"},{\"name\":\"cosmoem\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/790/\"},{\"name\":\"solgaleo\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/791/\"},{\"name\":\"lunala\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/792/\"},{\"name\":\"nihilego\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/793/\"},{\"name\":\"buzzwole\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/794/\"},{\"name\":\"pheromosa\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/795/\"},{\"name\":\"xurkitree\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/796/\"},{\"name\":\"celesteela\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/797/\"},{\"name\":\"kartana\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/798/\"},{\"name\":\"guzzlord\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/799/\"},{\"name\":\"necrozma\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/800/\"},{\"name\":\"magearna\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/801/\"},{\"name\":\"marshadow\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/802/\"},{\"name\":\"poipole\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/803/\"},{\"name\":\"naganadel\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/804/\"},{\"name\":\"stakataka\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/805/\"},{\"name\":\"blacephalon\",\"url\":\"https://pokeapi.co/api/v2/pokemon-species/806/\"}]}"
        )

        val pokemonList = listOf<DatabasePokemon>(
            DatabasePokemon(1, "pokemon 1", usedAsQuestion = false),
            DatabasePokemon(2, "pokemon 2", usedAsQuestion = false),
            DatabasePokemon(3, "pokemon 3", usedAsQuestion = false),
            DatabasePokemon(4, "pokemon 4", usedAsQuestion = false),
            DatabasePokemon(5, "pokemon 5", usedAsQuestion = false)
        )
    }

}

