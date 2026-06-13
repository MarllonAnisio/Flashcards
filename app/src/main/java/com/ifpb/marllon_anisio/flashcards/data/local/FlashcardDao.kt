package com.ifpb.marllon_anisio.flashcards.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Explicando um pouco a anotation @Dao, essa anotação faz basicamente o aviso,
 * ele anuncia ao Room que é pra ele gerar o necessario para as funçoes ele funciona igual o repository do Spring basicamente
 * */
@Dao
interface FlashcardDao {
    @Query("SELECT * FROM decks")
    fun getAllDecks(): Flow<List<DeckEntity>>

    /**
     * mais uma vez repassando, esse Flow nesse contexto, ele é um fluxo continuo, igual uma torneira msms tlgd?  ele vai fazer o Live Update
     * toda vez que mudar algo em deck ele já repassa tudo atualizado igual bomba patch
     * */
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId")
    fun getCardsByDeckFlow(deckId: Int): Flow<List<FlashcardEntity>>

    // Retorna apenas os cartões que precisam ser revisados no momento (nextReviewDate <= now)
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND nextReviewDate <= :now ORDER BY nextReviewDate ASC")
    suspend fun getDueCardsForDeck(deckId: Int, now: Long): List<FlashcardEntity>

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId = :deckId")
    suspend fun getCardCountByDeck(deckId: Int): Int

    /**
     * aqui tenho que falar um pouco, esse "onConflict = OnConflictStrategy.REPLACE:" é basicamente, se o deck ja existir, ele vai substituir por esse deck,
     *
     * ele é oq chamaos de Upsert(update + insert) curto muito não sinceramente
     *
     * o suspend aqui, obviamente é pq diferente quando usamos o flow, esse daqui é tiro curto o famoso one-shot, atualizou é isso vlw
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity): Long

    @Update
    suspend fun updateDeck(deck: DeckEntity)

    @Delete
    suspend fun deleteDeck(deck: DeckEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity): Long

    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)

    @Delete
    suspend fun deleteFlashcard(flashcard: FlashcardEntity)

    @Query("SELECT * FROM flashcards WHERE id = :cardId")
    suspend fun getCardById(cardId: Int): FlashcardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviewHistory(history: ReviewHistoryEntity)

    @Query("SELECT * FROM review_history WHERE reviewDate >= :startOfDay AND reviewDate <= :endOfDay")
    suspend fun getReviewsBetween(startOfDay: Long, endOfDay: Long): List<ReviewHistoryEntity>

    @Query("SELECT reviewDate FROM review_history ORDER BY reviewDate DESC")
    suspend fun getAllReviewDates(): List<Long>

    @Query("SELECT COUNT(*) FROM review_history")
    suspend fun getTotalReviewsCount(): Int

    @Query("SELECT COUNT(*) FROM review_history WHERE isCorrect = 1")
    suspend fun getCorrectReviewsCount(): Int
}
