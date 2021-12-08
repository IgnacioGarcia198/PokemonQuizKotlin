package com.ignacio.pokemonquizkotlin2.data.api.speciesdetail

import com.ignacio.pokemonquizkotlin2.data.api.NameUrlPair
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSpeciesDetail(
    @SerialName("base_happiness")
    val baseHappiness: Int = 0,
    @SerialName("capture_rate")
    val captureRate: Int =0,
    @SerialName("color")
    val color: NameUrlPair?,
    @SerialName("egg_groups")
    val eggGroups: List<NameUrlPair?>?,
    @SerialName("evolution_chain")
    val evolutionChain: EvolutionChain?,
    @SerialName("evolves_from_species")
    val evolvesFromSpecies: NameUrlPair?,
    @SerialName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntry>,
    @SerialName("form_descriptions")
    val formDescriptions: List<@Contextual Any>?,
    @SerialName("forms_switchable")
    val formsSwitchable: Boolean = false,
    @SerialName("gender_rate")
    val genderRate: Int = 0,
    @SerialName("genera")
    val genera: List<Genera?>?,
    @SerialName("generation")
    val generation: NameUrlPair?,
    @SerialName("growth_rate")
    val growthRate: NameUrlPair?,
    @SerialName("habitat")
    val habitat: NameUrlPair?,
    @SerialName("has_gender_differences")
    val hasGenderDifferences: Boolean?,
    @SerialName("hatch_counter")
    val hatchCounter: Int = 0,
    @SerialName("id")
    val id: Int,
    @SerialName("is_baby")
    val isBaby: Boolean= false,
    @SerialName("name")
    val name: String,
    @SerialName("names")
    val names: List<Name>,
    @SerialName("order")
    val order: Int = 0,
    @SerialName("pal_park_encounters")
    val palParkEncounters: List<PalParkEncounter?>?,
    @SerialName("pokedex_numbers")
    val pokedexNumbers: List<PokedexNumber?>?,
    @SerialName("shape")
    val shape: NameUrlPair?,
    @SerialName("varieties")
    val varieties: List<Variety?>?
)

@Serializable
data class EvolutionChain(
    val url: String
)

@Serializable
data class FlavorTextEntry(
    @SerialName("flavor_text")
    val flavorText: String,
    @SerialName("language")
    val language: NameUrlPair,
    @SerialName("version")
    val version: NameUrlPair
)

@Serializable
data class Genera(
    val genus: String,
    val language: NameUrlPair
)

@Serializable
data class Name(
    val language: NameUrlPair,
    val name: String
)

@Serializable
data class PalParkEncounter(
    val area: NameUrlPair,
    @SerialName("base_score")
    val baseScore: Int,
    val rate: Int
)

@Serializable
data class PokedexNumber(
    @SerialName("entry_number")
    val entryNumber: Int,
    val pokedex: NameUrlPair
)

@Serializable
data class Variety(
    @SerialName("is_default")
    val isDefault: Boolean,
    val pokemon: NameUrlPair
)


