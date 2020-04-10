package com.ignacio.pokemonquizkotlin2.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.databinding.FragmentHomeBinding
import com.ignacio.pokemonquizkotlin2.di.Injectable
import com.ignacio.pokemonquizkotlin2.testing.OpenForTesting
import com.ignacio.pokemonquizkotlin2.utils.writeLine
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

@OpenForTesting
class HomeFragment() : Fragment(), Injectable {
    // I did this for testing, so that I can use a mocked viewmodel naturally.
    // to take into account now: Its possible that we want to test Fragment with viewmodel altogether
    // since we test viewmodel alone in unittest, and the fragment is just interface!
    // in that regard, this would not be necessary. I would also like to adapt this to a more modern approach
    //by using the viewmodels<> thing. (Not so necessary but)

    @Inject lateinit var viewModelFactory : ViewModelProvider.Factory
    lateinit var homeViewModel: HomeViewModel
    private var currentId: Int = 0

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("viewmodelfactory created")
        viewModelFactory = HomeViewModelFactory(activity!!.application,
            PokemonRepository.getDefaultRepository(context!!))
        homeViewModel = ViewModelProvider(this,viewModelFactory).get(HomeViewModel::class.java)

    }*/

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
        homeViewModel = provideViewModel() //
        writeLine()
        Timber.i("viewmodel is $homeViewModel")
        binding.viewModel = homeViewModel

        args  = HomeFragmentArgs.fromBundle(arguments!!)


        savedInstanceState?.let {
            currentId = it.getInt("currentIdLiveData", args.newId)
        }?:let { currentId = args.newId}
        homeViewModel.initPush(currentId)

        writeLine()
        Timber.i("after initpush")
        if(args.newId > 0) {
            var x1: Float = 0f
            var x2: Float = 0f
            val MIN_DISTANCE = 150

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

                        if(x1-x2 > MIN_DISTANCE) {
                            currentId = if(currentId == HomeViewModel.DOWNLOAD_SIZE) {
                                1
                            }
                            else {
                                currentId + 1
                            }
                            homeViewModel.initPush(currentId)

                        }
                        else if(x2-x1 > MIN_DISTANCE) {

                            currentId = if(currentId == 1) {
                                HomeViewModel.DOWNLOAD_SIZE
                            }
                            else {
                                currentId - 1
                            }
                            homeViewModel.initPush(currentId)

                        }
                        Timber.i("currentid is $currentId")
                    }
                    else -> result = false
                }
                result
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentIdLiveData", currentId)
    }

    // override this method in a subclass for testing.
    fun provideViewModel() : HomeViewModel {
        return ViewModelProvider(this,viewModelFactory).get(HomeViewModel::class.java)
    }

}