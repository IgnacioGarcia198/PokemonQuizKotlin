package com.ignacio.pokemonquizkotlin2.ui.gamerecords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ignacio.pokemonquizkotlin2.databinding.FragmentGameRecordsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameRecordsFragment : Fragment() {

    private val viewModel: GameRecordsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val args = GameRecordsFragmentArgs.fromBundle(arguments!!)
        //adapter.setLastRecord(args.lastRecord)
        val binding = FragmentGameRecordsBinding.inflate(inflater)
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
}
