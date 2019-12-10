package com.ignacio.pokemonquizkotlin2.data.api

import com.ignacio.pokemonquizkotlin2.data.api.speciesdetail.NetworkSpeciesDetail
import com.ignacio.pokemonquizkotlin2.data.db.DatabasePokemon
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import timber.log.Timber

@JsonClass(generateAdapter = true)
data class NetworkPokemonContainer (
    val count : Int,
    val next : String?,
    val previous : String?,
    val results : List<NameUrlPair?>
)

@JsonClass(generateAdapter = true)
data class NameUrlPair(
    @Json(name = "name")
    val name : String,
    @Json(name = "url")
    val url : String
)


fun NetworkPokemonContainer.asDatabaseModel(offset : Int = 0, limit : Int = -1) : List<DatabasePokemon> {
    var i = offset+1
    val thelimit = if(limit == -1) HomeViewModel.DOWNLOAD_SIZE else limit
    return results.take(thelimit).map {
        DatabasePokemon(i++, it!!.name,usedAsQuestion = false)
    }
}

fun NetworkSpeciesDetail.extractAvailableVersions(language: String) : List<String> {
    return flavorTextEntries.map { it.version.name }.distinct()
}

fun NetworkSpeciesDetail.extractFlavorTextAndName(language : String, version : String) : Pair<String,String> {
    return Pair(
        flavorTextEntries.filter {
            Timber.i("extractFlavorTextAndName: ${it.toString()}")
            if(it.language.name == language && it.version.name == version) {
                Timber.i("flavor entry selected: ${it.flavorText}")}
            it.language.name == language && it.version.name == version
        }.map { it.flavorText.replace("\n", " ") }.joinToString(separator = "\n"),
        names.filter {
            if(it.language.name == language) {Timber.i("language selected: ${it.language.name}")}
            it.language.name == language }.map {Timber.i("name selected: ${it.name}")
            it.name }.joinToString())
}

fun NetworkSpeciesDetail.extractFlavorText(language : String, version : String) : String {
    return flavorTextEntries.filter {
            Timber.i("extractFlavorTextAndName: ${it.toString()}")
            if(it.language.name == language && it.version.name == version) {
                Timber.i("flavor entry selected: ${it.flavorText}")}
            it.language.name == language && it.version.name == version
        }.map { it.flavorText.replace("\n", " ") }.joinToString(separator = "\n")
}

fun NetworkPokemonContainer.extractVersionList() : List<String> {

    return if(results.isNotEmpty()) {
            results.map {
                it!!.name
            }
        }
        else {
        listOf()
    }

}