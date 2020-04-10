package com.ignacio.pokemonquizkotlin2.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.ignacio.pokemonquizkotlin2.R
import kotlinx.android.synthetic.main.custom_progress_bar.view.*
import timber.log.Timber

class CustomProgressBar : RelativeLayout {
    constructor(context: Context) : super(context) {
        inflateThis(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        inflateThis(context)
        attrsSetup(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) :
            super(context, attrs, attributeSetId) {
        inflateThis(context)
        attrsSetup(context, attrs)
    }

    private fun inflateThis(context: Context) {
        LayoutInflater.from(context)
            .inflate(R.layout.custom_progress_bar, this, true)
    }


    private fun attrsSetup(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar)
            pbTV.text = ta.getString(R.styleable.CustomProgressBar_text)
            ta.recycle()
        }
    }

    //val textView: TextView? = pbTV

    var text : String
    get() = pbTV.text.toString()
    set(value) {
        Timber.i("progressbar is setting text to textview")
        pbTV.text = value
    }

}