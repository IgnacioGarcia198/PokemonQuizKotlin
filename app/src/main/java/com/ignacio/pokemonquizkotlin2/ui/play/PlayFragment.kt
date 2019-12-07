package com.ignacio.pokemonquizkotlin2.ui.play

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.databinding.FragmentPlayBinding
import com.ignacio.pokemonquizkotlin2.ui.choosequizdialog.ChooseQuizFragment
import kotlinx.android.synthetic.main.custom_progress_bar.*
import kotlinx.android.synthetic.main.fragment_play.*
import kotlinx.android.synthetic.main.right_toast3.*
import kotlinx.android.synthetic.main.right_toast3.view.*
import timber.log.Timber

class PlayFragment : Fragment(), ChooseQuizFragment.OnFragmentInteractionListener {
    companion object {
        const val toastDurationInMilliSeconds = 500L
    }
    private lateinit var playViewModel: PlayViewModel
    private lateinit var gameToast : Toast
    private lateinit var toastCountDown : CountDownTimer
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPlayBinding.inflate(inflater,container,false)
        playViewModel =
                ViewModelProviders.of(this).get(PlayViewModel::class.java)
            //ViewModelProviders.of(this).get(PlayViewModel::class.java)
        binding.viewModel = playViewModel
        binding.lifecycleOwner = this

        playViewModel.showChooseQuizFragment.observe(this, Observer {
            if(it) {
                showChooseQuizDialog()
                playViewModel.chooseQuizShown()
            }
        })

        playViewModel.radiogroupEnabled.observe(this, Observer {
            Timber.i("radiogroup is enabled : ${customRadioGroup.isEnabled}")
        })

        initToastTimer()

        playViewModel.lastResult.observe(this, Observer {
            Timber.i("lastResult is ${playViewModel.lastResult.value}")
            it?.let {
                showResult(it)
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

    internal fun showChooseQuizDialog() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.let {
            val chooseQuizFragment = activity?.supportFragmentManager?.findFragmentByTag("dialog")
            if (chooseQuizFragment != null) {
                ft.remove(chooseQuizFragment)
            }
            ft.addToBackStack(null)

            // Create and show the dialog.
            val newFragment =
                ChooseQuizFragment.newInstance(this)

            newFragment.show(ft, "dialog")
        }


        //newFragment.getDialog().getWindow()

    }

    override fun onFragmentInteraction(questionsOrTime: Boolean, value: Int) {
        playViewModel.initGame(questionsOrTime, value)
    }
}