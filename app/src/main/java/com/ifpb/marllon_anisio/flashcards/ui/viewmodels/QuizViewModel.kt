package com.ifpb.marllon_anisio.flashcards.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifpb.marllon_anisio.flashcards.data.repository.FlashcardRepository
import com.ifpb.marllon_anisio.flashcards.domain.models.Flashcard
import com.ifpb.marllon_anisio.flashcards.domain.models.QuizUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class QuizViewModel(private val repository: FlashcardRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState = _uiState.asStateFlow()

    private var sessionCards: List<Flashcard> = emptyList()

    fun startSession(deckId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Busca no banco apenas os cartões que estão "vencidos" (nextReviewDate <= now)
                val now = System.currentTimeMillis()
                val cards = repository.getDueCardsForDeck(deckId, now).shuffled()
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
                    // Feedback amigável para o usuário que já estudou tudo
                    _uiState.update { it.copy(isLoading = false, error = "Você já revisou todos os cartões vencidos deste baralho hoje! 🎉") }
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
        val currentState = _uiState.value
        val currentCard = currentState.currentCard ?: return

        viewModelScope.launch {
            // mudança para a qualidade de resposta do algoritmo SM-2 passando a atuar mais igual o anki
            val quality = if (correct) 4 else 0
            
            // Calcula as novas métricas do SRS (novo intervalo, repetição e próxima data)
            val updatedCard = calculateNextReview(currentCard, quality)
            
            // Salva a nova data de revisão no banco de dados para a próxima vez
            repository.updateCard(updatedCard)
            
            // Registra o hit da revisão para as estatísticas diárias
            repository.logCardReview(currentCard.id, correct)

            // Avança para a próxima carta
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
    }

    /**
     * Algoritmo SuperMemo-2 (SM-2) modificado
     * Calcula o próximo momento de revisão com base na qualidade da resposta
     */
    private fun calculateNextReview(card: Flashcard, quality: Int): Flashcard {
        var newInterval = card.interval
        var newRepetition = card.repetitionCount
        var newEaseFactor = card.easeFactor

        if (quality >= 3) { // Resposta Correta (Acertei)
            if (newRepetition == 0) {
                newInterval = 1 // Primeira revisão: volta amanhã
            } else if (newRepetition == 1) {
                newInterval = 6 // Segunda revisão: volta daqui a 6 dias
            } else {
                // Revisões subsequentes: cresce usando o fator de facilidade
                newInterval = (newInterval * newEaseFactor).roundToInt()
            }
            newRepetition++
        } else { // Resposta Incorreta (Errei)
            newRepetition = 0 // Reseta as "ofensivas" do cartão
            newInterval = 1 // Volta a ser cobrado amanhã
        }

        // Ajusta a "facilidade". Se acertou fácil, o card demora ainda mais a aparecer. Se errou, a penalidade o faz aparecer com mais frequência.
        newEaseFactor += (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
        if (newEaseFactor < 1.3f) newEaseFactor = 1.3f // Limite mínimo do SM-2

        // Calcula a próxima data de revisão (Timestamp atual + intervalo em dias)
        val oneDayInMillis = 24 * 60 * 60 * 1000L
        val newReviewDate = System.currentTimeMillis() + (newInterval * oneDayInMillis)

        return card.copy(
            interval = newInterval,
            repetitionCount = newRepetition,
            easeFactor = newEaseFactor,
            nextReviewDate = newReviewDate
        )
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
