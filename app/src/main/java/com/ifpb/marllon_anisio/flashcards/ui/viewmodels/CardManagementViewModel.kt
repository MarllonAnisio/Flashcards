package com.ifpb.marllon_anisio.flashcards.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifpb.marllon_anisio.flashcards.data.repository.FlashcardRepository
import com.ifpb.marllon_anisio.flashcards.domain.models.Flashcard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CardManagementViewModel(private val repository: FlashcardRepository) : ViewModel() {
    private val _selectedDeckId = MutableStateFlow(0)
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val cards: StateFlow<List<Flashcard>> = _selectedDeckId
        .flatMapLatest { deckId ->
            if (deckId == 0) flowOf(emptyList())
            else repository.getCardsForDeckFlow(deckId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun loadCards(deckId: Int) {
        _selectedDeckId.value = deckId
    }

    fun addCard(deckId: Int, question: String, answer: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addCard(Flashcard(deckId = deckId, question = question, answer = answer))
            if (result.isFailure) {
                _error.value = "Falha ao adicionar card: ${result.exceptionOrNull()?.message}"
            }
            _isLoading.value = false
        }
    }

    fun deleteCard(card: Flashcard) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteCard(card)
            if (result.isFailure) {
                _error.value = "Falha ao excluir card: ${result.exceptionOrNull()?.message}"
            }
            _isLoading.value = false
        }
    }

    fun updateCard(card: Flashcard) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateCard(card)
            if (result.isFailure) {
                _error.value = "Falha ao atualizar card: ${result.exceptionOrNull()?.message}"
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
