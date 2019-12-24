package com.ignacio.pokemonquizkotlin2.ui.gamerecords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.data.ServiceLocator
import com.ignacio.pokemonquizkotlin2.databinding.FragmentGameRecordsBinding
import com.ignacio.pokemonquizkotlin2.db.GameRecord
import com.ignacio.pokemonquizkotlin2.db.getDatabase
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModelFactory
import com.ignacio.pokemonquizkotlin2.ui.GameRecordsViewModelFactory
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel

class GameRecordsFragment : Fragment() {

    private lateinit var viewModel: GameRecordsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args = GameRecordsFragmentArgs.fromBundle(arguments!!)
        val binding = FragmentGameRecordsBinding.inflate(inflater)
        viewModel = provideViewModel(args.lastRecord)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val adapter = GameRecordsAdapter(getDatabase(context!!.applicationContext), args.lastRecord)
        binding.recordRecyclerView.adapter = adapter

        viewModel.allRecords.observe(viewLifecycleOwner, Observer {
            adapter.fixAndSubmitList(it)
        })

        return binding.root
    }

    // override this method in a subclass for testing.
    fun provideViewModel(lastRecord : GameRecord) : GameRecordsViewModel {
        return ViewModelProvider(this, GameRecordsViewModelFactory(
            requireActivity().application, lastRecord = lastRecord
        )
        ).get(GameRecordsViewModel::class.java)
    }
}