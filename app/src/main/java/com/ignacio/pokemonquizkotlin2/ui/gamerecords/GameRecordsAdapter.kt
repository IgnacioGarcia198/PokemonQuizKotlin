/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ignacio.pokemonquizkotlin2.ui.gamerecords

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.db.GameRecord
import com.ignacio.pokemonquizkotlin2.db.MyDatabase
import com.ignacio.pokemonquizkotlin2.databinding.RecordsRowBinding
import com.ignacio.pokemonquizkotlin2.utils.DefaultDispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*

class GameRecordsAdapter(private val database: MyDatabase, private val lastRecord: GameRecord,
                         private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : ListAdapter<RecordItem, RecyclerView.ViewHolder>(RecordDiffCallback()) {
    private val HEADER_ITEM = 1
    private val RECORD_ITEM = 2


    private val adapterScope = CoroutineScope(dispatchers.default())

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is RecordViewHolder -> holder.bind(position, getItem(position) as RecordItem.GameRecordItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType : Int): RecyclerView.ViewHolder {
        return when(viewType) {
            HEADER_ITEM -> HeaderViewHolder.from(parent)
            RECORD_ITEM  -> RecordViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }


    fun fixAndSubmitList(list: List<GameRecord>) {
        val outputList : MutableList<RecordItem> = mutableListOf(RecordItem.Header)
        adapterScope.launch {
            if(list.isNotEmpty()) {
                val averagesRow = GameRecord(questionsPerSecond = database.gameRecordDao.averageSpeed,
                    hitRate = database.gameRecordDao.averageHitRate)
                outputList.add(RecordItem.GameRecordItem(lastRecord))
                outputList.add(RecordItem.GameRecordItem(averagesRow))
                outputList.addAll(list.map { RecordItem.GameRecordItem(it) })
            }
            withContext(dispatchers.main()) {
                submitList(outputList)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is RecordItem.Header -> HEADER_ITEM
            is RecordItem.GameRecordItem -> RECORD_ITEM
        }
    }

    class HeaderViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.records_header, parent, false)
                return HeaderViewHolder(view)
            }
        }
    }

    class RecordViewHolder private constructor(val binding: RecordsRowBinding) : RecyclerView.ViewHolder(binding.root){
        private val LAST_RECORD_POSITION = 1
        private val AVG_POSITION = 2

        fun bind(position: Int, recordItem: RecordItem.GameRecordItem) {
            val record = recordItem.record
            val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault())
            when(position) {
                AVG_POSITION -> {
                    binding.title.setText(R.string.averages_title)
                    binding.hitRate.text = record.hitRate.toString()
                    binding.speed.text = record.questionsPerSecond.toString()
                }

                else -> {
                    if(position == LAST_RECORD_POSITION) binding.title.setText(R.string.last_result)
                    binding.date.text = sdf.format(record.recordTime)
                    binding.gameMode.text = if(record.gameMode) "Questions" else "Time"
                    binding.gameLength.text = record.gameLength.toString()
                    binding.hitRate.text = record.hitRate.toString()
                    binding.speed.text = record.questionsPerSecond.toString()
                }
            }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): RecordViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecordsRowBinding.inflate(layoutInflater, parent, false)
                return RecordViewHolder(binding)
            }
        }
    }
}


class RecordDiffCallback : DiffUtil.ItemCallback<RecordItem>() {
    override fun areItemsTheSame(oldItem: RecordItem, newItem: RecordItem): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: RecordItem, newItem: RecordItem): Boolean {
        return oldItem == newItem
    }
}

class RecordListener(val clickListener:(recordId : Int)->Unit) {
    fun onClick(record : GameRecord) = clickListener(record.id)
}

/**
 * We make this wrapper class as we are going to need to show the last (current) record,
 * the averages record (an optionally a best record) and after that the rest of records
 * in date order.
 * Ways of doing this: We could insert current record, calculate averages (and bests if needed),
 * And after that get all the records from the list. We will insert in this list the averages after
 * The first one without any problem. So we can apply a suspend function and if we needed a flow as well.
 * We will add a new field for the domain model, "title", which will normally be 'Record'
 */
sealed class RecordItem {
    abstract val id: Int
    data class GameRecordItem(val record : GameRecord) : RecordItem() {
        override val id = record.id
    }
    object Header : RecordItem() {
        override val id = -1
    }
}
