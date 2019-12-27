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
import com.ignacio.pokemonquizkotlin2.databinding.FragmentGameRecordsBinding
import com.ignacio.pokemonquizkotlin2.db.GameRecord
import com.ignacio.pokemonquizkotlin2.di.Injectable
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel

class GameRecordsFragment : Fragment(), Injectable {

    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: GameRecordsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args = GameRecordsFragmentArgs.fromBundle(arguments!!)
        val binding = FragmentGameRecordsBinding.inflate(inflater)
        viewModel = provideViewModel()
        viewModel.setRecord(args.lastRecord)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        //val adapter = GameRecordsAdapter(getDatabase(context!!.applicationContext), args.lastRecord)
        //binding.recordRecyclerView.adapter = adapter

        viewModel.allRecords.observe(viewLifecycleOwner, Observer {
            //adapter.fixAndSubmitList(it)
        })

        return binding.root
    }

    // override this method in a subclass for testing.
    fun provideViewModel() : GameRecordsViewModel {
        return ViewModelProvider(this,viewModelFactory)
        .get(GameRecordsViewModel::class.java)
    }
}