package com.ignacio.pokemonquizkotlin2.ui.play

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.ignacio.pokemonquizkotlin2.R
import timber.log.Timber

@BindingAdapter("pokemonId", "onSuccess", "onFail", requireAll = false)
fun loadThePokemonImage(imgView: ImageView, id: Int,
                                successCallback : () -> Unit = {},
failCallback : () -> Unit = {}) {
    Timber.d("Setting the image con id $id")
    if(id > 0) {

        val imgUri = imgView.context.getString(R.string.pokemon_image_url,id)
        Glide.with(imgView.context)
            .load(imgUri)
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    failCallback()
                    return false
                }
                // TODO LETS THINK ABOUT EXTRACTING FUNCTION FOR LOADING IMAGES WITH GLIDE... AS STATIC METHODS OR STH.
                // TODO SHOULD CHANGE TO FRESCO FROM FB IN CASE GLIDE GIVES MORE PROBLEMS. WILL TRY IT IN A BRANCH.

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    successCallback()
                    return false
                }
            })
            .apply(RequestOptions()
                .placeholder(R.drawable.transparent_pokeball_padding)
                .error(R.drawable.pokeball_supertransparent_padding)
            )
            .into(imgView)
    }
}

@BindingAdapter("answerList")
fun bindRadioGroup(radioGroup: RadioGroup, answerList: List<String>) {
    Timber.i("answer list length: ${answerList.size}")
    if(answerList.isNotEmpty() && radioGroup.childCount != 0) {
        for((i, answer : String) in answerList.withIndex()) {
                (radioGroup.getChildAt(i) as RadioButton).text = answer
            }
    }
}

@BindingAdapter("onOptionSelected")
fun bindAnswerChange(radioGroup: RadioGroup, viewModel: PlayViewModel) {
    // we only need to know the position of the chosen radiobutton
    Timber.i("Setting onclick with length = ${radioGroup.childCount}")
    if(radioGroup.childCount == 0) {
        for(i in 0 until NUMBER_OF_ANSWERS) {
            val radioButton = RadioButton(radioGroup.context)
            radioButton.setOnClickListener {
                radioGroup.setChildrenEnabled(false)
                radioGroup.check(-1)
                viewModel.onAnswerChosen(i)

            }
            radioGroup.addView(radioButton)
        }
    }
}

@BindingAdapter("lastResult")
fun bindLastResult(textView: TextView, lastResult : Boolean?) {
    lastResult?.let {
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
            if(lastResult)  R.drawable.right_answer_24dp else R.drawable.ic_batsu_red_24dp, 0)
    }
        ?: textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
}

fun RadioGroup.setChildrenEnabled(enabled: Boolean) {
    for(i in 0 until childCount) {
        getChildAt(i).isEnabled = enabled
    }
}

@BindingAdapter("enabled")
fun setEnabled(radioGroup: RadioGroup, enabled : Boolean) {
    radioGroup.setChildrenEnabled(enabled)
}

@BindingAdapter("animationLevel", "maxListener")
fun setAnimationLevel(textView: TextView, level : Float, maxListener : () -> Unit) {
    val parent = textView.parent as ConstraintLayout
    val totalHeight = parent.height - parent.paddingBottom-parent.paddingTop
    //Timber.i("total height is $totalHeight")
    Timber.i("level is $level and total height is $totalHeight")
    val newHeight = (totalHeight*level).toInt()
    Timber.i("new height is $newHeight")
    textView.height = newHeight
    Timber.i("new height in textview is ${textView.height}")
    if(newHeight != 0 && newHeight >= totalHeight) {
        maxListener()
    }
}

