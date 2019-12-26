package com.ignacio.pokemonquizkotlin2.data.api.speciesdetail

import com.ignacio.pokemonquizkotlin2.data.api.NameUrlPair
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkSpeciesDetail(
    @Json(name = "base_happiness")
    val baseHappiness: Int = 0,
    @Json(name = "capture_rate")
    val captureRate: Int =0,
    @Json(name = "color")
    val color: NameUrlPair?,
    @Json(name = "egg_groups")
    val eggGroups: List<NameUrlPair?>?,
    @Json(name = "evolution_chain")
    val evolutionChain: EvolutionChain?,
    @Json(name = "evolves_from_species")
    val evolvesFromSpecies: NameUrlPair?,
    @Json(name = "flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntry>,
    @Json(name = "form_descriptions")
    val formDescriptions: List<Any>?,
    @Json(name = "forms_switchable")
    val formsSwitchable: Boolean = false,
    @Json(name = "gender_rate")
    val genderRate: Int = 0,
    @Json(name = "genera")
    val genera: List<Genera?>?,
    @Json(name = "generation")
    val generation: NameUrlPair?,
    @Json(name = "growth_rate")
    val growthRate: NameUrlPair?,
    @Json(name = "habitat")
    val habitat: NameUrlPair?,
    @Json(name = "has_gender_differences")
    val hasGenderDifferences: Boolean?,
    @Json(name = "hatch_counter")
    val hatchCounter: Int = 0,
    @Json(name = "id")
    val id: Int,
    @Json(name = "is_baby")
    val isBaby: Boolean= false,
    @Json(name = "name")
    val name: String,
    @Json(name = "names")
    val names: List<Name>,
    @Json(name = "order")
    val order: Int = 0,
    @Json(name = "pal_park_encounters")
    val palParkEncounters: List<PalParkEncounter?>?,
    @Json(name = "pokedex_numbers")
    val pokedexNumbers: List<PokedexNumber?>?,
    @Json(name = "shape")
    val shape: NameUrlPair?,
    @Json(name = "varieties")
    val varieties: List<Variety?>?
)

@JsonClass(generateAdapter = true)
data class EvolutionChain(
    @Json(name = "url")
    val url: String
)

@JsonClass(generateAdapter = true)
data class FlavorTextEntry(
    @Json(name = "flavor_text")
    val flavorText: String,
    @Json(name = "language")
    val language: NameUrlPair,
    @Json(name = "version")
    val version: NameUrlPair
)

@JsonClass(generateAdapter = true)
data class Genera(
    @Json(name = "genus")
    val genus: String,
    @Json(name = "language")
    val language: NameUrlPair
)

@JsonClass(generateAdapter = true)
data class Name(
    @Json(name = "language")
    val language: NameUrlPair,
    @Json(name = "name")
    val name: String
)

@JsonClass(generateAdapter = true)
data class PalParkEncounter(
    @Json(name = "area")
    val area: NameUrlPair,
    @Json(name = "base_score")
    val baseScore: Int,
    @Json(name = "rate")
    val rate: Int
)

@JsonClass(generateAdapter = true)
data class PokedexNumber(
    @Json(name = "entry_number")
    val entryNumber: Int,
    @Json(name = "pokedex")
    val pokedex: NameUrlPair
)

@JsonClass(generateAdapter = true)
data class Variety(
    @Json(name = "is_default")
    val isDefault: Boolean,
    @Json(name = "pokemon")
    val pokemon: NameUrlPair
)


