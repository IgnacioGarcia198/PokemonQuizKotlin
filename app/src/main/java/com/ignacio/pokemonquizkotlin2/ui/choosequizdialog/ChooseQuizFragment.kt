package com.ignacio.pokemonquizkotlin2.ui.choosequizdialog

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.databinding.FragmentChooseQuizBinding
import com.ignacio.pokemonquizkotlin2.ui.play.PlayViewModel
import kotlinx.android.synthetic.main.fragment_choose_quiz.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ChooseQuizFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ChooseQuizFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseQuizFragment(var listener: OnFragmentInteractionListener) : DialogFragment() { // TODO: IM GONNA USE A PROPER FRAGMENT WITH ITS NAVIGATION AND ALL THE STUFF. AND SO I CAN INSTANTIATE THE
    // GAME FRAGMENT PROPERLY FROM THE BEGINNING WITH ITS ARGUMENTS. ONE OF THE REASONS IS THAT IT IS MUCH BETTER SEPARATING THE VIEWMODELS.
    // SO I COULD USE INTERFACES OR SETTERS IN PLAYVIEWMODEL FOR THE TIME AND QUESTIONS. BUT WE CANNOT USE SAFEARGS IN THAT WAY SO WE CAN TRY IN THIS BRANCH, LATER ON
    // IN A NEW BRANCH WE WILL HAVE TO MAKE THIS INTO A PROPER FRAGMENT.
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    //private var listener: OnFragmentInteractionListener? = null
    private var questionsOrTime = true
    private lateinit var viewModel: ChooseQuizViewModel
    var limitValue : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentChooseQuizBinding.inflate(inflater)
        viewModel =
            ViewModelProviders.of(this).get(ChooseQuizViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //val v : View = inflater.inflate(R.layout.fragment_choose_quiz, container, false)
        val toolbar = binding.toolbar
        toolbar.title = "Choose Quiz"
        toolbar.setNavigationIcon(R.drawable.ic_close)
        toolbar.setNavigationOnClickListener{ dismiss() }

        viewModel.navigateToPlayFragment.observe(this, Observer {
            if(it) {
                val questionsOrTime = viewModel.questionsOrTime.value?:true

                val gameLength = viewModel.gameLength

                //listener?.onFragmentInteraction(questionsOrTime, gameLength)

                viewModel.doneNavigating()
                dismiss()
                listener.onFragmentInteraction(questionsOrTime,gameLength.value!!)
                //viewModel.initGame()
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

    // TODO: Rename method, update argument and hook method into UI event
    /*fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }*/

    /*override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        //listener = null
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(questionsOrTime : Boolean, value : Int)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChooseQuizFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(listener : OnFragmentInteractionListener) =
            ChooseQuizFragment(listener)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }

    }
}
