package com.ignacio.pokemonquizkotlin2.ui.play

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.ignacio.pokemonquizkotlin2.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.databinding.BindingAdapter
import androidx.fragment.app.testing.launchFragment
import androidx.navigation.NavController
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule

import com.ignacio.pokemonquizkotlin2.testing.SingleFragmentActivity
import com.nhaarman.mockitokotlin2.*
import junit.framework.Assert.*
import org.hamcrest.CoreMatchers.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import timber.log.Timber


@RunWith(AndroidJUnit4::class)
class PlayBindingAdaptersTest {

    @get:Rule
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java)

    class TestPlayFragment : PlayFragment() {
        /*companion object {
            fun getInstance(questionsOrTime : Boolean, gameLength :Int) : TestPlayFragment {
                return TestPlayFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean("questionsOrTime",questionsOrTime)
                        putInt("gameLength",gameLength)
                    }
                }
            }
        }*/
    }

    private val fragment = TestPlayFragment().apply {
        arguments = PlayFragmentArgs.Builder(true, 10).build().toBundle()
    }

    /*@Test fun testEventFragment() {
        // The "fragmentArgs" and "factory" arguments are optional.
        //val fragment = TestPlayFragment.getInstance(false,10)
        fragment.playViewModel = mock()
        whenever(fragment.playViewModel.refreshPokemon(ArgumentMatchers.anyInt(),
            ArgumentMatchers.anyInt()))
        activityRule.activity.setFragment(fragment)
        //val context = fragment.context
        val radioGroup = fragment.view!!.findViewById<RadioGroup>(R.id.customRadioGroup)//RadioGroup(context)
        bindAnswerChange(radioGroup,fragment.playViewModel)
        assertEquals(radioGroup.childCount, NUMBER_OF_ANSWERS)

        println("id is ${radioGroup.getChildAt(0).id}")
        onView(withId(radioGroup.getChildAt(0).id)).perform(click())

        //radioGroup.getChildAt(0).performClick()
        com.nhaarman.mockitokotlin2.verify(fragment.playViewModel).onAnswerChosen(0)
        activityRule.activity.finish()
        onView(withId(R.id.nav_share)).check(matches(isDisplayed()))
    }*/

    @Test
    fun bindAnswerChangeTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val radioGroup = RadioGroup(context)
        val viewModel : PlayViewModel = mock()
        bindAnswerChange(radioGroup,viewModel)
        assertEquals(radioGroup.childCount, NUMBER_OF_ANSWERS)

        println("id is ${radioGroup.getChildAt(0).id}")
        onView(withId(radioGroup.getChildAt(0).id)).perform(click())

        //radioGroup.getChildAt(0).performClick()
        //verify(viewModel).onAnswerChosen(0)
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

    @Test
    fun bindRadioGroupTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val radioGroup = RadioGroup(context)
        bindRadioGroup(radioGroup,listOf("dog","cow","car","cat"))

        val textView = TextView(InstrumentationRegistry.getInstrumentation().context)
        //textView.dailyOrDetailChange(false)
        assertThat(textView.visibility, `is`(View.GONE))
        //textView.dailyOrDetailChange(true)
        //assert(textView.visibility == View.GONE)
        assertThat(textView.visibility,`is`(View.VISIBLE))
        //assertEquals

    }

    @BindingAdapter("answerList")
    fun bindRadioGroup(radioGroup: RadioGroup, answerList: List<String>) {
        Timber.i("answer list length: ${answerList.size}")
        if(answerList.isNotEmpty() && radioGroup.childCount != 0) {
            for((i, answer : String) in answerList.withIndex()) {
                //Timber.i("radiogroup has ${radioGroup.childCount} children")
                (radioGroup.getChildAt(i) as RadioButton).text = answer
            }
            //radioGroup.check(-1)
        }
    }


    /*@Test
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
    }*/




}