package com.dicoding.asclepius.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.asclepius.data.local.HistoryDao
import com.dicoding.asclepius.data.local.models.History
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    fun getHistories(): Flow<PagingData<History>> {
        val config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = PLACEHOLDERS,
            initialLoadSize = PAGE_SIZE
        )
        return Pager(config = config, pagingSourceFactory = { historyDao.getHistories() }).flow
    }

    suspend fun insertHistory(history: History): Long {
        return historyDao.insertHistory(history)
    }

    suspend fun deleteHistory(history: History) {
        historyDao.deleteHistory(history)
    }

    companion object {
        const val PAGE_SIZE = 30
        const val PLACEHOLDERS = true
    }
}