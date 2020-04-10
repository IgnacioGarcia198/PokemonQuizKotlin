package com.ignacio.pokemonquizkotlin2.ui.pokemonlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ignacio.pokemonquizkotlin2.databinding.FragmentPokemonlistBinding
import com.ignacio.pokemonquizkotlin2.di.Injectable
import javax.inject.Inject

class PokemonListFragment : Fragment(),Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var pokemonListViewModel: PokemonListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pokemonListViewModel = provideViewModel()
            //ViewModelProviders(this).get(PokemonListViewModel::class.java)
        val binding = FragmentPokemonlistBinding.inflate(inflater)
        //val root = inflater.inflate(R.layout.fragment_pokemonlist, container, false)
        binding.viewModel = pokemonListViewModel
        binding.lifecycleOwner = this


        val adapter = PokemonAdapter(PokemonClickListener {
            findNavController().navigate(PokemonListFragmentDirections.actionNavPokemonListToNavDailyPokemon().setNewId(it))
        })
        //val recyclerView = root.findViewById<RecyclerView>(R.id.poklistRecyclerView)
        binding.poklistRecyclerView.adapter = adapter
        pokemonListViewModel.pokemonList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        binding.editText.doOnTextChanged { text, _,_,_->
            pokemonListViewModel.changeText(text.toString())
        }

        return binding.root
    }

    // override this method in a subclass for testing.
    fun provideViewModel() : PokemonListViewModel {
        return ViewModelProvider(this,viewModelFactory).get(PokemonListViewModel::class.java)
    }
}