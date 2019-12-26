package com.ignacio.pokemonquizkotlin2.data.api
import com.ignacio.pokemonquizkotlin2.BuildConfig
import com.ignacio.pokemonquizkotlin2.data.api.speciesdetail.NetworkSpeciesDetail
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * A retrofit service to fetch a devbyte playlist.
 */
interface PokemonService {
    @GET("api/v2/pokemon-species")
    fun getPokemonList(@Query("offset") offset : Int,
                       @Query("limit") limit : Int): Deferred<NetworkPokemonContainer>

    @GET("api/v2/pokemon-species/{pokid}")
    fun getSpecieDetail(@Path("pokid") pokid: Int): Deferred<NetworkSpeciesDetail>

    @GET("api/v2/version")
    fun getVersionList(@Query("offset") offset : Int,
                       @Query("limit") limit : Int): Deferred<NetworkPokemonContainer>
}

/**
 * Main entry point for network access. Call like `DevByteNetwork.devbytes.getPlaylist()`
 */
object PokemonNetwork {

    val level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }
    val loggingInterceptor = HttpLoggingInterceptor().setLevel(level)
    // Configure retrofit to parse JSON and use coroutines
    val okHttp = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/")
        .client(okHttp)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val pokemonApiService = retrofit.create(PokemonService::class.java)
    fun <T>createService (service : Class<T>) : T {
        return retrofit.create(service)
    }


}
