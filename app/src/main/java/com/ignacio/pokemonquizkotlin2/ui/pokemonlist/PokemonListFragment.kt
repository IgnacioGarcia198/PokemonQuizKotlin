package com.ignacio.pokemonquizkotlin2.ui.pokemonlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.databinding.FragmentPokemonlistBinding
import kotlinx.android.synthetic.main.fragment_pokemonlist.*

class PokemonListFragment : Fragment() {

    private lateinit var pokemonListViewModel: PokemonListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pokemonListViewModel =
            ViewModelProviders.of(this).get(PokemonListViewModel::class.java)
        val binding = FragmentPokemonlistBinding.inflate(inflater)
        //val root = inflater.inflate(R.layout.fragment_pokemonlist, container, false)
        binding.viewModel = pokemonListViewModel
        binding.lifecycleOwner = this


        val adapter = PokemonAdapter(PokemonClickListener {
            findNavController().navigate(PokemonListFragmentDirections.actionNavPokemonListToNavDailyPokemon(it))
        })
        //val recyclerView = root.findViewById<RecyclerView>(R.id.poklistRecyclerView)
        binding.poklistRecyclerView.adapter = adapter
        pokemonListViewModel.pokemonList.observe(this, Observer {
            adapter.submitList(it)
        })

        binding.editText.doOnTextChanged { text, _,_,_->
            pokemonListViewModel.changeText(text.toString())
        }

        return binding.root
    }


}