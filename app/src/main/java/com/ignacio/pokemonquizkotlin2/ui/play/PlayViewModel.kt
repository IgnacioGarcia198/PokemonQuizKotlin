package com.ignacio.pokemonquizkotlin2.ui.play

import android.app.Application
import android.content.SharedPreferences
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import com.ignacio.pokemonquizkotlin2.db.GameRecord
import com.ignacio.pokemonquizkotlin2.db.asDomainModel
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModel
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel
import com.ignacio.pokemonquizkotlin2.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    var lastResultShown = false

    val sdf : SimpleDateFormat = SimpleDateFormat("mm:ss",Locale.getDefault())

    @VisibleForTesting var roundNumber = 0
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

    fun setParams(questionsOrTime: Boolean, gameLenght : Int) {
        if(questionsOrTime != this.questionsOrTime || gameLenght != this.limitValue) {
            this.questionsOrTime = questionsOrTime
            this.limitValue = gameLenght
            initForUnitTest()
        }
    }

    /**
     * We do this instead of normal init() because of using Dagger, passing values with setParams()
     * is much easier for dependency injection when we talk about ViewModels.
     */
    @VisibleForTesting
    fun initForUnitTest() {

        var lastRefreshMinutes = sharedPref.getLong(LAST_DB_REFRESH,0)
        if(!dateIsFresh(lastRefreshMinutes)) {
            // if we have to update pokemon, show such message in the progressbar's textview
            Timber.i("calling refresh pokemon")
            refreshPokemon()
        }
        else {
            val lastId = sharedPref.getInt(LAST_PAGING_POKEMON_ID_KEY,0)
            if(lastId < HomeViewModel.DOWNLOAD_SIZE) {
                refreshPokemon(lastId + 1)
            }
            else {
                initGame()
            }
        }
    }

    @VisibleForTesting
    fun refreshPokemon(offset : Int = 0, limit : Int = HomeViewModel.DOWNLOAD_SIZE - offset) {
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

    // index where we hide right answer
    var rightAnswerIndex : Int = 0
    // id of the pokemon used as question
    var questionPokemonId = 0


    @VisibleForTesting fun nextRound() {
        _radiogroupEnabled.postValue(false)
        _imageVisible.postValue(View.INVISIBLE)
        _progressbarVisible.postValue(View.VISIBLE)
        _progressbarText.postValue(app.getString(R.string.loading))


        viewModelScope.launch {
            wrapEspressoIdlingResource {
                try {
                    repository.changeResponseState(PokemonResponseState.LOADING)
                    var nextQuestionPokemon =
                        repository.getNextRoundQuestionPokemon()?.asDomainModel()
                    Timber.i("next pokemon is $nextQuestionPokemon")
                    if (nextQuestionPokemon == null && networkIsOk(app)) {
                        Timber.i("all pokemon used, resetting...")
                        repository.resetUsedAsQuestionPlain()
                        nextQuestionPokemon =
                            repository.getNextRoundQuestionPokemon()!!.asDomainModel()
                    }
                    repository.updateUsedAsQuestion(nextQuestionPokemon!!.id, true)

                    questionPokemonId = nextQuestionPokemon.id
                    Timber.i("next id is $questionPokemonId")

                    // start getting image from Glide...
                    _nextRoundQuestionPokemonId.postValue(questionPokemonId)

                    // get answer pokemon names from db and show them
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
        lastResultShown = false
        if(index == rightAnswerIndex) {
            // right answer
            _rightAnswersCount.value = _rightAnswersCount.value!!+1
            _lastResult.value = true
        }
        else {
            // wrong answer
            _wrongAnswersCount.value = _wrongAnswersCount.value!!+1
            _lastResult.value = false
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

        // go to game records fragment
        _showRecords.value = GameRecord(
            gameMode = questionsOrTime,
            gameLength = limitValue,
            questionsPerSecond = speed.roundTo(3),
            hitRate = hitRate.roundTo(3),
            recordTime = Date()
        )
    }

    // for showing records fragment
    private val _showRecords = MutableLiveData<GameRecord?>()
    val showRecords : LiveData<GameRecord?>
    get() = _showRecords

    fun showRecordsDone() {
        _showRecords.value = null
    }
}

