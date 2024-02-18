package com.dicoding.asclepius.data.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.asclepius.data.local.models.History

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history")
    fun getHistories(): PagingSource<Int, History>

    @Query("SELECT * FROM history WHERE id = :historyId")
    fun getHistoryById(historyId: Int): LiveData<History>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistory(history: History): Long

    @Delete
    suspend fun deleteHistory(history: History)
}
