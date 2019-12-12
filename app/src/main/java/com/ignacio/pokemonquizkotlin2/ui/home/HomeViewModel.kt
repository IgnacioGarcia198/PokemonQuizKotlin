package com.ignacio.pokemonquizkotlin2.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import com.ignacio.pokemonquizkotlin2.ui.pokemondetail.PokemonDetailViewModel
import com.ignacio.pokemonquizkotlin2.utils.sharedPreferences
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.util.*

class HomeViewModel(app : Application) : PokemonDetailViewModel(app) {

    companion object {
        private const val BASE_POK_DAY = "basePokDay"
        /**
         * CHANGE ONLY FOR TESTING. IF YOU CHANGE IT, IN ORDER TO SEE THE EFFECTS YOU NEED TO DELETE ALL
         * FROM THE DATABASE AND RESET THE lastRefresh VARIABLE. ORIGINAL VALUE IS 897 FOR THE LITTLE SPRITES
         * AND 720 FOR THE ARTWORK IMAGES.
         */
        const val DOWNLOAD_SIZE = 720//897
    }


    /**
     * Daily pokemon id for today
     */
    private var todayPokId = 0
    private val _todayPokIdLiveData = MutableLiveData<Int>()
    val todayPokIdLiveData : LiveData<Int>
        get() = _todayPokIdLiveData

    override var inited = false

    fun initPushHome() {
        if(inited) return

        Timber.i("Init viewmodel")

        calculateTodayPokId()
        /**
         * Gets both flavor text for current language and version and also the pokemon's name, we do it together
         * in order to save in calls to the api.
         */
        viewModelScope.launch {
            try {
                repository.changeResponseState(PokemonResponseState.LOADING)
                repository.getFlavorTextAndNameFirstTime(pokid= todayPokId) {
                        versions, flavorAndName ->
                    onVersionsReady(versions)
                    setVersionsAndName(flavorAndName)
                    inited = true
                }
                repository.changeResponseState(PokemonResponseState.DONE)
            }
            catch (e: IOException) {
                // Show a Toast error message and hide the progress bar.
                if(flavorText.value == null || name.value == null ||
                    flavorText.value!!.isEmpty() || name.value!!.isEmpty())
                    repository.changeResponseState(PokemonResponseState.NETWORK_ERROR)
            }
        }
    }



    /**
     * Event, version is chosen in spinner
     */
    override fun onVersionChangedOnSpinner(newVersion : String) {
        //_version.value = newVersion
        if(newVersion != _version.value) {
            viewModelScope.launch {
                try {
                    repository.changeResponseState(PokemonResponseState.LOADING)
                    repository.getFlavorTextNormally(pokid = todayPokId,version = newVersion) {
                        _version.value = newVersion
                        _flavorText.value = it
                    }
                    repository.changeResponseState(PokemonResponseState.DONE)
                }
                catch (e: IOException) {
                    // Show a Toast error message and hide the progress bar.
                    if(flavorText.value == null || flavorText.value!!.isEmpty())
                        repository.changeResponseState(PokemonResponseState.NETWORK_ERROR)
                }
            }
        }
    }

    /**
     * Calculate which is today's pokemon id
     */
    fun calculateTodayPokId() {
        // calculate today as gettimeinmillis converted to days
        val c = Calendar.getInstance(TimeZone.getDefault())
        c.set(Calendar.HOUR_OF_DAY,0)
        c.set(Calendar.MINUTE,0)
        c.set(Calendar.SECOND,0)
        c.set(Calendar.MILLISECOND,0)
        val today = c.timeInMillis


        // get last pokemon day as
        var basePokDay = sharedPreferences.getLong(BASE_POK_DAY,0L)
        // TODO use basePokDay to make a preference for new year pokemon, in a next version.
        // TODO ADD PREFERENCE TO SELECT POKEMON TEST TIMING FOR QUESTIONS
        if(basePokDay == 0L) { // FIRST TIME
            c.set(Calendar.YEAR, 2019)
            c.set(Calendar.MONTH, 0)
            c.set(Calendar.DAY_OF_MONTH, 1)
            basePokDay = c.timeInMillis
            sharedPreferences.edit().putLong(BASE_POK_DAY,basePokDay).apply()
        }

        val daysDiff = (today - basePokDay)/1000/3600/24+1
        todayPokId = (daysDiff% DOWNLOAD_SIZE).toInt()
        _todayPokIdLiveData.value = todayPokId
        //return todayId

        //_todayPokIdLiveData.value = (daysDiff% DOWNLOAD_SIZE).toInt()
        //return (daysDiff% DOWNLOAD_SIZE).toInt()
    }

    override var errorShown = false

}