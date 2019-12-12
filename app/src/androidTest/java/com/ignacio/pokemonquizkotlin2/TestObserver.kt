package com.ignacio.pokemonquizkotlin2

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.test(): TestObserver<T> {
  return TestObserver.test(this)
}
