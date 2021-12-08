package com.ignacio.pokemonquizkotlin2.data.api

import com.ignacio.pokemonquizkotlin2.BuildConfig
import com.ignacio.pokemonquizkotlin2.data.api.speciesdetail.NetworkSpeciesDetail
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Deferred
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * A retrofit service to fetch a devbyte playlist.
 */
interface PokemonService {
    @GET("api/v2/pokemon-species")
    fun getPokemonList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Deferred<NetworkPokemonContainer>

    @GET("api/v2/pokemon-species/{pokid}")
    fun getSpecieDetail(@Path("pokid") pokid: Int): Deferred<NetworkSpeciesDetail>

    @GET("api/v2/version")
    fun getVersionList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Deferred<NetworkPokemonContainer>
}

/**
 * Main entry point for network access. Call like `DevByteNetwork.devbytes.getPlaylist()`
 */
object PokemonNetwork {

    private val logLevel = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }
    val loggingInterceptor = HttpLoggingInterceptor().apply { level = logLevel }

    // Configure retrofit to parse JSON and use coroutines
    val okHttp = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    private val contentType = "application/json".toMediaType()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/")
        .client(okHttp)
        .addConverterFactory(Json.asConverterFactory(contentType))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val pokemonApiService = retrofit.create(PokemonService::class.java)
}
