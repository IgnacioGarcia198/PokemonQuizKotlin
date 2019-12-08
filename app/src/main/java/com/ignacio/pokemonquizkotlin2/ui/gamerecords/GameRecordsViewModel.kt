package com.ignacio.pokemonquizkotlin2.ui.gamerecords

import android.app.Application
import androidx.lifecycle.*
import com.ignacio.pokemonquizkotlin2.data.db.GameRecord
import com.ignacio.pokemonquizkotlin2.data.db.getDatabase
import kotlinx.coroutines.*

class GameRecordsViewModel(val app : Application, lastRecord: GameRecord) : AndroidViewModel(app) {
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(app)

    val allRecords = Transformations.switchMap(database.gameRecordDao.allGameRecordsLiveData) { database.gameRecordDao.allGameRecordsLiveData}

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.gameRecordDao.save(lastRecord)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}