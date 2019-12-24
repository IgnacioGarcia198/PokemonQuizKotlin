package com.ignacio.pokemonquizkotlin2.ui.home

import android.content.SharedPreferences
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
import com.ignacio.pokemonquizkotlin2.androidtestutil.ViewModelUtil
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModelFactory
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentScenarioTest {


    val testViewModel : HomeViewModel = mock()
    class TestHomeFragment1(val testViewModel: HomeViewModel) : HomeFragment() {
        override fun provideViewModel(): HomeViewModel {
            return testViewModel
        }

    }

    /*fun getFragmentScenario(id:Int) : FragmentScenario<TestHomeFragment> {

        val fragmentArgs = HomeFragmentArgs.Builder().setNewId(id).build().toBundle()

        //return FragmentScenario.launchInContainer<TestHomeFragment>(fragmentArgs, R.style.AppTheme)

        //val fragmentArgs = HomeFragmentArgs.Builder(id).build().toBundle()
        //return launchFragmentInContainer<HomeFragment>(fragmentArgs)
    }*/

    @Test
    fun testEventFragment() {
        //val scenario = getFragmentScenario(0)
        val fragmentArgs = HomeFragmentArgs.Builder().setNewId(0).build().toBundle()
        //launchFragmentInContainer<HomeFragment>(fragmentArgs,R.style.AppTheme)
        launchFragmentInContainer<TestHomeFragment1>(fragmentArgs,R.style.AppTheme)
        onView(withId(R.id.textView7)).check(matches(withText("Today\'s pokemon is&#8230;")))

    }

}