package com.ignacio.pokemonquizkotlin2.testutils

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.test(): TestObserver<T> {
  return TestObserver.test(this)
}
