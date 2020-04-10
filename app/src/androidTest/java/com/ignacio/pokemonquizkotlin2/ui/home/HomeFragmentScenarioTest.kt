package com.ignacio.pokemonquizkotlin2.ui.home

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.androidtestutil.DataBindingIdlingResource2
import com.ignacio.pokemonquizkotlin2.androidtestutil.FakeRepository
import com.ignacio.pokemonquizkotlin2.androidtestutil.monitorFragment
import com.ignacio.pokemonquizkotlin2.data.api.extractFlavorText
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModelFactory
import com.ignacio.pokemonquizkotlin2.utils.EspressoIdlingResource
import com.ignacio.pokemonquizkotlin2.utils.writeLine
import com.nhaarman.mockitokotlin2.spy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class HomeFragmentScenarioTest {
    //@get:Rule
    //var coroutinesTestRule = CoroutineTestRule()

    val repository = FakeRepository()
    val repositorySpy = spy(repository)
    class TestHomeFragment1(val repository: FakeRepository) : HomeFragment() {
        override fun provideViewModel(): HomeViewModel {
            return BaseViewModelFactory(requireActivity().application,repository).create(HomeViewModel::class.java)
        }
    }

    class HomeFragmentFactory(val repository: FakeRepository) : FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            return TestHomeFragment1(repository)
        }
    }

    /*fun getFragmentScenario(id:Int) : FragmentScenario<TestHomeFragment> {

        val fragmentArgs = HomeFragmentArgs.Builder().setNewId(id).build().toBundle()

        //return FragmentScenario.launchInContainer<TestHomeFragment>(fragmentArgs, R.style.AppTheme)

        //val fragmentArgs = HomeFragmentArgs.Builder(id).build().toBundle()
        //return launchFragmentInContainer<HomeFragment>(fragmentArgs)
    }*/
    private lateinit var scenario : FragmentScenario<TestHomeFragment1>
    //private lateinit var scenario : FragmentScenario<HomeFragment>
    private val dataBindingIdlingResource = DataBindingIdlingResource2()

    @Before
    fun initFragment() {
        //val scenario = getFragmentScenario(0)
        val fragmentArgs = HomeFragmentArgs.Builder().setNewId(1).build().toBundle()
        //launchFragmentInContainer<HomeFragment>(fragmentArgs,R.style.AppTheme)
        scenario = launchFragmentInContainer<TestHomeFragment1>(fragmentArgs,R.style.AppTheme,HomeFragmentFactory(repository))
        //scenario = launchFragmentInContainer<HomeFragment>(fragmentArgs,R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)
    }

    @After
    fun resetDispatchers() {
        //Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }


    @Test
    fun fragmentStarts() {
        onView(withId(R.id.textView7)).check(matches(withText("Today's pokemon is…")))
    }

    @Test
    fun checkScreenTest() {

        onView(withId(R.id.textView7)).check(matches(not(isDisplayed())))
            //.check(matches(ViewMatchers.withText(getString(R.string.today_s_pokemon_is))))
        onView(withId(R.id.pokNameTV)).check(matches(isDisplayed()))//.check(matches(withText("Bulbasaur")))
        onView(withId(R.id.mainImageView)).check(matches(isDisplayed()))
        onView(withId(R.id.textSelectPrompt)).check(matches(isDisplayed()))
            .check(matches(withText(getString(R.string.text_from_version_prompt))))
        onView(withId(R.id.spinner)).check(matches(isDisplayed()))
        onView(withId(R.id.flavorTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.spinner)).check(matches(isDisplayed()))
        onView(withId(R.id.spinner)).check(matches(withSpinnerText(
            FakeRepository.speciesDetail!!.flavorTextEntries[0].version.name
        )))

        onView(withId(R.id.flavorTextView)).check(matches(isDisplayed()))
            .check(matches(withText(HomeFragmentTestAgain.theflavorAndName.first)))

    }

    @Test
    fun selectVersion() {
        onView(withId(R.id.spinner)).perform(click())
        //onView(allOf(withId(android.R.id.text1), withText("y"))).perform(click()) // all of them do the same :)
        //onData(allOf(`is`(instanceOf(String::class.java)),
        //    `is`("y"))).perform(click())
        onData(anything()).atPosition(2).perform(click())
        onView(withId(R.id.spinner)).check(matches(
            withSpinnerText(
            FakeRepository.theversions[2])
        ))

        onView(withId(R.id.flavorTextView)).check(matches(isDisplayed()))
            .check(matches(withText(FakeRepository.speciesDetail!!.extractFlavorText("en","y"))))
        writeLine()
        Timber.i("test function is finish")
    }

    private fun getString(@StringRes id : Int) : String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(id)
    }
    /*private fun<T : View> findView(@IdRes id : Int) : T {
        return homeFragment.view!!.findViewById<T>(id)
    }*/

    private fun getContext() : Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

}