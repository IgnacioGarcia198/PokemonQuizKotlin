package com.ignacio.pokemonquizkotlin2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,
            container,false)
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = homeViewModel
        /*if(spinnerPosition != 0) {
            binding.spinner.setSelection(spinnerPosition)
        }*/

        return binding.root
    }

}