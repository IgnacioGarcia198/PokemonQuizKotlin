package com.ignacio.pokemonquizkotlin2.ui.choosequizdialog

import android.app.Application
import android.media.RemoteControlClient
import androidx.lifecycle.*
import com.ignacio.pokemonquizkotlin2.R

class ChooseQuizViewModel(val app:Application) : AndroidViewModel(app) {

    private val _questionsOrTime = MutableLiveData<Boolean>(true)
    val questionsOrTime : LiveData<Boolean> = _questionsOrTime

    fun chooseQuestionsOrTime(b:Boolean) {
        _questionsOrTime.value = b
        _gameLength.value =
        if(b) {
             app.resources.getIntArray(
                R.array.questions_number_values)[_questionSpinnerPosition.value!!]
        }
        else {
            app.resources.getIntArray(
                R.array.time_values)[_timeSpinnerPosition.value!!]
        }
    }

    private val _gameLength= MutableLiveData<Int>(app.resources.getIntArray(
        R.array.questions_number_values)[0])
    val gameLength : LiveData<Int> = _gameLength


    private val _questionSpinnerPosition = MutableLiveData<Int>(0)
    val questionSpinnerPosition : LiveData<Int> = _questionSpinnerPosition

    private val _timeSpinnerPosition = MutableLiveData<Int>(0)
    val timeSpinnerPosition : LiveData<Int> = _timeSpinnerPosition


    fun onValueChangedOnSpinner(newPosition : Int) {
        if(_questionsOrTime.value!! && newPosition != _questionSpinnerPosition.value) {
            _gameLength.value = app.resources.getIntArray(
                R.array.questions_number_values)[newPosition]
            _questionSpinnerPosition.value = newPosition
        }
        else if(!_questionsOrTime.value!! && newPosition != _timeSpinnerPosition.value) {
            _gameLength.value = app.resources.getIntArray(
                R.array.time_values)[newPosition]
            _timeSpinnerPosition.value = newPosition
        }
    }



    private val _navigateToPlayFragment = MutableLiveData<Boolean>(false)
    val navigateToPlayFragment : LiveData<Boolean> = _navigateToPlayFragment

    fun onButtonClicked() {
        _navigateToPlayFragment.value = true
        // navigate to play fragment
    }

    fun doneNavigating() {
        _navigateToPlayFragment.value = false
    }


// TODO: DECLARE LIVEDATA TO MATCH THE LAYOUT
}