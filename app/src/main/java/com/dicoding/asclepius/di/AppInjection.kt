package com.dicoding.asclepius.di

import androidx.room.Room
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.local.AsclepiusDatabase
import com.dicoding.asclepius.data.remote.ApiService
import com.dicoding.asclepius.data.repo.HistoryRepository
import com.dicoding.asclepius.data.repo.NewsRepository
import com.dicoding.asclepius.viewmodel.HistoryViewModel
import com.dicoding.asclepius.viewmodel.MainViewModel
import com.dicoding.asclepius.viewmodel.ResultViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val databaseModule = module {
    factory { get<AsclepiusDatabase>().historyDao() }
    single {
        Room.databaseBuilder(
            androidContext(),
            AsclepiusDatabase::class.java, "AsclepiusDB"
        ).fallbackToDestructiveMigration()
            .build()
    }
}

val networkModule = module {
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.NEWSAPI_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
            )
            .build()
        retrofit.create(ApiService::class.java)
    }
}

val repositoryModule = module {
    single { NewsRepository(get()) }
    single { HistoryRepository(get()) }
}

val viewModelModule = module {
    viewModel { ResultViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { MainViewModel(get()) }
}
