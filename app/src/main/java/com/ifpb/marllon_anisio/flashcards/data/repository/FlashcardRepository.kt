package com.ifpb.marllon_anisio.flashcards.data.repository

import com.ifpb.marllon_anisio.flashcards.data.local.FlashcardDao
import com.ifpb.marllon_anisio.flashcards.data.local.toDomain
import com.ifpb.marllon_anisio.flashcards.data.local.toEntity
import com.ifpb.marllon_anisio.flashcards.domain.models.Deck
import com.ifpb.marllon_anisio.flashcards.domain.models.Flashcard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.util.Log

class FlashcardRepository(private val dao: FlashcardDao) {
    
    fun getDecks(): Flow<List<Deck>> = dao.getAllDecks().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getCardsForDeckFlow(deckId: Int): Flow<List<Flashcard>> = 
        dao.getCardsByDeckFlow(deckId).map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun getCardCount(deckId: Int): Int = try {
        dao.getCardCountByDeck(deckId)
    } catch (e: Exception) {
        Log.e("Repository", "Error getting card count", e)
        0
    }

    suspend fun addDeck(deck: Deck): Result<Unit> = safeCall {
        dao.insertDeck(deck.toEntity())
    }
    
    suspend fun updateDeck(deck: Deck): Result<Unit> = safeCall {
        dao.updateDeck(deck.toEntity())
    }

    suspend fun deleteDeck(deck: Deck): Result<Unit> = safeCall {
        dao.deleteDeck(deck.toEntity())
    }

    suspend fun addCard(card: Flashcard): Result<Unit> = safeCall {
        dao.insertFlashcard(card.toEntity())
    }

    suspend fun updateCard(card: Flashcard): Result<Unit> = safeCall {
        dao.updateFlashcard(card.toEntity())
    }

    suspend fun deleteCard(card: Flashcard): Result<Unit> = safeCall {
        dao.deleteFlashcard(card.toEntity())
    }

    private suspend fun safeCall(call: suspend () -> Any): Result<Unit> = try {
        call()
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("Repository", "Database error", e)
        Result.failure(e)
    }
}
