package com.ignacio.pokemonquizkotlin2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import com.ignacio.pokemonquizkotlin2.testing.OpenForTesting
import com.ignacio.pokemonquizkotlin2.utils.DefaultDispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@OpenForTesting
abstract class BaseViewModel(
    val app : Application,
    val repository : PokemonRepositoryInterface,
    val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : AndroidViewModel(app) {

    fun getResponseState() : LiveData<PokemonResponseState> {
        return repository.responseState
    }

    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    protected val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     *
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    protected val viewModelScope = CoroutineScope(viewModelJob + dispatchers.main())

    protected val _showError = MutableLiveData<Boolean>(false)
    val showError : LiveData<Boolean> = _showError

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}