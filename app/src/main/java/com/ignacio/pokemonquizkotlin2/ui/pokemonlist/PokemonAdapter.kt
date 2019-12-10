package com.ignacio.pokemonquizkotlin2.ui.pokemonlist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.db.DatabasePokemon
import com.ignacio.pokemonquizkotlin2.data.db.asDomainModel
import com.ignacio.pokemonquizkotlin2.data.model.Pokemon
import com.ignacio.pokemonquizkotlin2.databinding.PokemonRowBinding
import com.ignacio.pokemonquizkotlin2.ui.play.loadThePokemonImage

internal class PokemonAdapter(val listener: PokemonClickListener) : PagedListAdapter<DatabasePokemon,
        PokemonAdapter.PokemonViewHolder>(PokemonDiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PokemonViewHolder {
        return PokemonViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    internal class PokemonViewHolder(val binding : PokemonRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pokemon : DatabasePokemon?, listener: PokemonClickListener) {
            pokemon?.let {
                binding.pokemon = pokemon.asDomainModel()
                binding.listener = listener
                binding.executePendingBindings()
                loadThePokemonImage(binding.coverImage,pokemon.id, successCallback = {
                    // success:

                },
                    failCallback = { // error:
                        if(!errorShown) {
                            Toast.makeText(
                                binding.root.context,
                                binding.root.context.getString(R.string.could_not_load_images),
                                Toast.LENGTH_LONG
                            ).show()
                            errorShown = true
                        }
                })
            }
        }

        companion object {
            fun from(parent: ViewGroup) : PokemonViewHolder {
                return PokemonViewHolder(PokemonRowBinding.inflate(LayoutInflater.from(parent.context), parent,false))
            }
            var errorShown : Boolean = false
        }
    }
}

object PokemonDiffCallback : DiffUtil.ItemCallback<DatabasePokemon>() {
    override fun areItemsTheSame(oldItem: DatabasePokemon, newItem: DatabasePokemon): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DatabasePokemon, newItem: DatabasePokemon): Boolean {
        return  oldItem == newItem
    }
}

class PokemonClickListener(val clickListener: (id : Int) -> Unit) {
    fun onClick(pokemon: Pokemon) {clickListener(pokemon.id)}
}