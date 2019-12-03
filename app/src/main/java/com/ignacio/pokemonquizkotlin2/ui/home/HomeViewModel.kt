package com.ignacio.pokemonquizkotlin2.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.db.getDatabase
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.util.*

const val PREFERENCE_FILE_NAME = "customPrefs.pref"

class HomeViewModel(app : Application) : BaseViewModel(app) {

    companion object {
        private const val BASE_POK_DAY = "basePokDay"
        /**
         * CHANGE ONLY FOR TESTING. IF YOU CHANGE IT, IN ORDER TO SEE THE EFFECTS YOU NEED TO DELETE ALL
         * FROM THE DATABASE AND RESET THE lastRefresh VARIABLE. ORIGINAL VALUE IS 897 FOR THE LITTLE SPRITES
         * AND 720 FOR THE ARTWORK IMAGES.
         */
        const val DOWNLOAD_SIZE = 720//897
    }

    var spinnerPosition : Int = 0

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

    /**
     * The list of versions where current pokemon's flavor text is available
     */
    private val _versionList = MutableLiveData<List<String>>()
    val versionList : LiveData<List<String>>
    get() = _versionList

    /**
     * Version of the flavor text
     */
    private val _version = MutableLiveData<String>()
    val version : LiveData<String>
        get() = _version
    //val version = Transformations.map(repository.versionList) {list -> list.first()}
    /**
     * Current pokemon's flavor text
     */
    val flavorText = Transformations.map(repository.flavorTextAndName) {pair -> pair.first}
    /**
     * Currrent pokemon's name
     */
    val name = Transformations.map(repository.flavorTextAndName) {pair -> pair.second}

    /**
     * Error on loading data from network
     */
    private val _eventNetworkError = MutableLiveData<Boolean>(false)
    val eventNetworkError : LiveData<Boolean>
    get() = _eventNetworkError

    /**
     * Flag in order to stop emitting eventNetworkError
     */
    private val _networkErrorShown = MutableLiveData<Boolean>(false)
    val networkErrorShown : LiveData<Boolean>
    get() = _networkErrorShown

    /**
     * Daily pokemon id for today
     */
    private var todayPokId = 0
    private val _todayPokIdLiveData = MutableLiveData<Int>()
    val todayPokIdLiveData : LiveData<Int>
        get() = _todayPokIdLiveData

    init {
        calculateTodayPokId()
        Timber.i("Init viewmodel")
        //Log.i("init","viewmodel init")
        //repository = PokemonRepository(getDatabase(app))
        calculateTodayPokId()
        //todayPokId.value = todayPokId
        getVersionListFromNetwork()
        //getFlavorTexts(todayPokId,"en", "alpha-sapphire")
        //_flavorText.value =
    }

    //private val flavorsAndName = Transformations.map(version) {version -> getFlavorTexts(_todayPokIdLiveData.value!!,"en", version)}
    //val flavorText = Transformations.map(flavorsAndName) {flavors -> flavors.first}
    //val name = Transformations.map(flavorsAndName) {flavors -> flavors.second}

    /*
        TODO IMPROVEMENT IN NEXT VERSION: TRY TO GET ALL VERSIONS FROM SHAREDPREF, IF NO POSSIBLE
        THEN GET FROM NETWORK. TET ALL FLAVOR TEXTS FOR CURRENT POKEMON AND UPDATE THE REAL VERSIONS LIST
        ON MEMORY. STORE FLAVOR TEXTS LIST IN LOCAL LIST.
        SO WE WILL NEED AT MOST TWO CALLS TO RETROFIT.
     */

    /**
     * Gets initially the pokemon game version list
     */
    fun getVersionListFromNetwork() {
        Timber.i("getVersionlist")
        viewModelScope.launch {
            try {
                //versionList = repository.getVersionListFromNetwork()
                repository.getVersionList{
                        //versions ->  onVersionsReady(versions)
                    // for example : put this versionList in sharedPreferences
                    Timber.i("from versions, calling getFlavorText")
                    getFlavorTextsAndName(todayPokId,"en"){
                            onVersionsReady(it)
                        Timber.i("versions are ready")
                    }
                }
                _eventNetworkError.value = false
                _networkErrorShown.value = false

                //Timber.i("versionlist in viewmodel is ${versionList.value}")
                //_version.value = versionList.value?.first()

            }
            catch (e: IOException) {
                // Show a Toast error message and hide the progress bar.
                if(versionList.value!!.isEmpty())
                    _eventNetworkError.postValue(true)
            }
        }
    }

    /**
     * To call when the list of available versions for current pokemon is ready.
     */
    fun onVersionsReady(versions : List<String>) {
        Timber.i("calling versions ready")
        _versionList.value = versions
        _version.value = versions.first()
    }

    /**
     * Gets both flavor text for current language and version and also the pokemon's name, we do it together
     * in order to save in calls to the api.
     */
    fun getFlavorTextsAndName(pokid : Int, language : String = "en", version : String = "red",
                              callback :  (versions: List<String>) -> Unit = {}) {
        Timber.i("getFlavorTexts")
        viewModelScope.launch {
            try {
                repository.getSpecieFlavorText(pokid,language, version,callback)
                _eventNetworkError.value = false
                _networkErrorShown.value = false
            }
            catch (e: IOException) {
                // Show a Toast error message and hide the progress bar.
                if(flavorText.value!!.isEmpty() || name.value!!.isEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

    /**
     * Event, version is chosen in spinner
     */
    fun onVersionChangedOnSpinner(newVersion : String) {
        //_version.value = newVersion
        if(newVersion != _version.value) {
            getFlavorTextsAndName(todayPokId,"en", newVersion)
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

        val sharedPreferences = app.getSharedPreferences(
            PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        // get last pokemon day as
        var basePokDay = sharedPreferences.getLong(BASE_POK_DAY,0L)
        // TODO use basePokDay to make a preference for new year pokemon, in a next version.
        // TODO ADD PREFERENCE TO SELECT POKEMON TEST TIMING FOR QUESTIONS
        if(basePokDay == 0L) {
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


    /**
     * Clean the job if viewmodel is finish
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}