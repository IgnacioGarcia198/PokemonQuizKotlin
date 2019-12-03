package com.ignacio.pokemonquizkotlin2.ui.pokemonlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ignacio.pokemonquizkotlin2.R

class PokemonListFragment : Fragment() {

    private lateinit var pokemonListViewModel: PokemonListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pokemonListViewModel =
            ViewModelProviders.of(this).get(PokemonListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_pokemonlist, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        pokemonListViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}