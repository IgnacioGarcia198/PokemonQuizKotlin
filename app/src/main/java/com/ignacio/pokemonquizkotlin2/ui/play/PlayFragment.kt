package com.ignacio.pokemonquizkotlin2.ui.play

import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ignacio.pokemonquizkotlin2.OpenClass
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.databinding.FragmentPlayBinding
import com.ignacio.pokemonquizkotlin2.testing.OpenForTesting
import com.ignacio.pokemonquizkotlin2.ui.BaseFragment
import com.ignacio.pokemonquizkotlin2.ui.PlayViewModelFactory
import com.ignacio.pokemonquizkotlin2.ui.getViewModelFactory
import com.ignacio.pokemonquizkotlin2.utils.sharedPreferences
import kotlinx.android.synthetic.main.fragment_play.*
import kotlinx.android.synthetic.main.right_toast3.view.*
import timber.log.Timber

@OpenForTesting
class PlayFragment : Fragment() {

    companion object {
        const val toastDurationInMilliSeconds = 500L
    }
    @VisibleForTesting lateinit var playViewModel: PlayViewModel
    private lateinit var gameToast : Toast
    private lateinit var toastCountDown : CountDownTimer



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPlayBinding.inflate(inflater,container,false)
        val args = PlayFragmentArgs.fromBundle(arguments!!)
        //playViewModel.initGame(args.questionsOrTime,args.gameLength)
        playViewModel = ViewModelProvider(this,
            getViewModelFactory(questionsOrTime = args.questionsOrTime, limitValue = args.gameLength))
            .get(PlayViewModel::class.java)

            //ViewModelProviders.of(this).get(PlayViewModel::class.java)
        binding.viewModel = playViewModel
        binding.lifecycleOwner = this


        /*playViewModel.showChooseQuizFragment.observe(this, Observer {
            if(it) {
                //showChooseQuizDialog()
                playViewModel.chooseQuizShown()
            }
        })*/

        playViewModel.radiogroupEnabled.observe(viewLifecycleOwner, Observer {
            Timber.i("radiogroup is enabled : ${customRadioGroup.isEnabled}")
        })

        initToastTimer()

        playViewModel.lastResult.observe(viewLifecycleOwner, Observer {
            Timber.i("lastResult is ${playViewModel.lastResult.value}")
            it?.let {
                showResult(it)
            }
        })

        playViewModel.showError.observe(viewLifecycleOwner, Observer {
            Toast.makeText(
                context, context!!.getString(R.string.could_not_load_images),
                Toast.LENGTH_LONG
            ).show()
            playViewModel.showErrorDone()
        })

        playViewModel.showRecords.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(PlayFragmentDirections.actionNavPlayToNavGameRecords(it))
                playViewModel.showRecordsDone()
            }
        })

        /*playViewModel.getResponseState().observe(this, Observer {
            addOrRemoveErrorMsg(binding.root as ViewGroup,it)
        })*/


        return binding.root
    }

    private fun showResult(rightAnswer : Boolean) {
        gameToast = Toast(context)
        gameToast.setGravity(Gravity.CENTER, 0, 100)
        gameToast.duration = Toast.LENGTH_SHORT

        val layout = layoutInflater.inflate(R.layout.right_toast3, activity!!.findViewById(R.id.rightToastLayout))
        //ImageView image = layout.findViewById(R.id.image)
        if (rightAnswer) {
            layout.image.setImageResource(R.drawable.greenmaruthin)
        } else {
            layout.image.setImageResource(R.drawable.redbatsuthin)
        }
        gameToast.view = layout
        gameToast.show()
        toastCountDown.start()

    }

    private fun initToastTimer() {

        toastCountDown =
            object : CountDownTimer(toastDurationInMilliSeconds, 100 /*Tick duration*/) {
                override fun onFinish() {
                    gameToast.cancel()
                    playViewModel.onResultShown()
                }

                override fun onTick(millisUntilFinished: Long) {}

            }
    }

    fun getViewModelFactory(
        repository: PokemonRepositoryInterface = PokemonRepository.getDefaultRepository(requireActivity().application),
        sharedPref: SharedPreferences = sharedPreferences,
        questionsOrTime : Boolean,
        limitValue : Int
    ) : PlayViewModelFactory {
        return PlayViewModelFactory(requireActivity().application,repository, sharedPref, questionsOrTime, limitValue)
    }

}