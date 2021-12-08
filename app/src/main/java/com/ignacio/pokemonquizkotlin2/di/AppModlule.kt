package com.ignacio.pokemonquizkotlin2.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.ignacio.pokemonquizkotlin2.BuildConfig
import com.ignacio.pokemonquizkotlin2.data.api.PokemonNetwork
import com.ignacio.pokemonquizkotlin2.data.api.PokemonService
import com.ignacio.pokemonquizkotlin2.db.GameRecordDao
import com.ignacio.pokemonquizkotlin2.db.MyDatabase
import com.ignacio.pokemonquizkotlin2.db.PokemonDao
import com.ignacio.pokemonquizkotlin2.utils.PREFERENCE_FILE_NAME
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    private val json = Json { ignoreUnknownKeys = true }

    @Singleton
    @Provides
    fun providePokemonService(): PokemonService {
        val level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = level
        // Configure retrofit to parse JSON and use coroutines
        val okHttp = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/")
            .client(okHttp)
            .addConverterFactory(json.asConverterFactory(contentType))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        return retrofit.create(PokemonService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): MyDatabase {
        return Room.databaseBuilder(
            app,
            MyDatabase::class.java, "pokemondb.db"
        ).build()
    }

    @Singleton
    @Provides
    fun providePokemonDao(db: MyDatabase): PokemonDao {
        return db.pokemonDao
    }

    @Singleton
    @Provides
    fun provideGameRecordDao(db: MyDatabase): GameRecordDao {
        return db.gameRecordDao
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return app.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
    }
}
