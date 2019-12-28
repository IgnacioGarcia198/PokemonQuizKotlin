package com.ignacio.pokemonquizkotlin2.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.ignacio.pokemonquizkotlin2.BuildConfig
import com.ignacio.pokemonquizkotlin2.data.api.PokemonService
import com.ignacio.pokemonquizkotlin2.db.GameRecordDao
import com.ignacio.pokemonquizkotlin2.db.MyDatabase
import com.ignacio.pokemonquizkotlin2.db.PokemonDao
import com.ignacio.pokemonquizkotlin2.ui.gamerecords.GameRecordsAdapter
import com.ignacio.pokemonquizkotlin2.utils.DispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.PREFERENCE_FILE_NAME
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
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
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/")
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        return retrofit.create(PokemonService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): MyDatabase {
        return Room.databaseBuilder(app,
            MyDatabase::class.java, "pokemondb.db").build()
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
    fun provideSharedPreferences(app: Application) : SharedPreferences {
        return app.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
    }



}