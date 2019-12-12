package com.ignacio.pokemonquizkotlin2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import com.ignacio.pokemonquizkotlin2.db.getDatabase
import com.ignacio.pokemonquizkotlin2.utils.DefaultDispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class BaseViewModel(val app : Application, private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()) : AndroidViewModel(app) {
    protected val repository = PokemonRepository(getDatabase(app))
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}