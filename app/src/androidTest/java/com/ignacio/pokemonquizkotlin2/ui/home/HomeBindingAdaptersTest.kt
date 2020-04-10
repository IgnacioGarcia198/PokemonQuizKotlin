package com.ignacio.pokemonquizkotlin2.ui.home

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.ignacio.pokemonquizkotlin2.MyApplication
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import com.nhaarman.mockitokotlin2.*
import junit.framework.Assert.*
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.junit.runner.RunWith


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
        val textView = TextView(InstrumentationRegistry.getInstrumentation().context)
        textView.dailyOrDetailChange(false)
        assertThat(textView.visibility, `is`(View.GONE))
        textView.dailyOrDetailChange(true)
        //assert(textView.visibility == View.GONE)
        assertThat(textView.visibility,`is`(View.VISIBLE))
        //assertEquals

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
        spinner.layout(0,0,10,10)
        setInitialPosition(spinner,2)
        //spinner.doOnLayout {
            println("we are doing the assertion an spinner position is ${spinner.selectedItemPosition}")
            assertThat(spinner.selectedItemPosition, `is`(2))
            //assertTrue(spinner.selectedItemPosition == 3)
        //}
        //assert(spinner.selectedItemPosition == 2)

    }

    @Test
    fun doOnLayoutBeforeLayout() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val spinner = Spinner(context)
        var called = false
        spinner.doOnLayout {
            called = true
        }
        spinner.layout(0, 0, 10, 10)
        assertTrue(called)
    }

    @Test
    fun setListIntoAdapterTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val spinner = Spinner(context)

        setVersions(spinner,listOf("house","dog","spider","dolphin"))

        assertTrue(
            spinner.adapter.count == 4 &&
                    spinner.adapter.getItem(1) as String == "dog"
        )
    }


    /**
     * Failing
     */
    @Test
    fun setOnItemSelectedTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val spinner = Spinner(context)
        //val spinnerSpy = spy(spinner)

        val adapter = ArrayAdapter<String>(
            spinner.context!!, android.R.layout.simple_spinner_item, context.resources.getStringArray(R.array.time_options)

        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        //setVersions(spinner,listOf("house","dog","spider","dolphin"))
        val myApplication : MyApplication = mock()
        val repository : PokemonRepository = mock()
        val homeViewModel = HomeViewModel(myApplication,repository)
        val spy = spy(homeViewModel)
        //val homeViewModel : HomeViewModel = mock()


        spinner.setOnItemSelected(homeViewModel)
        spinner.layout(0,0,10,10)
        assertTrue(spinner.onItemSelectedListener != null)
        println("spinner listener issssssssssssss: ${spinner.onItemSelectedListener}")
        //val listenerSpy = spy(spinner.onItemSelectedListener)

        //verify(listenerSpy)!!.onItemSelected(any(),spinner,2, any())
        //println("listener position is ${spinner.onItemSelected}")


        spinner.doOnLayout {
            spinner.setSelection(2)
            assertTrue(spinner.selectedItemPosition == 2)
            assertTrue(spinner.selectedItem.toString() == "1 minute")
            assertThat(homeViewModel.spinnerPosition, `is`(2))
        }


        //assertTrue(spinner.selectedItemPosition == 3)
        //verify(homeViewModel).spinnerPosition
        //assertTrue(homeViewModel.spinnerPosition == 2)

        //verify(spy, atLeastOnce()).onVersionChangedOnSpinner(spinner.selectedItem as String)
    }



    /*
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
     */

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

    @Test
    fun addOrRemoveErrorMsgTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val constraintLayout = ConstraintLayout(context)

        assertNull(constraintLayout.findViewById(R.id.errorLayout))

        addOrRemoveErrorMsg(constraintLayout, PokemonResponseState.NETWORK_ERROR)

        assertEquals(constraintLayout, constraintLayout.findViewById<ConstraintLayout>(R.id.errorLayout).parent)
        assertNotNull(constraintLayout.findViewById(R.id.errorLayout))
        assertFalse(constraintLayout.isClickable)
        assertNotNull(constraintLayout.findViewById(R.id.errorImage))
        assertNotNull(constraintLayout.findViewById(R.id.errorText))
        assertEquals(constraintLayout.findViewById<ImageView>(R.id.errorImage).drawable.constantState,context.getDrawable(R.drawable.ic_cloud_off_black_24dp)!!.constantState)
        assertEquals(constraintLayout.findViewById<TextView>(R.id.errorText).text,context.getString(R.string.network_error_msg))



        addOrRemoveErrorMsg(constraintLayout, PokemonResponseState.DB_ERROR)

        assertEquals(constraintLayout, constraintLayout.findViewById<ConstraintLayout>(R.id.errorLayout).parent)
        assertNotNull(constraintLayout.findViewById(R.id.errorLayout))
        assertFalse(constraintLayout.isClickable)
        assertNotNull(constraintLayout.findViewById(R.id.errorImage))
        assertNotNull(constraintLayout.findViewById(R.id.errorText))
        assertEquals(constraintLayout.findViewById<ImageView>(R.id.errorImage).drawable.constantState,context.getDrawable(R.drawable.ic_broken_image_black_24dp)!!.constantState)
        assertEquals(constraintLayout.findViewById<TextView>(R.id.errorText).text,context.getString(R.string.db_error_msg))


        addOrRemoveErrorMsg(constraintLayout, PokemonResponseState.DONE)

        assertNull(constraintLayout.findViewById(R.id.errorLayout))
        assertEquals(constraintLayout.indexOfChild(constraintLayout.findViewById(R.id.errorLayout)), -1)
        assertTrue(constraintLayout.isClickable)
    }

    @Test
    fun showErrorViewTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val constraintLayout = ConstraintLayout(context)
        //val constraintLayout = LayoutInflater.from(context).inflate(R.layout.fragment_home,null,false)
        assertNull(constraintLayout.findViewById(R.id.errorImage))
        assertNull(constraintLayout.findViewById(R.id.errorLayout))

        //assertFalse(constraintLayout.findViewById<ConstraintLayout>(R.id.errorLayout).isAttachedToWindow)
        showErrorView(constraintLayout,R.drawable.ic_cloud_off_black_24dp,R.string.network_error_msg)

        assertEquals(constraintLayout, constraintLayout.findViewById<ConstraintLayout>(R.id.errorLayout).parent)
        assertNotNull(constraintLayout.findViewById(R.id.errorLayout))
        assertFalse(constraintLayout.isClickable)
        assertNotNull(constraintLayout.findViewById(R.id.errorImage))
        assertNotNull(constraintLayout.findViewById(R.id.errorText))
        assertEquals(constraintLayout.findViewById<ImageView>(R.id.errorImage).drawable.constantState,context.getDrawable(R.drawable.ic_cloud_off_black_24dp)!!.constantState)
        assertEquals(constraintLayout.findViewById<TextView>(R.id.errorText).text,context.getString(R.string.network_error_msg))
        //assertTrue(constraintLayout.findViewById<ConstraintLayout>(R.id.errorLayout).isAttachedToWindow)
    }

    @Test
    fun removeErrorViewTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val constraintLayout = ConstraintLayout(context)
        assertNull(constraintLayout.findViewById(R.id.errorLayout))
        //val constraintLayout = LayoutInflater.from(context).inflate(R.layout.fragment_home,null,false)
        showErrorView(constraintLayout,R.drawable.ic_cloud_off_black_24dp,R.string.network_error_msg)
        assertNotNull(constraintLayout.findViewById(R.id.errorLayout))
        //assertTrue(constraintLayout.findViewById<ConstraintLayout>(R.id.errorLayout).isAttachedToWindow)
        assertFalse(constraintLayout.isClickable)

        removeErrorView(constraintLayout)


        assertNull(constraintLayout.findViewById(R.id.errorLayout))
        assertEquals(constraintLayout.indexOfChild(constraintLayout.findViewById(R.id.errorLayout)), -1)
    }




}