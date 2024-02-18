package com.dicoding.asclepius.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.local.models.History
import com.dicoding.asclepius.data.repo.HistoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val historyRepository: HistoryRepository) : ViewModel() {
    fun addHistory(history: History) {
        viewModelScope.launch {
            historyRepository.insertHistory(history)
        }
    }
}