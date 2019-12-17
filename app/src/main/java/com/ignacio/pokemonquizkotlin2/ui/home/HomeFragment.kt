package com.ignacio.pokemonquizkotlin2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var args :HomeFragmentArgs


        val binding : FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,
            container,false)
        homeViewModel = ViewModelProvider(this,HomeViewModelFactory(activity!!.application,
            PokemonRepository(getDatabase(activity!!.applicationContext))
        )).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = homeViewModel
        if(arguments != null) {
            args  = HomeFragmentArgs.fromBundle(arguments!!)
            homeViewModel.initPush(args.newId)
        }
        else {
            homeViewModel.initPush(0)
        }

        homeViewModel.showError.observe(viewLifecycleOwner, Observer {
            Toast.makeText(
                context,
                context!!.getString(R.string.could_not_load_images),
                Toast.LENGTH_LONG
            ).show()
            homeViewModel.showErrorDone()
        })

        return binding.root
    }

}