package com.ifpb.marllon_anisio.flashcards.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifpb.marllon_anisio.flashcards.data.local.UserPreferencesRepository
import com.ifpb.marllon_anisio.flashcards.data.repository.FlashcardRepository
import com.ifpb.marllon_anisio.flashcards.domain.models.Deck
import com.ifpb.marllon_anisio.flashcards.domain.models.Flashcard
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeckViewModel(
    private val repository: FlashcardRepository,
    private val userPreferencesRepository: UserPreferencesRepository // Injetando o DataStore aqui
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Transforma o Flow do DataStore em um StateFlow para a UI consumir com segurança
    val appTheme: StateFlow<String> = userPreferencesRepository.appTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "SYSTEM"
        )

    val decks: StateFlow<List<Deck>> = repository.getDecks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Função para alternar de forma cíclica entre LIGHT -> DARK -> SYSTEM
    fun toggleTheme(currentTheme: String) {
        viewModelScope.launch {
            val nextTheme = when (currentTheme) {
                "LIGHT" -> "DARK"
                "DARK" -> "SYSTEM"
                else -> "LIGHT"
            }
            userPreferencesRepository.saveThemePreference(nextTheme)
        }
    }

    fun addDeck(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addDeck(Deck(name = name))
            if (result.isFailure) {
                _error.value = "Falha ao criar baralho: ${result.exceptionOrNull()?.message}"
            }
            _isLoading.value = false
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteDeck(deck)
            if (result.isFailure) {
                _error.value = "Falha ao excluir baralho: ${result.exceptionOrNull()?.message}"
            }
            _isLoading.value = false
        }
    }

    fun updateDeck(deck: Deck) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateDeck(deck)
            if (result.isFailure) {
                _error.value = "Falha ao atualizar baralho: ${result.exceptionOrNull()?.message}"
            }
            _isLoading.value = false
        }
    }

    fun seedDataIfEmpty() {
        viewModelScope.launch {
            val currentDecks = repository.getDecks().first()
            if (currentDecks.isEmpty()) {
                _isLoading.value = true
                try {
                    val kotlinResult = repository.addDeck(Deck(name = "Kotlin Fundamentals"))
                    if (kotlinResult.isSuccess) {
                        val decks = repository.getDecks().first()
                        val kotlinDeck = decks.find { it.name == "Kotlin Fundamentals" }
                        kotlinDeck?.let { deck ->
                            repository.addCard(Flashcard(deckId = deck.id, question = "O que é uma 'val'?", answer = "Uma variável de apenas leitura."))
                            repository.addCard(Flashcard(deckId = deck.id, question = "O que é 'null safety'?", answer = "Um recurso que evita NullPointerExceptions."))
                        }
                    }

                    repository.addDeck(Deck(name = "Jetpack Compose"))
                    val decksAfter = repository.getDecks().first()
                    val composeDeck = decksAfter.find { it.name == "Jetpack Compose" }
                    composeDeck?.let { deck ->
                        repository.addCard(Flashcard(deckId = deck.id, question = "O que é Composable?", answer = "Uma função que define a UI de forma declarativa."))
                    }
                } catch (e: Exception) {
                    _error.value = "Erro ao semear dados: ${e.message}"
                }
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}