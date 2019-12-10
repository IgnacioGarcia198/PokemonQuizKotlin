package com.ignacio.pokemonquizkotlin2.ui

import android.renderscript.ScriptGroup
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingComponent
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.error_layout.view.*
import timber.log.Timber

interface BaseFragment {

    //abstract var binding : ViewDataBinding
    //open var errorView : ConstraintLayout?
    fun addOrRemoveErrorMsg(viewGroup: ViewGroup, responseState: PokemonResponseState) {
        when(responseState) {
            PokemonResponseState.NETWORK_ERROR -> {
                Timber.i("showing network error")
                showErrorView(viewGroup,
                    R.drawable.ic_cloud_off_black_24dp,R.string.network_error_msg)
            }
            PokemonResponseState.DB_ERROR -> {
                showErrorView(viewGroup,
                    R.drawable.ic_broken_image_black_24dp,R.string.db_error_msg)
            }
            else -> {
                removeErrorView(viewGroup)
                viewGroup.isClickable = true
            }
        }
    }

    fun showErrorView(
        layout: ViewGroup,
        img : Int, txt : Int) {
        var errorView : ConstraintLayout? = layout.findViewById<ConstraintLayout>(R.id.errorLayout)
        if(errorView == null) {
            //val viewGroup : ViewGroup = binding.root as ViewGroup
            errorView = LayoutInflater.from(layout.context).inflate(
                R.layout.error_layout,layout, false)
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
        var errorView : ConstraintLayout? = layout.findViewById<ConstraintLayout>(R.id.errorLayout)
        if(errorView != null) {
            layout.removeView(layout)
        }
    }
}