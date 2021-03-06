package com.ignacio.pokemonquizkotlin2.ui.play

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import com.ignacio.pokemonquizkotlin2.R
import com.ignacio.pokemonquizkotlin2.androidtestutil.DataBindingIdlingResource
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.data.ServiceLocator
import com.ignacio.pokemonquizkotlin2.db.MyDatabase
import com.ignacio.pokemonquizkotlin2.db.PokemonDao
import com.ignacio.pokemonquizkotlin2.testing.SingleFragmentActivity
import com.ignacio.pokemonquizkotlin2.ui.PlayViewModelFactory
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PlayFragmentMyActivityTest {

    private lateinit var repository: PokemonRepositoryInterface
    private lateinit var dao : PokemonDao
    private lateinit var db : MyDatabase
    private lateinit var playFragment: PlayFragment
    // An Idling Resource that waits for Data Binding to have no pending bindings
    //private val dataBindingIdlingResource = DataBindingIdlingResource2()


    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)
    private val idlingResource = DataBindingIdlingResource(activityRule)

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */

    private fun registerIdlingResource() {
        //IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        //IdlingRegistry.getInstance().register(dataBindingIdlingResource)
        IdlingRegistry.getInstance().register(idlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */

    private fun unregisterIdlingResource() {
        //IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        //IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
        IdlingRegistry.getInstance().unregister(idlingResource)

    }

    @Before
    fun initRepository() {
        //repository = FakeRepository()
        /*val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MyDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.pokemonDao
        ServiceLocator.repository = PokemonRepository(db, PokemonNetwork.pokemonApiService)
        ServiceLocator.database = db*/

        //registerIdlingResource()

        playFragment = PlayFragment().apply {
            arguments = PlayFragmentArgs.Builder(false,10).build().toBundle()
        }
        activityRule.activity.setFragment(playFragment)
    }


    @After
    fun tearDown() {
        //cleanupDb()
        activityRule.activity.supportFragmentManager.beginTransaction().remove(playFragment).commit()
        //unregisterIdlingResource()
    }

    private fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }


    @Test
    fun viewsInInitialState() {
        // GIVEN
        //val bundle = PlayFragmentArgs.Builder(false,10).build().toBundle()
        // WHEN
        //val scenario = launchFragmentInContainer<PlayFragment>(bundle, R.style.AppTheme)
        //dataBindingIdlingResource.monitorFragment(scenario)
        // CHECK
        onView(withId(R.id.radioGroupProgressBar)).check(matches(isDisplayed()))
        onView(withId(R.id.pbTV)).check(matches(isDisplayed())).check(matches(withText("Refreshing pokemon…")))
        onView(withId(R.id.customRadioGroup)).check(matches(isDisplayed()))
        onView(withId(R.id.lastResultTV)).check(matches(withText("Last result:"))).check(matches(withCompoundDrawables(
            arrayOf(0,0,0,0))))
        onView(withId(R.id.rightAnswersTV)).check(matches(isDisplayed())).check(matches(withText("0")))
        onView(withId(R.id.wrongAnswersTV)).check(matches(isDisplayed())).check(matches(withText("0")))
        onView(withId(R.id.gameTimeTV)).check(matches(isDisplayed())).check(matches(withRegex("\\d{2}:\\d{2}")))

        for(i in 0 until NUMBER_OF_ANSWERS-1) {
            onView(nthChildOf(withId(R.id.customRadioGroup),i)).check(matches(not(isEnabled())))
        }

    }

    lateinit var  spy: PlayViewModel
    class TestPlayFragment : PlayFragment() {
        lateinit var spy : PlayViewModel
        override fun provideViewModel(questionsOrTime: Boolean, gameLength: Int): PlayViewModel {
            val viewModel = super.provideViewModel(questionsOrTime, gameLength)
            spy = spy(viewModel)

            return viewModel
        }
    }

    @Test
    fun pokemonsAreRefreshed() {
        // GIVEN
        val bundle = PlayFragmentArgs.Builder(false,10).build().toBundle()
        // WHEN
        val pepe = launchFragmentInContainer<TestPlayFragment>(bundle, R.style.AppTheme)
        var theSpy : PlayViewModel? = null
        pepe.onFragment {
            Thread.sleep(100)
            theSpy = it.spy
            verify(theSpy)!!.refreshPokemon(0,-1)
        }
        //Thread.sleep(100)


        // CHECK


    }

    @Test
    fun navegatesToRecords() {

        // GIVEN
        val bundle = PlayFragmentArgs.Builder(false,10).build().toBundle()
        // WHEN
        val scenario = launchFragmentInContainer<PlayFragment>(bundle, R.style.AppTheme)
        val navController : NavController = mock()
        Navigation.setViewNavController(playFragment.view!!, navController)
        //scenario.onFragment {
            //Navigation.setViewNavController(it.view!!,navController)
        //}

        //val record =  // set the record for navigation
        // CHECK
        //verify(navController).navigate(PlayFragmentDirections.actionNavPlayToNavGameRecords())

    }

    fun nthChildOf(parentMatcher: Matcher<View>, childPosition: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("with $childPosition child view of type parentMatcher")
            }

            override fun matchesSafely(item: View): Boolean {
                if (item.parent !is ViewGroup) {
                    return parentMatcher.matches(item.parent)
                }

                val group = item.parent as ViewGroup
                return parentMatcher.matches(item.parent) && group.getChildAt(childPosition) == item
            }
        }

    }

    fun withRegex(regex : String) : Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("Checking the matcher on the received text with pattern $regex")
            }

            override fun matchesSafely(item: TextView?): Boolean {
                return item?.text?.matches(regex.toRegex()) ?: false
            }
        }
    }

    fun withCompoundDrawables(
        drawables : Array<Int> = arrayOf(0,0,0,0)
        ) : Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("Checking the drawables on the view with the ids $drawables")
            }

            override fun matchesSafely(item: TextView?): Boolean {
                return item?.let {
                    val compoundDrawables = item.compoundDrawables
                    var b = true
                    for(i in drawables) {
                        val resource = drawables[i]
                        b = b &&
                        when {
                            resource == 0 -> compoundDrawables[i] == null
                            resource > 0 -> compoundDrawables[i].constantState == item.context.getDrawable(drawables[i])!!.constantState
                            else -> false
                        }
                    }
                    return@matchesSafely b
                } ?: false
            }

        }
    }

}