package com.ignacio.pokemonquizkotlin2.ui.home

import android.app.Application
import android.content.SharedPreferences
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.ignacio.pokemonquizkotlin2.MyApplication
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import com.ignacio.pokemonquizkotlin2.testing.OpenForTesting
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModel
import com.ignacio.pokemonquizkotlin2.utils.sharedPreferences
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.util.*

@OpenForTesting
class HomeViewModel(
    app : Application,
    repository: PokemonRepositoryInterface = (app as MyApplication).repository,
    val sharedPref: SharedPreferences = sharedPreferences
) : BaseViewModel(app,repository) {

    companion object {
        @VisibleForTesting const val BASE_POK_DAY = "basePokDay"
        /**
         * CHANGE ONLY FOR TESTING. IF YOU CHANGE IT, IN ORDER TO SEE THE EFFECTS YOU NEED TO DELETE ALL
         * FROM THE DATABASE AND RESET THE lastRefresh VARIABLE. ORIGINAL VALUE IS 897 FOR THE LITTLE SPRITES
         * AND 720 FOR THE ARTWORK IMAGES.
         */
        const val DOWNLOAD_SIZE = 720//897

    }

    var currentId : Int = 0
    var spinnerPosition : Int = 0

    /**
     * The list of versions where current pokemon's flavor text is available
     */
    @VisibleForTesting val _versionList = MutableLiveData<List<String>>(mutableListOf())
    val versionList : LiveData<List<String>>
        get() = _versionList


    /**
     * Version of the flavor text
     */
    // Open for testing
    val _version = MutableLiveData<String>()
    val version : LiveData<String>
        get() = _version
    //val version = Transformations.map(repository.versionList) {list -> list.first()}
    /**
     * Current pokemon's flavor text
     */
    private val _flavorText = MutableLiveData<String>("") //= Transformations.map(repository.flavorTextAndName) {pair -> pair.first}
    val flavorText : LiveData<String> = _flavorText
    /**
     * Currrent pokemon's name
     */
    private val _name = MutableLiveData<String>("") //Transformations.map(repository.flavorTextAndName) {pair -> pair.second}
    val name : LiveData<String> = _name
    /**
     * Response state
     */
    //val responseState = Transformations.switchMap(repository.responseState) {repository.responseState}

    /**
     * Daily pokemon id for today
     */
    @VisibleForTesting val _currentIdLiveData = MutableLiveData<Int>(currentId)
    val currentIdLiveData : LiveData<Int> = _currentIdLiveData

    @VisibleForTesting
    var inited = false

    /**
     * Daily pokemon id for today
     */
    /*private var todayPokId = 0
    private val _todayPokIdLiveData = MutableLiveData<Int>()
    val todayPokIdLiveData : LiveData<Int>
        get() = _todayPokIdLiveData*/


    private val _dailyOrDetail = MutableLiveData(false) // false for daily pokemon, true for detail
    val dailyOrDetail : LiveData<Boolean> = _dailyOrDetail

    fun initPush(newId: Int) {
        calculateNewId(newId)
        if(!inited) {
            getFlavorAndNameInitially()
        }
    }

    @VisibleForTesting
    fun calculateNewId(newId : Int) {
        val nextId : Int
        if(newId == 0) {
            nextId = calculateTodayPokId()
            _dailyOrDetail.value = false
        }
        else {
            nextId = newId
            _dailyOrDetail.value = true
        }

        if(nextId != currentId) {
            inited = false
            currentId = nextId
            _currentIdLiveData.value = nextId
        }
    }

    /**
     * Gets both flavor text for current language and version and also the pokemon's name, we do it together
     * in order to save in calls to the api.
     */
    @VisibleForTesting
    fun getFlavorAndNameInitially() {

        viewModelScope.launch {
            try {
                repository.changeResponseState(PokemonResponseState.LOADING)
                repository.getFlavorTextAndNameFirstTime(currentId,"en","red") {
                        versions, flavorAndName ->
                    onFlavorTextAndNameResult(versions, flavorAndName)
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
    fun onVersionChangedOnSpinner(newVersion : String) {
        //_version.value = newVersion
        if(newVersion != _version.value) {
            viewModelScope.launch {
                try {
                    repository.changeResponseState(PokemonResponseState.LOADING)
                    repository.getFlavorTextNormally(pokid = currentId,language = "en",version = newVersion) {
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
    @VisibleForTesting fun calculateTodayPokId() : Int {
        // calculate today as gettimeinmillis converted to days
        val c = Calendar.getInstance(TimeZone.getDefault())
        c.set(Calendar.HOUR_OF_DAY,0)
        c.set(Calendar.MINUTE,0)
        c.set(Calendar.SECOND,0)
        c.set(Calendar.MILLISECOND,0)
        val today = c.timeInMillis


        // get last pokemon day as
        var basePokDay = sharedPref.getLong(BASE_POK_DAY,0L)
        // TODO use basePokDay to make a preference for new year pokemon, in a next version.
        // TODO ADD PREFERENCE TO SELECT POKEMON TEST TIMING FOR QUESTIONS
        if(basePokDay == 0L) { // FIRST TIME
            c.set(Calendar.YEAR, 2019)
            c.set(Calendar.MONTH, 0)
            c.set(Calendar.DAY_OF_MONTH, 1)
            basePokDay = c.timeInMillis
            sharedPref.edit().putLong(BASE_POK_DAY,basePokDay).apply()
        }


        val daysDiff = (today - basePokDay)/1000/3600/24+1
        return (daysDiff% DOWNLOAD_SIZE).toInt()

    }

    //================================================


    @VisibleForTesting
    fun onFlavorTextAndNameResult(versions : List<String>, flavorAndName: Pair<String, String>) {
        onVersionsReady(versions)
        setFlavorAndName(flavorAndName)
        inited = true
    }
    /**
     * To call when the list of available versions for current pokemon is ready.
     */
    @VisibleForTesting
    fun onVersionsReady(versions : List<String>) {
        Timber.i("calling versions ready")
        _versionList.value = versions
        _version.value = versions.first()
    }

    @VisibleForTesting
    fun setFlavorAndName(flavorAndName: Pair<String, String>) {
        _name.value = flavorAndName.second
        _flavorText.value = flavorAndName.first
    }

    @VisibleForTesting var errorShown = false
    fun onLoadImageSuccess() {

    }


    fun onLoadImageFailed() {
        _showError.value = true
    }

    fun showErrorDone() {
        _showError.value = false
    }

}