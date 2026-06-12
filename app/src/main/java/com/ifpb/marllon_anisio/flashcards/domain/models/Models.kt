package com.ifpb.marllon_anisio.flashcards.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Deck(
    val id: Int = 0,
    val name: String
) {
    init {
        require(name.isNotBlank()) { "O nome do baralho não pode estar vazio" }
    }
}

@Serializable
data class Flashcard(
    val id: Int = 0,
    val deckId: Int,
    val question: String,
    val answer: String,
    // Campos adicionados para o Sistema de Repetição Espaçada (SRS)
    // nextReviewDate guarda o timestamp (em milissegundos) de quando o cartão deve ser revisado novamente.
    val nextReviewDate: Long = System.currentTimeMillis(),
    // interval guarda o intervalo atual em dias até a próxima revisão. Começa em 0.
    val interval: Int = 0,
    // easeFactor é o multiplicador de facilidade (padrão 2.5 no SM-2). Se errar cai, se acertar sobe.
    val easeFactor: Float = 2.5f,
    // repetitionCount guarda a quantidade de vezes seguidas que o usuário acertou este cartão.
    val repetitionCount: Int = 0
) {
    init {
        require(question.isNotBlank()) { "A pergunta não pode estar vazia" }
        require(answer.isNotBlank()) { "A resposta não pode estar vazia" }
        require(deckId != 0) { "O card deve estar associado a um baralho" }
    }
}

@Serializable
data class QuizUiState(
    val currentCard: Flashcard? = null,
    val isAnswerRevealed: Boolean = false,
    val currentIndex: Int = 0,
    val totalCards: Int = 0,
    val correctAnswers: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
