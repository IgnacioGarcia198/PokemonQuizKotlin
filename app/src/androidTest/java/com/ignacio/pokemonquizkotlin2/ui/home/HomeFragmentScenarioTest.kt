package com.ignacio.pokemonquizkotlin2.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ignacio.pokemonquizkotlin2.R
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentScenarioTest {

    fun getFragmentScenario(id:Int) : FragmentScenario<HomeFragment> {

        val fragmentArgs = HomeFragmentArgs.Builder().setNewId(id).build().toBundle()
        val viewModel : HomeViewModel = mock()
        return FragmentScenario.launchInContainer(HomeFragment::class.java, fragmentArgs,
            object : FragmentFactory() {
                override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                    return HomeFragment().apply {
                        homeViewModel = viewModel
                    }
                }

            })

        //val fragmentArgs = HomeFragmentArgs.Builder(id).build().toBundle()
        //return launchFragmentInContainer<HomeFragment>(fragmentArgs)
    }

    @Test
    fun testEventFragment() {
        val scenario = getFragmentScenario(0)
        onView(withId(R.id.textView7)).check(matches(withText("Today\'s pokemon is&#8230;")))

    }

}