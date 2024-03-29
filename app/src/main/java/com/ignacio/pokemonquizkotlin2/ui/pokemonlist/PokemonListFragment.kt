package com.ignacio.pokemonquizkotlin2.ui.pokemonlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ignacio.pokemonquizkotlin2.databinding.FragmentPokemonlistBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PokemonListFragment : Fragment() {

    private val pokemonListViewModel: PokemonListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPokemonlistBinding.inflate(inflater)
        //val root = inflater.inflate(R.layout.fragment_pokemonlist, container, false)
        binding.viewModel = pokemonListViewModel
        binding.lifecycleOwner = this


        val adapter = PokemonAdapter(PokemonClickListener {
            findNavController().navigate(
                PokemonListFragmentDirections.actionNavPokemonListToNavDailyPokemon().setNewId(it)
            )
        })
        //val recyclerView = root.findViewById<RecyclerView>(R.id.poklistRecyclerView)
        binding.poklistRecyclerView.adapter = adapter
        pokemonListViewModel.pokemonList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        binding.editText.doOnTextChanged { text, _, _, _ ->
            pokemonListViewModel.changeText(text.toString())
        }

        return binding.root
    }
}
