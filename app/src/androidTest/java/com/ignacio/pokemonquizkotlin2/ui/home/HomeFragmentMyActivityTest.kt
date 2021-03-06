package com.ignacio.pokemonquizkotlin2.ui.home

import androidx.annotation.StringRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.androidtestutil.DataBindingIdlingResource
import com.ignacio.pokemonquizkotlin2.androidtestutil.FakeRepository
import com.ignacio.pokemonquizkotlin2.data.ServiceLocator
import com.ignacio.pokemonquizkotlin2.data.api.extractFlavorText
import com.ignacio.pokemonquizkotlin2.testing.SingleFragmentActivity
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModelFactory
import com.ignacio.pokemonquizkotlin2.ui.PlayViewModelFactory
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel
import com.ignacio.pokemonquizkotlin2.utils.EspressoIdlingResource
import com.ignacio.pokemonquizkotlin2.utils.writeLine
import com.nhaarman.mockitokotlin2.spy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber


@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class HomeFragmentMyActivityTest {
    /*@get:Rule
    var coroutinesTestRule = CoroutineTestRule()*/

    val repository = FakeRepository()
    val repositorySpy = spy(repository)
    class TestHomeFragment2(val repository: FakeRepository) : HomeFragment() {
        override fun provideViewModel(): HomeViewModel {
            return BaseViewModelFactory(requireActivity().application,repository).create(HomeViewModel::class.java)
        }
    }
    lateinit var homeFragment : TestHomeFragment2 //: HomeFragment
    /*@Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()*/

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)
    private val idlingResource = DataBindingIdlingResource(activityRule)

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(idlingResource)
    }



    @Before
    fun initFragment() {

        homeFragment = TestHomeFragment2(repository).apply {
            arguments = HomeFragmentArgs.Builder().setNewId(1).build().toBundle()
        }
        activityRule.activity.setFragment(homeFragment)

        /*homeFragment = HomeFragment().apply {
            arguments = HomeFragmentArgs.Builder().setNewId(1).build().toBundle()
        }
        activityRule.activity.setFragment(homeFragment)*/

    }


    @After
    fun tearDown() {
        //cleanupDb()
        activityRule.activity.supportFragmentManager.beginTransaction().remove(homeFragment).commit()
        //unregisterIdlingResource()
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(idlingResource)

    }

    private fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }


    @Test
    fun viewsInInitialState() {
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
        onView(withId(R.id.spinner)).perform(ViewActions.click())
        //onView(allOf(withId(android.R.id.text1), withText("y"))).perform(click()) // all of them do the same :)
        //onData(allOf(`is`(instanceOf(String::class.java)),
        //    `is`("y"))).perform(click())
        Espresso.onData(anything()).atPosition(2).perform(ViewActions.click())
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

}