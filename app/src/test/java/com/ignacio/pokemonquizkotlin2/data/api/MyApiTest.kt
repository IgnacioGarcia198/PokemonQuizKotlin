package com.ignacio.pokemonquizkotlin2.data.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.ignacio.pokemonquizkotlin2.data.api.speciesdetail.NetworkSpeciesDetail
import com.ignacio.pokemonquizkotlin2.testutils.CoroutineTestRule
import com.ignacio.pokemonquizkotlin2.testutils.TestObjects
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.io.IOException


@Config(manifest= Config.NONE)
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MyApiTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @get:Rule val server = MockWebServer()

    //@Rule
    //lateinit var server : MockWebServer
    var theApi : PokemonService? = null
    private lateinit var service: Service


    interface Service {
        @GET("/") fun body(): Deferred<NetworkSpeciesDetail>
        @GET("/") fun response(): Deferred<Response<NetworkSpeciesDetail>>
    }

    @Before fun setUp() {
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
        service = retrofit.create(Service::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
    }

    /**
     * These are the mockwebserver tests we are supposed to do when we have coroutines.
     * In case of default Call adapter,
     * we will change the Deferred for a Call and we do execute() in order to get it right away.
     */
    @Test fun bodySuccess200() = runBlocking {
        server.enqueue(MockResponse().setBody(TestObjects.speciesDetailResponseText))

        val deferred = service.body()
        assertThat(deferred.await()).isEqualTo(TestObjects.getBulbasaurDetailResponseTest())
    }

    @Test fun bodySuccess404() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(404))

        val deferred = service.body()
        try {
            deferred.await()
            fail()
        } catch (e: HttpException) {
            assertThat(e).hasMessageThat().isEqualTo("HTTP 404 Client Error")
        }
    }

    @Test fun responseSuccess200() = runBlocking {
        server.enqueue(MockResponse().setBody(TestObjects.speciesDetailResponseText))

        val deferred = service.response()
        val response = deferred.await()
        assertThat(response.isSuccessful).isTrue()
        assertThat(response.body()).isEqualTo(TestObjects.getBulbasaurDetailResponseTest())
    }

    @Test fun responseSuccess404() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(404).setBody(TestObjects.speciesDetailResponseText))

        val deferred = service.response()
        val response = deferred.await()
        assertThat(response.isSuccessful).isFalse()
        //assertThat(response.errorBody()!!.toString()).isEqualTo(TestObjects.getBulbasaurDetailResponseTest().toString())
    }

    @Test fun responseFailure() = runBlocking {
        val mockResponse = MockResponse()
        mockResponse.socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
        server.enqueue(mockResponse)

        val deferred = service.response()
        try {
            deferred.await()
            fail()
        } catch (e: IOException) {
        }
    }


    @Test
    @Throws(Exception::class)
    fun test() { // Test with real api calls
        val service = PokemonNetwork.pokemonApiService

        runBlocking {
            val bulbasaurPokemonDetail = service.getSpecieDetail(1).await()
            assertEquals(TestObjects.getBulbasaurDetailResponseTest(), bulbasaurPokemonDetail)
        }

        runBlocking {
            val allPokemonResponse = service.getPokemonList(0,-1).await()
            assertEquals(TestObjects.getAllThePokemonsResponseTest(), TestObjects.allPokemonsResponse)
        }

        runBlocking {
            val pokemonsFrom42To81Response = service.getPokemonList(41,40).await()
            assertEquals(TestObjects.getPokemonsFrom42To81ResponseTest(), pokemonsFrom42To81Response)
        }

    }




}