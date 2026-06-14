package com.ifpb.marllon_anisio.flashcards.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifpb.marllon_anisio.flashcards.data.repository.FlashcardRepository
import com.ifpb.marllon_anisio.flashcards.domain.models.DailyStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatsViewModel(private val repository: FlashcardRepository) : ViewModel() {
    private val _stats = MutableStateFlow(DailyStats())
    val stats = _stats.asStateFlow()

    init {
        refreshStats()
    }

    fun refreshStats() {
        viewModelScope.launch {
            _stats.value = repository.getDailyStats()
        }
    }
}
