package com.dicoding.asclepius.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.remote.models.ArticlesItem
import com.dicoding.asclepius.data.repo.NewsRepository
import com.dicoding.asclepius.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listArticle = MutableLiveData<List<ArticlesItem?>?>(arrayListOf())
    val listArticle: LiveData<List<ArticlesItem?>?> = _listArticle

    private val _textMessage = MutableLiveData("")
    val textMessage: LiveData<String> = _textMessage


    init {
        getArticles()
    }

    fun getArticles() {
        viewModelScope.launch(Dispatchers.IO) {
            newsRepository.getArticles().collect { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Loading -> {
                            _textMessage.value = ""
                            _isLoading.value = true
                        }

                        is Result.Success -> {
                            _isLoading.value = false
                            _listArticle.value = result.data.articles
                        }

                        is Result.Error -> {
                            _isLoading.value = false
                            _textMessage.value = result.error
                        }
                    }
                }
            }
        }
    }
}
