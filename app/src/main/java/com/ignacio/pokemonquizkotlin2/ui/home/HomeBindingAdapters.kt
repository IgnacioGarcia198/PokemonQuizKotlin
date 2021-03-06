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
            Timber.d("setting the image with url $url")
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
    fun Spinner.setOnItemSelected(viewModel: HomeViewModel?) {
        Timber.d("setting onitemselected now")
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel?.spinnerPosition = position
                viewModel?.onVersionChangedOnSpinner(this@setOnItemSelected.getItemAtPosition(position) as String)
                //callback(spinner.getItemAtPosition(position) as String)
            }

        }
    }

    @BindingAdapter("initialPosition",requireAll = false)
    fun setInitialPosition(spinner: Spinner, position: Int = 0) {
        spinner.doOnLayout { spinner.setSelection(position) }
        //spinner.setSelection(position)
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
            layout.removeView(errorView)
        }
    }

    @BindingAdapter("dailyOrDetail")
    fun TextView.dailyOrDetailChange(dailyOrDetail: Boolean) {
        this.visibility =
            when (dailyOrDetail) {
                false -> {
                    Timber.d("the top text is visible")
                    View.VISIBLE
                }
                true -> {
                    Timber.d("the top text is not visible")
                    View.GONE
                }
            }

    }





