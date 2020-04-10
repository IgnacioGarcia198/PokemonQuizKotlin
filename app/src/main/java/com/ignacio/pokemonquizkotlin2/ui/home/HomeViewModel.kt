package com.ignacio.pokemonquizkotlin2.ui.home

import android.app.Application
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import com.ignacio.pokemonquizkotlin2.testing.OpenForTesting
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModel
import com.ignacio.pokemonquizkotlin2.utils.writeLine
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.util.*
import javax.inject.Inject

@OpenForTesting
class HomeViewModel @Inject constructor(
    app : Application,
    repository: PokemonRepositoryInterface,
    val sharedPref: SharedPreferences
) : BaseViewModel(app,repository) {

    companion object {
        @VisibleForTesting const val BASE_POK_DAY = "basePokDay"
        /**
         * CHANGE ONLY FOR TESTING. IF YOU CHANGE IT, IN ORDER TO SEE THE EFFECTS YOU NEED TO DELETE ALL
         * FROM THE DATABASE AND RESET THE lastRefresh VARIABLE. ORIGINAL VALUE IS 897 FOR THE LITTLE SPRITES
         * AND 720 FOR THE ARTWORK IMAGES.
         */
        const val DOWNLOAD_SIZE = 721//897

    }

    var currentId : Int = 0
    var spinnerPosition : Int = 0

    /**
     * The list of versions where current pokemon's flavor text is available
     */
    private val _versionList = MutableLiveData<List<String>>(listOf(""))
    val versionList : LiveData<List<String>> = _versionList//Transformations.map(versionsMap) {it.keys.toList()}


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
    val flavorText : LiveData<String> = _flavorText//Transformations.map(_version) {_versionsMap.value!![it] ?: ""}
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
            writeLine()
            Timber.i("getting flavor and name initially")
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

    private val _versionsMap = MutableLiveData<Map<String,String>>()
    val versionsMap : LiveData<Map<String,String>>
    get() = _versionsMap

    /**
     * Gets both flavor text for current language and version and also the pokemon's name, we do it together
     * in order to save in calls to the api.
     */
    @VisibleForTesting
    fun getFlavorAndNameInitially() {
        viewModelScope.launch {
            try {
                writeLine()
                Timber.i("coroutine lauched")
                repository.changeResponseState(PokemonResponseState.LOADING)
                writeLine()
                Timber.i("before calculating")
                /**
                 * Destructuring declaration
                 */
                val (themap, thename) = repository.getFlavorTextAndNameFirstTimeReturns(currentId,"en")
                writeLine()
                Timber.i("after calculating versionsmap and name are $themap, $thename")
                if(themap.isNullOrEmpty() || thename.isNullOrEmpty()) {
                    repository.changeResponseState(PokemonResponseState.NETWORK_ERROR)
                }
                else {
                    //val themap = versionsMapAndName.first
                    _name.postValue(thename)
                    _versionsMap.postValue(themap)
                    _versionList.postValue(themap.keys.toList())
                    _flavorText.postValue(themap[themap.keys.first()])
                    _version.postValue(themap.keys.first())

                    /*_name.value = versionsMapAndName.second
                    _versionsMap.value = themap
                    _versionList.value = themap.keys.toList()
                    _flavorText.value = themap[themap.keys.first()]
                    _version.value = themap.keys.first()*/
                    repository.changeResponseState(PokemonResponseState.DONE)
                }
                writeLine()
                Timber.i("getflavorandnameinitially was executed, result is $themap, $thename")
            }
            catch (e: IOException) {
                // Show a Toast error message and hide the progress bar.
                if(versionList.value.isNullOrEmpty() || name.value.isNullOrEmpty())
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
                _version.value  = newVersion
                _versionsMap.value?.let {
                    _flavorText.value = _versionsMap.value!![newVersion]
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