package com.ignacio.pokemonquizkotlin2.ui.home

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


//object HomeBindingAdapters {
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
    fun setVersions(spinner: Spinner, versionList: List<String>?) {
        Timber.i("Version in bindingAdapter : $versionList")
        versionList?.let {
            //Timber.i("Version in bindingAdapter : $it")
            val adapter = ArrayAdapter<String>(
                spinner.context!!, android.R.layout.simple_spinner_item

            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter.addAll(it)
            spinner.adapter = adapter
            adapter.notifyDataSetChanged()
            //spinner.setSelection(0)
            Timber.i("Setting the adapter now")
        }
    }

    @BindingAdapter("onItemSelected")
    fun setOnItemSelected(spinner: Spinner, viewModel: HomeViewModel?) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel?.spinnerPosition = position
                viewModel?.onVersionChangedOnSpinner(spinner.getItemAtPosition(position) as String)
            }

        }
    }

    @BindingAdapter("initialPosition")
    fun setInitialPosition(spinner: Spinner, position: Int = 0) {
        spinner.doOnLayout { spinner.setSelection(position) }
    }

    @BindingAdapter("responseState")
    fun addOrRemoveErrorMsg(viewGroup: ViewGroup, responseState: PokemonResponseState) {
        when (responseState) {
            PokemonResponseState.NETWORK_ERROR -> {
                Timber.i("showing network error")
                showErrorView(
                    viewGroup,
                    R.drawable.ic_cloud_off_black_24dp, R.string.network_error_msg
                )
            }
            PokemonResponseState.DB_ERROR -> {
                showErrorView(
                    viewGroup,
                    R.drawable.ic_broken_image_black_24dp, R.string.db_error_msg
                )
            }
            else -> {
                removeErrorView(viewGroup)
                viewGroup.isClickable = true
            }
        }
    }

    fun showErrorView(
        layout: ViewGroup,
        img: Int, txt: Int
    ) {
        var errorView: ConstraintLayout? = layout.findViewById<ConstraintLayout>(R.id.errorLayout)
        if (errorView == null) {
            //val viewGroup : ViewGroup = binding.root as ViewGroup
            errorView = LayoutInflater.from(layout.context).inflate(
                R.layout.error_layout, layout, false
            )
                    as ConstraintLayout
            layout.addView(errorView)
            layout.isClickable = false
        }
        //layout.visibility = View.VISIBLE
        errorView.findViewById<ImageView>(R.id.errorImage)
            .setImageResource(img)
        errorView.findViewById<TextView>(R.id.errorText).setText(txt)
    }

    fun removeErrorView(layout: ViewGroup) {
        var errorView: ConstraintLayout? = layout.findViewById<ConstraintLayout>(R.id.errorLayout)
        if (errorView != null) {
            layout.removeView(layout)
        }
    }

    @BindingAdapter("dailyOrDetail")
    fun dailyOrDetailChange(textView: TextView, dailyOrDetail: Boolean) {
        textView.visibility =
            when (dailyOrDetail) {
                true -> View.VISIBLE
                false -> View.GONE
            }
    }

/*@BindingAdapter("responseState")
fun getResponseState(layout : ConstraintLayout, responseState: PokemonResponseState){
    //val img = layout.findViewById<ImageView>(R.id.errorImage)
    //val txt = layout.findViewById<TextView>(R.id.errorText)
    //lateinit var errorLayout : ConstraintLayout
    when (responseState) {
        PokemonResponseState.NETWORK_ERROR -> {
            showErrorView(layout, R.drawable.ic_cloud_off_black_24dp,R.string.network_error_msg)
        }
        PokemonResponseState.DB_ERROR -> {
            showErrorView(layout, errorLayout,)
        }
        else -> layout.visibility = View.GONE
    }

}

private fun showErrorView(
    layout: ConstraintLayout,
 img : Int, txt : Int) {
    var errorLayout1 = errorLayout
    if (layout.findViewById<ConstraintLayout>(R.id.errorLayout) == null) {
        errorLayout1 = LayoutInflater.from(layout.context).inflate(
            R.layout.error_layout, layout, false
        )
                as ConstraintLayout
        layout.addView(errorLayout1)
        layout.isClickable = false
    }
    //layout.visibility = View.VISIBLE
    errorLayout1.findViewById<ImageView>(R.id.errorImage)
        .setImageResource(img)
    errorLayout1.findViewById<TextView>(R.id.errorText).setText(txt)
}


@BindingAdapter("responseState")
fun getResponseState(img : ImageView, responseState: PokemonResponseState){
    if(responseState == PokemonResponseState.NETWORK_ERROR) {
        img.setImageResource(R.drawable.ic_cloud_off_black_24dp)
    }
    else if(responseState == PokemonResponseState.DB_ERROR) {
        img.setImageResource(R.drawable.ic_broken_image_black_24dp)

    }

}*/

//}




