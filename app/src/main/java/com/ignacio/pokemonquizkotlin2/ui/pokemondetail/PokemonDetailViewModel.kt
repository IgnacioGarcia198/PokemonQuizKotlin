package com.ignacio.pokemonquizkotlin2.ui.pokemondetail

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModel
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel
import com.ignacio.pokemonquizkotlin2.utils.sharedPreferences
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.util.*

open class PokemonDetailViewModel(app : Application) : BaseViewModel(app) {
    private var id : Int = 0
    open var spinnerPosition : Int = 0

    /**
     * The list of versions where current pokemon's flavor text is available
     */
    protected open val _versionList = MutableLiveData<List<String>>(mutableListOf())
    open val versionList : LiveData<List<String>>
        get() = _versionList

    /**
     * Version of the flavor text
     */
    protected open val _version = MutableLiveData<String>()
    open val version : LiveData<String>
        get() = _version
    //val version = Transformations.map(repository.versionList) {list -> list.first()}
    /**
     * Current pokemon's flavor text
     */
    protected open val _flavorText = MutableLiveData<String>("") //= Transformations.map(repository.flavorTextAndName) {pair -> pair.first}
    open val flavorText : LiveData<String> = _flavorText
    /**
     * Currrent pokemon's name
     */
    protected open val _name = MutableLiveData<String>("") //Transformations.map(repository.flavorTextAndName) {pair -> pair.second}
    open val name : LiveData<String> = _name
    /**
     * Response state
     */
    //val responseState = Transformations.switchMap(repository.responseState) {repository.responseState}

    /**
     * Daily pokemon id for today
     */
    private val _currentId = MutableLiveData<Int>(id)
    val currentId : LiveData<Int> = _currentId


    protected open var inited = false

    fun initPush(newId: Int) {
        if(_currentId.value != newId) {
            inited = false
            id = newId
            _currentId.value = newId
        }
        if(inited) return

        Timber.i("Init viewmodel")

        /**
         * Gets both flavor text for current language and version and also the pokemon's name, we do it together
         * in order to save in calls to the api.
         */
        viewModelScope.launch {
            try {
                repository.changeResponseState(PokemonResponseState.LOADING)
                repository.getFlavorTextAndNameFirstTime(id) {
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
     * To call when the list of available versions for current pokemon is ready.
     */
    protected open fun onVersionsReady(versions : List<String>) {
        Timber.i("calling versions ready")
        _versionList.value = versions
        _version.value = versions.first()
    }

    protected open fun setVersionsAndName(flavorAndName: Pair<String, String>) {
        _name.value = flavorAndName.second
        _flavorText.value = flavorAndName.first
    }

    /**
     * Event, version is chosen in spinner
     */
    open fun onVersionChangedOnSpinner(newVersion : String) {
        //_version.value = newVersion
        Timber.i("onversionchanged called")
        if(newVersion != _version.value) {
            viewModelScope.launch {
                try {
                    repository.changeResponseState(PokemonResponseState.LOADING)
                    repository.getFlavorTextNormally(id,version = newVersion) {
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
    protected open var errorShown = false
    open fun onLoadImageSuccess() {

    }

    open fun onLoadImageFailed() {
        if(errorShown) {
            Toast.makeText(
                app,
                app.getString(R.string.could_not_load_images),
                Toast.LENGTH_LONG
            ).show()
        }
    }

}
