package com.ignacio.pokemonquizkotlin2.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import timber.log.Timber
import java.util.*

fun networkIsOk(context: Context) : Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true
}

const val PREFERENCE_FILE_NAME = "customPrefs.pref"
const val FRESH_TIMEOUT_IN_MINUTES = 43200 // ONE WEEK
const val LAST_DB_REFRESH = "lastDbRefresh"
const val LAST_PAGING_POKEMON_ID_KEY = "lastOfset"

fun dateIsFresh(minutes : Long) : Boolean {
    val nowMillis = Calendar.getInstance().timeInMillis
    return nowMillis/60/1000 - minutes <= FRESH_TIMEOUT_IN_MINUTES
}

fun writeLine() {
    Timber.i("=========================================================================================")
}

fun writeLineTest() {
    println("=========================================================================================")
}

fun Float.roundTo(n : Int) : Float {
    return "%.${n}f".format(this).toFloat()
}