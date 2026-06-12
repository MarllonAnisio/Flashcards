package com.ifpb.marllon_anisio.flashcards.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifpb.marllon_anisio.flashcards.data.repository.FlashcardRepository
import com.ifpb.marllon_anisio.flashcards.domain.models.Flashcard
import com.ifpb.marllon_anisio.flashcards.domain.models.QuizUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: FlashcardRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState = _uiState.asStateFlow()

    private var sessionCards: List<Flashcard> = emptyList()

    fun startSession(deckId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Using first() to get current snapshot from Flow
                val cards = repository.getCardsForDeckFlow(deckId).first().shuffled()
                sessionCards = cards
                if (cards.isNotEmpty()) {
                    _uiState.update { 
                        it.copy(
                            currentCard = cards[0],
                            currentIndex = 0,
                            totalCards = cards.size,
                            isAnswerRevealed = false,
                            correctAnswers = 0,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Este baralho não possui cards.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Erro ao iniciar sessão: ${e.message}") }
            }
        }
    }

    fun revealAnswer() {
        _uiState.update { it.copy(isAnswerRevealed = true) }
    }

    fun submitAnswer(correct: Boolean) {
        _uiState.update { state ->
            val newScore = if (correct) state.correctAnswers + 1 else state.correctAnswers
            val nextIndex = state.currentIndex + 1
            
            if (nextIndex < state.totalCards && nextIndex < sessionCards.size) {
                val nextCard = sessionCards[nextIndex]
                state.copy(
                    currentCard = nextCard,
                    currentIndex = nextIndex,
                    isAnswerRevealed = false,
                    correctAnswers = newScore
                )
            } else {
                // aqui finaliza a sessão
                state.copy(correctAnswers = newScore, currentIndex = nextIndex)
            }
        }
    }

    fun resetSession() {
        _uiState.value = QuizUiState()
        sessionCards = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        resetSession()
    }
}
