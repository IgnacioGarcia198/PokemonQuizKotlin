package com.ignacio.pokemonquizkotlin2.ui.play

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.databinding.FragmentPlayBinding
import com.ignacio.pokemonquizkotlin2.di.Injectable
import com.ignacio.pokemonquizkotlin2.testing.OpenForTesting
import kotlinx.android.synthetic.main.fragment_play.*
import kotlinx.android.synthetic.main.right_toast3.view.*
import timber.log.Timber
import javax.inject.Inject

@OpenForTesting
class PlayFragment : Fragment(), Injectable {

    companion object {
        const val toastDurationInMilliSeconds = 500L
    }
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var playViewModel: PlayViewModel
    private lateinit var gameToast : Toast
    private lateinit var toastCountDown : CountDownTimer



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPlayBinding.inflate(inflater,container,false)
        val args = PlayFragmentArgs.fromBundle(arguments!!)
        playViewModel = provideViewModel()
        playViewModel.setParams(args.questionsOrTime,args.gameLength)
        binding.viewModel = playViewModel
        binding.lifecycleOwner = this


        playViewModel.radiogroupEnabled.observe(viewLifecycleOwner, Observer {
            Timber.i("radiogroup is enabled : ${customRadioGroup.isEnabled}")
        })

        initToastTimer()

        playViewModel.lastResult.observe(viewLifecycleOwner, Observer {
            if(!playViewModel.lastResultShown) {
                Timber.i("lastResult is ${playViewModel.lastResult.value}")
                it?.let {
                    showResult(it)
                }
            }
        })

        playViewModel.showError.observe(viewLifecycleOwner, Observer {
            if(it) {
                Toast.makeText(
                    context, context!!.getString(R.string.could_not_load_images),
                    Toast.LENGTH_LONG
                ).show()
                playViewModel.showErrorDone()
            }
        })

        playViewModel.showRecords.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(PlayFragmentDirections.actionNavPlayToNavGameRecords(it))
                playViewModel.showRecordsDone()
            }
        })

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
        playViewModel.lastResultShown = true
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


    // override this method in a subclass for testing.
    fun provideViewModel() : PlayViewModel {
        return ViewModelProvider(this,viewModelFactory).get(PlayViewModel::class.java)
    }

}