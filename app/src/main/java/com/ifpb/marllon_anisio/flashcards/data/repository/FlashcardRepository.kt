package com.ifpb.marllon_anisio.flashcards.data.repository

import com.ifpb.marllon_anisio.flashcards.data.local.FlashcardDao
import com.ifpb.marllon_anisio.flashcards.data.local.toDomain
import com.ifpb.marllon_anisio.flashcards.data.local.toEntity
import com.ifpb.marllon_anisio.flashcards.domain.models.Deck
import com.ifpb.marllon_anisio.flashcards.domain.models.Flashcard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlashcardRepository(private val dao: FlashcardDao) {
    fun getDecks(): Flow<List<Deck>> = dao.getAllDecks().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getCardsForDeck(deckId: Int): List<Flashcard> = 
        dao.getCardsByDeck(deckId).map { it.toDomain() }

    suspend fun addDeck(deck: Deck) = dao.insertDeck(deck.toEntity())
    
    suspend fun addCard(card: Flashcard) = dao.insertFlashcard(card.toEntity())
}
