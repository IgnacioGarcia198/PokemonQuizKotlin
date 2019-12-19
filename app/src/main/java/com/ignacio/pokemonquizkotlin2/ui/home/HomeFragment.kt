package com.ignacio.pokemonquizkotlin2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.databinding.FragmentHomeBinding
import com.ignacio.pokemonquizkotlin2.db.getDatabase
import com.ignacio.pokemonquizkotlin2.testing.OpenForTesting
import com.ignacio.pokemonquizkotlin2.ui.BaseFragment
import timber.log.Timber

@OpenForTesting
class HomeFragment() : Fragment() {
    lateinit var viewModelFactory : ViewModelProvider.Factory
    @VisibleForTesting lateinit var homeViewModel: HomeViewModel

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
        if(arguments != null) {
            args  = HomeFragmentArgs.fromBundle(arguments!!)
            homeViewModel.initPush(args.newId)
        }
        else {
            Timber.d("doing on  push for daily pokemon with 0")
            homeViewModel.initPush(0)
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
                arguments = HomeFragmentArgs.Builder(id).build().toBundle()

        }
    }



}