package com.ignacio.pokemonquizkotlin2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.databinding.FragmentHomeBinding
import com.ignacio.pokemonquizkotlin2.testing.OpenForTesting
import timber.log.Timber

@OpenForTesting
class HomeFragment() : Fragment() {
    lateinit var viewModelFactory : ViewModelProvider.Factory
    @VisibleForTesting lateinit var homeViewModel: HomeViewModel
    private var currentId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("viewmodelfactory created")
        viewModelFactory = HomeViewModelFactory(activity!!.application,
            PokemonRepository.getDefaultRepository(context!!))
        homeViewModel = ViewModelProvider(this,viewModelFactory).get(HomeViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var args :HomeFragmentArgs


        val binding : FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,
            container,false)
        //homeViewModel =
        binding.lifecycleOwner = this
        binding.viewModel = homeViewModel

        args  = HomeFragmentArgs.fromBundle(arguments!!)

        homeViewModel.initPush(args.newId)

        if(args.newId > 0) {
            var x1: Float = 0f
            var x2: Float = 0f
            val MIN_DISTANCE = 150
            currentId = args.newId
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
                            homeViewModel.initPush(++currentId)

                        }
                        else if(x2-x1 > MIN_DISTANCE && currentId > 1) {
                            homeViewModel.initPush(--currentId)

                        }
                    }
                    else -> result = false
                }
                result
            }

            savedInstanceState?.let {
                currentId = it.getInt("currentIdLiveData",args.newId)
            }
        }


        homeViewModel.showError.observe(viewLifecycleOwner, Observer {
            if(it) {
                Toast.makeText(
                    context,
                    context!!.getString(R.string.could_not_load_images),
                    Toast.LENGTH_LONG
                ).show()
                homeViewModel.showErrorDone()
            }
        })

        return binding.root
    }

    companion object {
        fun newInstance(id : Int) =
            HomeFragment().apply {
                arguments = HomeFragmentArgs.Builder().setNewId(id).build().toBundle()

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentIdLiveData", currentId)
    }

}