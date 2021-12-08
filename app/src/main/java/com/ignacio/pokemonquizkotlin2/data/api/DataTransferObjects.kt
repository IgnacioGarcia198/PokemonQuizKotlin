package com.ignacio.pokemonquizkotlin2.data.api

import com.ignacio.pokemonquizkotlin2.data.api.speciesdetail.FlavorTextEntry
import com.ignacio.pokemonquizkotlin2.data.api.speciesdetail.NetworkSpeciesDetail
import com.ignacio.pokemonquizkotlin2.db.DatabasePokemon
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
data class NetworkPokemonContainer(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<NameUrlPair?>
)

@Serializable
data class NameUrlPair(
    val name: String,
    val url: String
)


fun NetworkPokemonContainer.asDatabaseModel(
    offset: Int = 0,
    limit: Int = -1
): List<DatabasePokemon> {
    var i = offset + 1
    return results.map {
        DatabasePokemon(i++, it!!.name, usedAsQuestion = false)
    }
}

fun NetworkSpeciesDetail.extractAvailableVersions(language: String): List<String> {
    Timber.i(
        "Available versions are ${
            flavorTextEntries.filter { it.language.name == language }.map { it.version.name }
        }"
    )
    return flavorTextEntries.filter { it.language.name == language }.map { it.version.name }
}

fun NetworkSpeciesDetail.extractFlavorTextAndName(
    language: String,
    version: String
): Pair<String, String> {
    return Pair(
        flavorTextEntries.filter {
            it.language.name == language && it.version.name == version
        }.joinToString(separator = "\n") { cleanSpaces(it.flavorText) },
        names.find { it.language.name == language }!!.name
    )
}

fun NetworkSpeciesDetail.printAllVersionFlavors(language: String): Map<String, String> {
    //Timber.i(
    return flavorTextEntries.filter { it.language.name == language }
        .map {
            FlavorTextEntry(
                "\"" + cleanSpaces(it.flavorText) + "\"",
                NameUrlPair(language, it.language.url),
                NameUrlPair("\"" + it.version.name + "\"", it.version.url)
            )
        }
        .map { it.version.name to it.flavorText }.toMap()
    //)
}

fun NetworkSpeciesDetail.extractName(language: String): String {
    //Timber.i(
    return names.find { it.language.name == language }!!.name
}

fun NetworkSpeciesDetail.extractAllVersionsAndFlavors(language: String): Map<String, String> {
    return flavorTextEntries.asSequence().filter { it.language.name == language }
        .map { it.version.name to cleanSpaces(it.flavorText) }.toMap()
}

private fun cleanSpaces(s: String): String {
    return s.replace(
        "[\\n\\t\\u0012\\u0015\\x0c]".toRegex(),
        " "
    )//.replaceAll("[\\u0000-\\u0036 ]"," ");
}

fun NetworkSpeciesDetail.extractFlavorText(language: String, version: String): String {
    return flavorTextEntries.filter {
        it.language.name == language && it.version.name == version
    }.joinToString(separator = "\n") { cleanSpaces(it.flavorText) }
}
