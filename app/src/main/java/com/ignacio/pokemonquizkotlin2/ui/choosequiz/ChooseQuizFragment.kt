package com.ignacio.pokemonquizkotlin2.ui.choosequiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.databinding.FragmentChooseQuizBinding
import com.ignacio.pokemonquizkotlin2.di.Injectable
import javax.inject.Inject


class ChooseQuizFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: ChooseQuizViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentChooseQuizBinding.inflate(inflater)
        viewModel = provideViewModel()
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
            requireContext(), android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.questions_number_options)
        )
        byQuestionsNumberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.numberQuestionsSpinner.adapter = byQuestionsNumberAdapter

//=======================================================================

        val byTimeAdapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.time_options)
        )
        byTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.byTimeSpinner.adapter = byTimeAdapter
        //byTimeSpinner.setEnabled(false)

//===================================================================

        return binding.root
    }

    private fun provideViewModel() : ChooseQuizViewModel {
        return ViewModelProvider(this, viewModelFactory).get(ChooseQuizViewModel::class.java)
    }

}
