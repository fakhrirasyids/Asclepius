package com.dicoding.asclepius.data.repo

import com.dicoding.asclepius.data.remote.ApiService
import com.dicoding.asclepius.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class NewsRepository(private val apiService: ApiService) {
    fun getArticles() = flow {
        emit(Result.Loading)
        try {
            val articlesResponse = apiService.getArticles()
            emit(Result.Success(articlesResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)
}