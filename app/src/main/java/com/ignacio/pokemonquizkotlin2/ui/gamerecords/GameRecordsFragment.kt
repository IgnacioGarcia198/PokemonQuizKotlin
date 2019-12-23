package com.ignacio.pokemonquizkotlin2.ui.gamerecords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.ServiceLocator
import com.ignacio.pokemonquizkotlin2.databinding.FragmentGameRecordsBinding
import com.ignacio.pokemonquizkotlin2.db.getDatabase
import com.ignacio.pokemonquizkotlin2.ui.getViewModelFactory

class GameRecordsFragment : Fragment() {

    private lateinit var viewModel: GameRecordsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args = GameRecordsFragmentArgs.fromBundle(arguments!!)
        val binding = FragmentGameRecordsBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, getViewModelFactory(lastRecord = args.lastRecord))
            .get(GameRecordsViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val adapter = GameRecordsAdapter(getDatabase(context!!.applicationContext), args.lastRecord)
        binding.recordRecyclerView.adapter = adapter

        viewModel.allRecords.observe(viewLifecycleOwner, Observer {
            adapter.fixAndSubmitList(it)
        })

        return binding.root
    }
}