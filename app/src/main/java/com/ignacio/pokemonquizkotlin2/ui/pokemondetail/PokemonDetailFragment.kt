package com.ignacio.pokemonquizkotlin2.ui.pokemondetail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.DataBindingUtil
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.databinding.PokemonDetailFragmentBinding
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import kotlin.math.abs


class PokemonDetailFragment : Fragment() {

    private lateinit var viewModel: PokemonDetailViewModel
    private lateinit var mDetector: GestureDetectorCompat
    private var currentId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding : PokemonDetailFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.pokemon_detail_fragment,
            container,false)
        val args = PokemonDetailFragmentArgs.fromBundle(arguments!!)
        viewModel = ViewModelProviders.of(
            this).get(PokemonDetailViewModel::class.java)
        binding.lifecycleOwner = this

        var x1: Float = 0f
        var x2: Float = 0f
        val MIN_DISTANCE = 150
        currentId = args.id
        val initialX = binding.mainImageView.x


        binding.root.setOnTouchListener { v, event ->
            val action: Int = event!!.action
            var result = true
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    Timber.i("Action was DOWN")
                    x1 = event.x
                }

                MotionEvent.ACTION_UP -> {
                    Timber.i("Action was UP")
                    x2 = event.x

                    if(x1-x2 > MIN_DISTANCE && currentId < HomeViewModel.DOWNLOAD_SIZE) {
                        viewModel.initPush(++currentId)

                    }
                    else if(x2-x1 > MIN_DISTANCE && currentId > 1) {
                        viewModel.initPush(--currentId)

                    }
                }
                else -> result = false
            }
            result
        }

        savedInstanceState?.let {
            currentId = it.getInt("currentIdLiveData",args.id)
        }
        binding.viewModel = viewModel
        viewModel.initPush(currentId)

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentIdLiveData", currentId)
    }
}
