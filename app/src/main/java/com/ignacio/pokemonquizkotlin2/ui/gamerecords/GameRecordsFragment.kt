package com.ignacio.pokemonquizkotlin2.ui.gamerecords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ignacio.pokemonquizkotlin2.databinding.FragmentGameRecordsBinding
import com.ignacio.pokemonquizkotlin2.di.Injectable
import javax.inject.Inject

class GameRecordsFragment : Fragment(), Injectable {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    //@Inject lateinit var adapter: GameRecordsAdapter
    private lateinit var viewModel: GameRecordsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args = GameRecordsFragmentArgs.fromBundle(arguments!!)
        //adapter.setLastRecord(args.lastRecord)
        val binding = FragmentGameRecordsBinding.inflate(inflater)
        viewModel = provideViewModel()
        viewModel.setRecord(args.lastRecord)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val adapter = GameRecordsAdapter()
        binding.recordRecyclerView.adapter = adapter

        viewModel.fixedList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        return binding.root
    }

    // override this method in a subclass for testing.
    fun provideViewModel() : GameRecordsViewModel {
        return ViewModelProvider(this,viewModelFactory)
        .get(GameRecordsViewModel::class.java)
    }
}