package com.dicoding.asclepius.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.models.History
import com.dicoding.asclepius.data.repo.HistoryRepository
import com.dicoding.asclepius.utils.Event
import kotlinx.coroutines.launch

class HistoryViewModel(private val historyRepository: HistoryRepository) : ViewModel() {
    val listHistories: LiveData<PagingData<History>> by lazy {
        historyRepository.getHistories().asLiveData().cachedIn(viewModelScope)
    }

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _undo = MutableLiveData<Event<History>>()
    val undo: LiveData<Event<History>> = _undo

    fun deleteHistory(habit: History) {
        viewModelScope.launch {
            historyRepository.deleteHistory(habit)
            _snackbarText.value = Event(R.string.history_deleted)
            _undo.value = Event(habit)
        }
    }

    fun insertHistory(history: History) {
        viewModelScope.launch {
            historyRepository.insertHistory(history)
        }
    }
}