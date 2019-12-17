package com.ignacio.pokemonquizkotlin2.data.api
import com.ignacio.pokemonquizkotlin2.data.api.speciesdetail.NetworkSpeciesDetail
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
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

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val pokemonApiService = retrofit.create(PokemonService::class.java)
    /*fun createService() : PokemonService {
        return retrofit.create(PokemonService::class.java)
    }*/


}
