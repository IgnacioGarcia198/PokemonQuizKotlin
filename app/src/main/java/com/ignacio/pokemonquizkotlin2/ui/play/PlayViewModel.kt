package com.ignacio.pokemonquizkotlin2.ui.play

import android.app.Application
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ignacio.pokemonquizkotlin2.MyApplication
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import com.ignacio.pokemonquizkotlin2.db.GameRecord
import com.ignacio.pokemonquizkotlin2.db.asDomainModel
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModel
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel
import com.ignacio.pokemonquizkotlin2.utils.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

const val NUMBER_OF_ANSWERS = 4


class PlayViewModel @Inject constructor(
    app : Application,
    repository: PokemonRepositoryInterface,
    val sharedPref: SharedPreferences

) : BaseViewModel(app,repository) {
    private var questionsOrTime : Boolean = true
    private var limitValue : Int = 0

    val sdf : SimpleDateFormat = SimpleDateFormat("mm:ss",Locale.getDefault())
    // we will show the fragment just as we start.
    private val _showChooseQuizFragment = MutableLiveData<Boolean>(false)
    /*val showChooseQuizFragment : LiveData<Boolean>
    get() = _showChooseQuizFragment*/

    /*fun chooseQuizShown() {
        _showChooseQuizFragment.value = false
    }*/

    //private val questionsOrTime = false
    @VisibleForTesting var roundNumber = 0
    //private var currentTime = 0L
    private var animationTotalTime = 4000L

    private val _nextRoundQuestionPokemonId = MutableLiveData<Int>(0)
    val nextRoundQuestionPokemonId : LiveData<Int>
        get() = _nextRoundQuestionPokemonId

    private val _nextRoundAnswers = MutableLiveData<List<String>>(listOf())
    val nextRoundAnswers : LiveData<List<String>>
        get() = _nextRoundAnswers

    @VisibleForTesting val _rightAnswersCount = MutableLiveData<Int>(0)
    val rightAnswersCount : LiveData<Int>
        get() = _rightAnswersCount

    private val _wrongAnswersCount = MutableLiveData<Int>(0)
    val wrongAnswersCount : LiveData<Int>
        get() = _wrongAnswersCount

    private val _lastResult = MutableLiveData<Boolean?>(null)
    val lastResult : LiveData<Boolean?>
        get() = _lastResult

    private val _imageVisible = MutableLiveData<Int>(View.INVISIBLE)
    val imageVisible : LiveData<Int>
        get() = _imageVisible

    private val _progressbarVisible = MutableLiveData<Int>(View.VISIBLE)
    val progressbarVisible : LiveData<Int>
        get() = _progressbarVisible

    private val _progressbarText = MutableLiveData<String>("")
    val progressbarText : LiveData<String>
        get() = _progressbarText

    private val _radiogroupEnabled = MutableLiveData<Boolean>(false)
    val radiogroupEnabled : LiveData<Boolean>
        get() = _radiogroupEnabled

    private val _timeString = MutableLiveData<String>("00:00")
    val timeString : LiveData<String>
        get() = _timeString

    /**
     * All content of init() is moved to initForUnitTest() in order to make testing easier.
     */
    /*init {
        // we comment this line for unit-testing this viewmodel.
        initForUnitTest()
    }*/

    fun setParams(questionsOrTime: Boolean, gameLenght : Int) {
        if(questionsOrTime != this.questionsOrTime || gameLenght != this.limitValue) {
            this.questionsOrTime = questionsOrTime
            this.limitValue = gameLenght
            initForUnitTest()
        }
    }

    final fun initForUnitTest() {
        _showChooseQuizFragment.value = true // show fragment just started

        var lastRefreshMinutes = sharedPref.getLong(LAST_DB_REFRESH,0)
        if(!dateIsFresh(lastRefreshMinutes)) {
            // if we have to update pokemon, show such message in the progressbar's textview
            //_gameState.value = GameState.REFRESHING_POKEMON
            Timber.i("calling refresh pokemon")
            refreshPokemon()
        }
        else {
            val lastId = sharedPref.getInt(LAST_PAGING_POKEMON_ID_KEY,0)
            if(lastId < HomeViewModel.DOWNLOAD_SIZE) {
                refreshPokemon(lastId + 1, HomeViewModel.DOWNLOAD_SIZE - lastId)
            }
            else {
                initGame()
            }
        }
    }

    @VisibleForTesting
    fun refreshPokemon(offset : Int = 0, limit : Int = -1) {
        viewModelScope.launch {
            try {
                _imageVisible.postValue(View.INVISIBLE)
                _progressbarVisible.postValue(View.VISIBLE)
                _progressbarText.postValue(app.getString(R.string.refreshing_pokemon_msg))
                _radiogroupEnabled.postValue(false)
                Timber.i("calling refresh pokemon on the repository")
                repository.changeResponseState(PokemonResponseState.LOADING)
                repository.refreshPokemonPlay(offset,limit){
                    repository.changeResponseState(PokemonResponseState.DONE)
                    sharedPref.edit().putLong(LAST_DB_REFRESH, Date().time/60/1000).
                        putInt(LAST_PAGING_POKEMON_ID_KEY, HomeViewModel.DOWNLOAD_SIZE).apply()
                    initGame()
                }
                // TODO: MOVE ERROR VARIABLES TO REPOSITORY OR SOMEWHERE GENERALIZE THEM?
                //_eventNetworkError.value = false
                //_networkErrorShown.value = false
            }
            catch (e: IOException) {
                // Show a Toast error message and hide the progress bar.
                if(repository.pokemons.value!!.isEmpty()) {
                    Timber.e(e, "pokemon list is empty")
                    repository.changeResponseState(PokemonResponseState.NETWORK_ERROR)
                }
            }
        }
    }

    private val _animationLevel = MutableLiveData<Float>(0f)
    val animationLevel : LiveData<Float>
    get() = _animationLevel

    lateinit var timer: CountBaseTimer
    var currentAnimationTime = 0L

    fun initGame() {
        //this.questionsOrTime = questionsOrTime
        //this.limitValue = limitValue

        if(questionsOrTime) { // questions game
            startQuestionsGame()
        }
        else { // time game
           startTimeGame()
        }

    }

    fun startQuestionsGame() {
        Timber.i("questions game")

        timer = object : CountUpTimer(100) {
            override fun onTick(elapsedTime: Long) {
                _timeString.value = sdf.format(Date(elapsedTime))
            }
        }

        nextRound()

    }

    fun startTimeGame() {
        Timber.i("time game")
        // case of time
        timer = object : CountUpDownTimer(100, (limitValue*1000).toLong()) {
            override fun onFinish() {
                finishGame()
            }

            override fun onTick(elapsedTime: Long) {
                currentAnimationTime += interval
                _animationLevel.value = currentAnimationTime.toFloat()/animationTotalTime
            }

            override fun onDownTick(remainingTime: Long) {
                _timeString.value = sdf.format(Date(remainingTime))
            }
        }

        nextRound()
    }

    // game course:
    //================
    // one round
    //================
    // index where we hide right answer
    var rightAnswerIndex : Int = 0
    // id of the pokemon used as question
    var questionPokemonId = 0


    @VisibleForTesting fun nextRound() {
        //_radiogroupEnabled.value = false
        Timber.i("Launching nextRound coroutine")

        _radiogroupEnabled.postValue(false)
        _imageVisible.postValue(View.INVISIBLE)
        _progressbarVisible.postValue(View.VISIBLE)
        //Timber.i("progressbar changing text to ${app.getString(R.string.loading)}")
        _progressbarText.postValue(app.getString(R.string.loading))


        viewModelScope.launch {
            //var nextQuestionPokemon : Pokemon?
            // answerList : List<String>
            wrapEspressoIdlingResource {
                try {
                    repository.changeResponseState(PokemonResponseState.LOADING)
                    var nextQuestionPokemon =
                        repository.getNextRoundQuestionPokemon()?.asDomainModel()
                    //Timber.i("next pokemon is $nextQuestionPok")
                    //var nextQuestionPokemon = nextQuestionPok.asDomainModel()
                    Timber.i("next pokemon is $nextQuestionPokemon")
                    if (nextQuestionPokemon == null && networkIsOk(app)) {
                        Timber.i("all pokemon used, resetting...")
                        repository.resetUsedAsQuestionPlain()
                        nextQuestionPokemon =
                            repository.getNextRoundQuestionPokemon()!!.asDomainModel()
                    }
                    repository.updateUsedAsQuestion(nextQuestionPokemon!!.id, true)

                    // make function in Repository and inside it update pokemon in db (used as true).


                    questionPokemonId = nextQuestionPokemon.id
                    Timber.i("next id is $questionPokemonId")

                    // start gettin image from Glide...
                    _nextRoundQuestionPokemonId.postValue(questionPokemonId)

                    // get answer pokemon names from db and show them
                    //_gameState.value = GameState.GETTING_ANSWERS
                    val answerList = repository.getNextRoundAnswers(
                        questionPokemonId,
                        NUMBER_OF_ANSWERS - 1
                    )
                    repository.changeResponseState(PokemonResponseState.DONE)
                    withContext(dispatchers.main()) {
                        // add right answer in random place
                        rightAnswerIndex = Random().nextInt(NUMBER_OF_ANSWERS)
                        answerList.add(rightAnswerIndex, nextQuestionPokemon.name)
                        _nextRoundAnswers.value = answerList

                    }

                } catch (e: Exception) {
                    if (_nextRoundQuestionPokemonId.value == 0 || _nextRoundAnswers.value!!.isEmpty()) {
                        repository.changeResponseState(PokemonResponseState.DB_ERROR)
                    }
                }
            }
        }
    }

    fun onAnimationMaxed() {
        // start next round!
        //withContext(dispatchers.main()) {
        viewModelScope.launch {
            wrapEspressoIdlingResource {
                withContext(dispatchers.default()) {
                    delay(100)
                    withContext(dispatchers.main()) {
                        resetAnimation()
                        onAnswerChosen(-1)
                    }
                }
            }
        }
    }

    @VisibleForTesting fun resetAnimation() {
        _animationLevel.value = 0f
        currentAnimationTime = 0L
    }

    // loading image on glide failed
    fun onLoadImageFailed() {
        timer.stop()
        _progressbarVisible.value = View.INVISIBLE
        _showError.value = true
    }

    fun showErrorDone() {
        _showError.value = false
    }

    // loading image on glide succeeded
    fun onLoadImageSuccess() {
        _progressbarVisible.value = View.INVISIBLE
        _imageVisible.value = View.VISIBLE
        _radiogroupEnabled.value = true
        timer.start()
    }

    fun onAnswerChosen(index : Int) {
        Timber.i("on answer choser: $index")
        timer.pause()
        resetAnimation()
        if(index == rightAnswerIndex) {
            // right answer
            _rightAnswersCount.value = _rightAnswersCount.value!!+1
            _lastResult.value = true
            //_gameState.value = GameState.RIGHT_ANSWER
        }
        else {
            // wrong answer
            _wrongAnswersCount.value = _wrongAnswersCount.value!!+1
            _lastResult.value = false
            //_gameState.value = GameState.WRONG_ANSWER
        }
    }

    fun onResultShown() {
        // reset everything for next round
        roundNumber ++
        if(questionsOrTime) {
            if(roundNumber < limitValue) {
                nextRound()
            }
            else {
                finishGame()
            }
        }
        else {
            nextRound()
        }
    }

    fun finishGame() {
        timer.stop()
        _radiogroupEnabled.value = false
        val hitRate = _rightAnswersCount.value!!.toFloat() / roundNumber
        val speed =
            if(questionsOrTime) roundNumber.toFloat() / timer.elapsedTime
            else roundNumber.toFloat() / limitValue

        _showRecords.value = GameRecord(
            gameMode = questionsOrTime,
            gameLength = limitValue,
            questionsPerSecond = speed,
            hitRate = hitRate,
            recordTime = Date()
        )

        //roundNumber = 0
        // go to game records fragment
    }

    // for showing records fragment
    private val _showRecords = MutableLiveData<GameRecord?>()
    val showRecords : LiveData<GameRecord?>
    get() = _showRecords

    fun showRecordsDone() {
        _showRecords.value = null
    }
}

