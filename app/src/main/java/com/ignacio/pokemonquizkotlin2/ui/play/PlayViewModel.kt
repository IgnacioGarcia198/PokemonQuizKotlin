package com.ignacio.pokemonquizkotlin2.ui.play

import android.app.Application
import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ignacio.pokemonquizgamekotlin.utils.CountBaseTimer
import com.ignacio.pokemonquizgamekotlin.utils.CountUpDownTimer
import com.ignacio.pokemonquizgamekotlin.utils.CountUpTimer
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import com.ignacio.pokemonquizkotlin2.data.db.asDomainModel
import com.ignacio.pokemonquizkotlin2.data.db.getDatabase
import com.ignacio.pokemonquizkotlin2.data.model.Pokemon
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModel
import com.ignacio.pokemonquizkotlin2.ui.home.PREFERENCE_FILE_NAME
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val FRESH_TIMEOUT_IN_MINUTES = 43200 // ONE WEEK
const val LAST_DB_REFRESH = "lastDbRefresh"
const val NUMBER_OF_ANSWERS = 4

enum class GameState {
    REFRESHING_POKEMON, GETTING_QUESTION, GETTING_ANSWERS, WAITING_CHOICE, RIGHT_ANSWER, WRONG_ANSWER
}

class PlayViewModel(
    app : Application,
    private val questionsOrTime : Boolean,
    private val limitValue : Int) : BaseViewModel(app) {

    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     *
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    private val repository = PokemonRepository(getDatabase(app))
    private val sharedPref = app.getSharedPreferences(PREFERENCE_FILE_NAME,
        Context.MODE_PRIVATE)

    //private val questionsOrTime = false
    private var roundNumber = 0
    private var currentTime = 0L
    private var animationTotalTime = 4000L

    private val _gameState = MutableLiveData<GameState>()
    val gameState : LiveData<GameState>
    get() = _gameState

    private val _nextRoundQuestionPokemonId = MutableLiveData<Int>(0)
    val nextRoundQuestionPokemonId : LiveData<Int>
        get() = _nextRoundQuestionPokemonId

    private val _nextRoundAnswers = MutableLiveData<List<String>>(listOf())
    val nextRoundAnswers : LiveData<List<String>>
        get() = _nextRoundAnswers

    private val _rightAnswersCount = MutableLiveData<Int>(0)
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

    init {
        var lastRefreshMinutes = sharedPref.getLong(LAST_DB_REFRESH,0)
        if(!dateIsFresh(lastRefreshMinutes)) {
            // if we have to update pokemon, show such message in the progressbar's textview
            _gameState.value = GameState.REFRESHING_POKEMON
            Timber.i("calling refresh pokemon")
            refreshPokemon()
        }
        else {
            // for testing
            Timber.i("calling initgame")
            initGame()
        }

    }

    private fun refreshPokemon() {
        viewModelScope.launch {
            try {
                //_gameState.value = GameState.REFRESHING_POKEMON
                // change UI
                _imageVisible.postValue(View.INVISIBLE)
                _progressbarVisible.postValue(View.VISIBLE)
                _progressbarText.postValue(app.getString(R.string.refreshing_pokemon_msg))
                _radiogroupEnabled.postValue(false)
                Timber.i("calling refresh pokemon on the repository")
                repository.refreshPokemon{
                    sharedPref.edit().putLong(LAST_DB_REFRESH, Date().time/60/1000).apply()
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
                }
                //_eventNetworkError.value = true
            }
        }
    }

    /*fun hideProgressBar() {
        _progressbarVisible.value = View.INVISIBLE
    }
    fun showImage() {
        _imageVisible.value = View.VISIBLE
    }*/

    private val _animationLevel = MutableLiveData<Float>(0f)
    val animationLevel : LiveData<Float>
    get() = _animationLevel

    lateinit var timer: CountBaseTimer
    var currentAnimationTime = 0L

    private fun initGame() {
        val sdf = SimpleDateFormat("mm:ss",Locale.getDefault())
        if(questionsOrTime) { // questions game
            Timber.i("questions game")


            timer = object : CountUpTimer(100) {
                override fun onTick(elapsedTime: Long) {
                    _timeString.value = sdf.format(Date(elapsedTime))
                }
            }


            // init sth if needed
            if(roundNumber == 0) {
                // start of the game(count from 0)
                Timber.i("calling nextround")
                nextRound()
            }
            else {
                // resuming game
                Timber.i("calling nextround")
                nextRound()
            }
        }
        else { // time game
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

            // init sth if needed
            /*if(currentTime == 0L) {
                // start of the game(count from 0)

                Timber.i("calling nextround")
                nextRound()
            }
            else {
                // resuming game
                Timber.i("calling nextround")
                nextRound()
            }*/
            nextRound()
        }


    }

    // game course:
    //================
    // one round
    //================
    // index where we hide right answer
    var rightAnswerIndex : Int = 0
    // id of the pokemon used as question
    var questionPokemonId = 0
    private fun nextRound() {
        //_radiogroupEnabled.value = false
        Timber.i("Launching nextRound coroutine")
        // get non-used pokemon from db (pokemon for the image)
        // show loading message
        //_gameState.postValue(GameState.GETTING_QUESTION)
        // change UI
        _radiogroupEnabled.postValue(false)
        _imageVisible.postValue(View.INVISIBLE)
        _progressbarVisible.postValue(View.VISIBLE)
        Timber.i("progressbar changing text to ${app.getString(R.string.loading)}")
        _progressbarText.postValue(app.getString(R.string.loading))


        viewModelScope.launch {
            try {

                Timber.i("trying to get next pokemon")
                val nextQuestionPokemon =
                    repository.getNextRoundQuestionPokemon().await().asDomainModel()
                questionPokemonId = nextQuestionPokemon.id
                Timber.i("next id is $questionPokemonId")

                // start gettin image from Glide...
                _nextRoundQuestionPokemonId.postValue(questionPokemonId)

                // get answer pokemon names from db and show them
                //_gameState.value = GameState.GETTING_ANSWERS
                val answerList = repository.getNextRoundAnswers(
                    questionPokemonId,
                    NUMBER_OF_ANSWERS - 1
                ).await()
                withContext(Dispatchers.Main) {
                    // add right answer in random place
                    rightAnswerIndex = Random().nextInt(NUMBER_OF_ANSWERS)
                    answerList.add(rightAnswerIndex, nextQuestionPokemon.name)
                    _nextRoundAnswers.value = answerList

                    // hide loading message

                    // resume timer
                    // wait for user's click
                    //_gameState.value = GameState.WAITING_CHOICE
                }

            }
            catch (e : Exception) {
                if(_nextRoundQuestionPokemonId.value == 0 || _nextRoundAnswers.value!!.isEmpty()) {
                    _responseState.value = PokemonResponseState.DB_ERROR
                }
            }
        }
    }

    fun onAnimationMaxed() {
        // start next round!
        resetAnimation()
        onAnswerChosen(-1)
    }
    private fun resetAnimation() {
        _animationLevel.value = 0f
        currentAnimationTime = 0L
    }

    // loading image on glide failed
    fun onLoadImageFailed() {
        timer.stop()
        _progressbarVisible.value = View.INVISIBLE
        //_radiogroupEnabled.value = false
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
        _showRecords.value = true
        //roundNumber = 0
        // go to game records fragment
    }

    // for showing records fragment
    private val _showRecords = MutableLiveData<Boolean>(false)
    val showRecords : LiveData<Boolean>
    get() = _showRecords

    fun showRecordsDone() {
        _showRecords.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}

fun dateIsFresh(minutes : Long) : Boolean {
    val nowMillis = Calendar.getInstance().timeInMillis
    return nowMillis/60/1000 - minutes <= FRESH_TIMEOUT_IN_MINUTES
}