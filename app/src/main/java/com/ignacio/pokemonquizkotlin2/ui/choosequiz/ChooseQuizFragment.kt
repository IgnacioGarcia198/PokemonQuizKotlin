package com.ignacio.pokemonquizkotlin2.ui.choosequiz

import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.databinding.FragmentChooseQuizBinding
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModelFactory
import com.ignacio.pokemonquizkotlin2.ui.getViewModelFactory
import com.ignacio.pokemonquizkotlin2.utils.sharedPreferences


class ChooseQuizFragment : Fragment() {

    val viewModel: ChooseQuizViewModel by viewModels {getViewModelFactory()}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentChooseQuizBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.navigateToPlayFragment.observe(viewLifecycleOwner, Observer {
            if(it) {
                val questionsOrTime = viewModel.questionsOrTime.value?:true

                val gameLength = viewModel.gameLength

                // here actual navigation
                findNavController().navigate(ChooseQuizFragmentDirections.actionNavChooseQuizToNavPlay(questionsOrTime, gameLength.value!!))

                viewModel.doneNavigating()
            }
        })

        //=====================================================================

        val byQuestionsNumberAdapter = ArrayAdapter<String>(
            context!!, android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.questions_number_options)
        )
        byQuestionsNumberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.numberQuestionsSpinner.adapter = byQuestionsNumberAdapter

//=======================================================================

        val byTimeAdapter = ArrayAdapter<String>(
            context!!, android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.time_options)
        )
        byTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.byTimeSpinner.adapter = byTimeAdapter
        //byTimeSpinner.setEnabled(false)

//===================================================================

        return binding.root
    }

    fun getViewModelFactory() : ViewModelProvider.AndroidViewModelFactory {
        return ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
    }

}
