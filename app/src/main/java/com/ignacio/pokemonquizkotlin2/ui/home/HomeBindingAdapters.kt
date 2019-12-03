package com.ignacio.pokemonquizkotlin2.ui.home

import android.view.View
import android.widget.*
import androidx.core.view.doOnLayout
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import timber.log.Timber

/**
 * Binding adapter used to display images from URL using Glide
 */
@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    url?.let {
        Glide.with(imageView.context).load(url).into(imageView)
    }
}

@BindingAdapter("versions")
fun setVersions(spinner: Spinner, versionList:List<String>?) {
    Timber.i("Version in bindingAdapter : $versionList")
    versionList?.let {
        //Timber.i("Version in bindingAdapter : $it")
        val adapter = ArrayAdapter<String>(spinner.context!!, android.R.layout.simple_spinner_item,
            it)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.addAll(it)
        spinner.adapter = adapter
        adapter.notifyDataSetChanged()
        //spinner.setSelection(0)
        Timber.i("Setting the adapter now")
    }
}

@BindingAdapter("onItemSelected")
fun setOnItemSelected(spinner: Spinner, viewModel : HomeViewModel?) {
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel?.spinnerPosition = position
            viewModel?.onVersionChangedOnSpinner(spinner.getItemAtPosition(position) as String)
        }

    }
}

@BindingAdapter("initialPosition")
fun setInitialPosition(spinner: Spinner, position : Int = 0) {
    spinner.doOnLayout { spinner.setSelection(position) }
    spinner.onFocusChangeListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {

        }
    }
}




