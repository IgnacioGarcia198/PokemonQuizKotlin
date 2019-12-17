package com.ignacio.pokemonquizkotlin2.ui.home

import android.graphics.drawable.Drawable
import com.ignacio.pokemonquizkotlin2.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.databinding.BindingAdapter
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import timber.log.Timber


@RunWith(AndroidJUnit4::class)
class HomeBindingAdaptersTest {

    /*@Test
    fun isVisibleShouldBeEasilyControlledWithABoolean() {
        val v = View(InstrumentationRegistry.getTargetContext())
        setIsVisible(v, true) // visible
        assertThat(v.visibility).isEqualTo(View.VISIBLE)
        setIsVisible(v, false) // gone
        assertThat(v.visibility).isEqualTo(View.GONE)
    }*/

    @Test
    fun dailyOrDetailChangeHidesTextView() {
        val textView = TextView(InstrumentationRegistry.getInstrumentation().targetContext)
        dailyOrDetailChange(textView,false)
        assert(textView.visibility == View.GONE)
        dailyOrDetailChange(textView,true)
        assert(textView.visibility == View.VISIBLE)
    }

    @Test
    fun setInitialPositionInSpinnerTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val spinner = Spinner(context)
        val adapter = ArrayAdapter<String>(
            spinner.context!!, android.R.layout.simple_spinner_item, context.resources.getStringArray(R.array.time_options)

        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        setInitialPosition(spinner,2)
        assert(spinner.selectedItemPosition == 2)
    }

    @Test
    fun setListIntoAdapterTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val spinner = Spinner(context)
        /*val adapter = ArrayAdapter<String>(
            spinner.context!!, android.R.layout.simple_spinner_item, context.resources.getStringArray(R.array.time_options)

        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter*/
        setVersions(spinner,listOf("house","dog","spider","dolphin"))
        //assert(spinner.selectedItemPosition == 2)
        assert(
            spinner.adapter.count == 4 &&
                    spinner.adapter.getItem(0) as String == "house"
        )
    }

    /*@BindingAdapter("versions")
    fun setVersions(spinner: Spinner, versionList: List<String>?) {
        Timber.i("Version in bindingAdapter : $versionList")
        versionList?.let {
            //Timber.i("Version in bindingAdapter : $it")
            val adapter = ArrayAdapter<String>(
                spinner.context!!, android.R.layout.simple_spinner_item,
                it
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter.addAll(it)
            spinner.adapter = adapter
            adapter.notifyDataSetChanged()
            //spinner.setSelection(0)
            Timber.i("Setting the adapter now")
        }
    }*/

    @Test
    fun setImageUrlTest() {
        val requestManager : RequestManager = mock()
        val requestBuilder : RequestBuilder<Drawable> = mock()
        val glide : Glide = mock()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val image = ImageView(context)
        whenever(Glide.with(image)).thenReturn(requestManager)
        whenever(requestManager.load(eq("caca"))).thenReturn(requestBuilder)

        setImageUrl(image,"caca")
        verify(requestManager).load("caca")
        verify(requestBuilder).into(image)
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



    @BindingAdapter("responseState")
    fun addOrRemoveErrorMsg(viewGroup: ViewGroup, responseState: PokemonResponseState) {
        when (responseState) {
            PokemonResponseState.NETWORK_ERROR -> {
                Timber.i("showing network error")
                showErrorView(
                    viewGroup,
                    com.ignacio.pokemonquizkotlin2.R.drawable.ic_cloud_off_black_24dp, com.ignacio.pokemonquizkotlin2.R.string.network_error_msg
                )
            }
            PokemonResponseState.DB_ERROR -> {
                showErrorView(
                    viewGroup,
                    com.ignacio.pokemonquizkotlin2.R.drawable.ic_broken_image_black_24dp, com.ignacio.pokemonquizkotlin2.R.string.db_error_msg
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
        var errorView: ConstraintLayout? = layout.findViewById<ConstraintLayout>(com.ignacio.pokemonquizkotlin2.R.id.errorLayout)
        if (errorView == null) {
            //val viewGroup : ViewGroup = binding.root as ViewGroup
            errorView = LayoutInflater.from(layout.context).inflate(
                com.ignacio.pokemonquizkotlin2.R.layout.error_layout, layout, false
            )
                    as ConstraintLayout
            layout.addView(errorView)
            layout.isClickable = false
        }
        //layout.visibility = View.VISIBLE
        errorView.findViewById<ImageView>(com.ignacio.pokemonquizkotlin2.R.id.errorImage)
            .setImageResource(img)
        errorView.findViewById<TextView>(com.ignacio.pokemonquizkotlin2.R.id.errorText).setText(txt)
    }

    fun removeErrorView(layout: ViewGroup) {
        var errorView: ConstraintLayout? = layout.findViewById<ConstraintLayout>(com.ignacio.pokemonquizkotlin2.R.id.errorLayout)
        if (errorView != null) {
            layout.removeView(layout)
        }
    }


}