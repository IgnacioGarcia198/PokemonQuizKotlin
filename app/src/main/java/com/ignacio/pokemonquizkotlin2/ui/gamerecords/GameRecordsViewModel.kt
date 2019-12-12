package com.ignacio.pokemonquizkotlin2.ui.gamerecords

import android.app.Application
import androidx.lifecycle.*
import com.ignacio.pokemonquizkotlin2.db.GameRecord
import com.ignacio.pokemonquizkotlin2.db.getDatabase
import com.ignacio.pokemonquizkotlin2.utils.DefaultDispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.DispatcherProvider
import kotlinx.coroutines.*

class GameRecordsViewModel(val app : Application,
                           lastRecord: GameRecord,
                           private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()) : AndroidViewModel(app) {
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + dispatchers.main())

    private val database = getDatabase(app)

    val allRecords = Transformations.switchMap(database.gameRecordDao.allGameRecordsLiveData) { database.gameRecordDao.allGameRecordsLiveData}

    init {
        viewModelScope.launch {
            withContext(dispatchers.io()) {
                database.gameRecordDao.save(lastRecord)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}