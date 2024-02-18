package com.dicoding.asclepius.data.remote

import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.remote.models.ArticlesResponse
import retrofit2.http.*

interface ApiService {
    @GET("top-headlines")
    suspend fun getArticles(
        @Query("q") query: String = CANCER_QUERY,
        @Query("category") category: String = HEALTH_CATEGORY,
        @Query("language") language: String = EN_LANGUAGE,
        @Query("apiKey") apiKey: String = BuildConfig.NEWSAPI_KEY,

    ): ArticlesResponse

    companion object {
        private const val CANCER_QUERY = "cancer"
        private const val HEALTH_CATEGORY = "health"
        private const val EN_LANGUAGE = "en"
    }
}
