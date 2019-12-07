package com.ignacio.pokemonquizkotlin2.ui.choosequizdialog

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.view.doOnLayout
import androidx.databinding.BindingAdapter
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel
import com.ignacio.pokemonquizkotlin2.ui.play.PlayViewModel
import timber.log.Timber
// TODO: EVERYTHING IS COPIED FROM OTHER PLACE, CHANGE FUNCTIONS.


@BindingAdapter("onItemSelected1")
fun setOnItemSelected(spinner: Spinner, viewModel: ChooseQuizViewModel) {
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.onValueChangedOnSpinner(position)
            //viewModel?.spinnerPosition = position
            //viewModel?.onVersionChangedOnSpinner(spinner.getItemAtPosition(position) as String)
        }

    }
}

/*@BindingAdapter("initialPosition1")
fun setInitialPosition(spinner: Spinner, position : Int = 0) {
    spinner.doOnLayout { spinner.setSelection(position) }
    spinner.onFocusChangeListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {

        }
    }
}*/

