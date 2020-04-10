package com.ignacio.pokemonquizkotlin2.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ignacio.pokemonquizkotlin2.testutils.CoroutineTestRule
import com.ignacio.pokemonquizkotlin2.testutils.observeOnce
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    //private val pokeIdLiveData = MutableLiveData<Resource<Repo>>()
    private val showError = MutableLiveData<Boolean>(false)
    private lateinit var viewModel: HomeViewModel
    //private lateinit var mockBindingAdapter: FragmentBindingAdapters

    @Before
    fun init() {
        viewModel = mock(HomeViewModel::class.java)
        //mockBindingAdapter = mock(FragmentBindingAdapters::class.java)
        //doNothing().`when`(viewModel.initPush(ArgumentMatchers.anyInt()))

        whenever(viewModel.showError).thenReturn(showError)

    }

    @Test
    fun initialErrorTest() {
        showError.observeOnce {
            assert(!it)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

    //private val homeFragment = TestHomeFragment().apply {
     //   arguments = HomeFragmentArgs.Builder(0).build().toBundle()
    //}


    //class TestHomeFragment : HomeFragment() {
        /*val navController : NavController = mock()
        override fun findNavController() = navController*/
    //}

}