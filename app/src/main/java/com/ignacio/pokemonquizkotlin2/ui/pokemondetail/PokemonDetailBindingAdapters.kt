package com.ignacio.pokemonquizkotlin2.ui.pokemondetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import timber.log.Timber

@BindingAdapter("onItemSelected")
fun setOnItemSelected(spinner: Spinner, viewModel : PokemonDetailViewModel?) {
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel?.spinnerPosition = position
            viewModel?.onVersionChangedOnSpinner(spinner.getItemAtPosition(position) as String)
        }

    }
}






