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

    /**
     * Rapaz esse flow é foda, ele é do kotlin coroutines https://developer.android.com/kotlin/flow?hl=pt-br
     * basicamente ele pode cuspir varios tipos de dados,
     * ou seja, ele é um fluxo de dados, e o mais interessante é que ele é assíncrono, ou seja, ele não bloqueia a thread principal,
     * ele é muito útil para lidar com dados que podem mudar ao longo do tempo, como os dados do banco de dados, ou seja,
     * quando tiver uma mudança no banco de dados, ele vai emitir um novo valor para quem estiver coletando esse flow.
     * */
    fun getDecks(): Flow<List<Deck>> = dao.getAllDecks().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getCardsForDeckFlow(deckId: Int): Flow<List<Flashcard>> = 
        dao.getCardsByDeckFlow(deckId).map { entities ->
            entities.map { it.toDomain() }
        }

    // Retorna a lista de cartões vencidos para o SRS
    suspend fun getDueCardsForDeck(deckId: Int, now: Long): List<Flashcard> =
        dao.getDueCardsForDeck(deckId, now).map { it.toDomain() }

    /**
     * Salientando o uso do suspend, ele é usado basicamente para dizer que a função ṕode demorar,
     * fazendo a Thread principal não travar(que é oq o usuario está vendo), quando estiver pronto a função vai ser liberada.
     *
     * mas marllon, porque o try-catch!? simples, estamos usando banco, se ele travar lasca tudo, com isso não vai ter crash,
     * é bom para a robustez tamembem é claro, caso a função "exploda" no pior dos casos, vai fazer o log do erro e em seguida retornar 0.
     * */
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

    /**
     * não trabalhei malha de exceptions porem caso eu mudasse algum dia eu so precisaria mudar aqui porque metodos que usam o banco
     * como add,update e delete usam esse molde
     * */
    private suspend fun safeCall(call: suspend () -> Any): Result<Unit> = try {
        call()
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("Repository", "Database error", e)
        Result.failure(e)
    }

    suspend fun logCardReview(cardId: Int, isCorrect: Boolean): Result<Unit> = safeCall {
        dao.insertReviewHistory(
            com.ifpb.marllon_anisio.flashcards.data.local.ReviewHistoryEntity(
                cardId = cardId,
                isCorrect = isCorrect
            )
        )
    }

    suspend fun getDailyStats(): com.ifpb.marllon_anisio.flashcards.domain.models.DailyStats {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val endOfDay = calendar.timeInMillis - 1
        
        val todayReviews = dao.getReviewsBetween(startOfDay, endOfDay)
        val cardsReviewedToday = todayReviews.size
        
        val totalReviews = try { dao.getTotalReviewsCount() } catch (e: Exception) { 0 }
        val correctReviews = try { dao.getCorrectReviewsCount() } catch (e: Exception) { 0 }
        val retentionRate = if (totalReviews > 0) (correctReviews.toFloat() / totalReviews.toFloat()) * 100f else 0f
        
        val allDates = try { dao.getAllReviewDates() } catch (e: Exception) { emptyList() }
        val currentStreak = calculateStreak(allDates)
        
        return com.ifpb.marllon_anisio.flashcards.domain.models.DailyStats(
            cardsReviewedToday = cardsReviewedToday,
            currentStreak = currentStreak,
            retentionRate = retentionRate
        )
    }

    private fun calculateStreak(dates: List<Long>): Int {
        if (dates.isEmpty()) return 0
        
        val uniqueDays = dates.map { 
            val cal = java.util.Calendar.getInstance()
            cal.timeInMillis = it
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.distinct().sortedDescending()
        
        var streak = 0
        val todayCal = java.util.Calendar.getInstance()
        todayCal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        todayCal.set(java.util.Calendar.MINUTE, 0)
        todayCal.set(java.util.Calendar.SECOND, 0)
        todayCal.set(java.util.Calendar.MILLISECOND, 0)
        val today = todayCal.timeInMillis
        
        val yesterdayCal = java.util.Calendar.getInstance()
        yesterdayCal.timeInMillis = today
        yesterdayCal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        val yesterday = yesterdayCal.timeInMillis
        
        if (!uniqueDays.contains(today) && !uniqueDays.contains(yesterday)) {
            return 0
        }
        
        var expectedDay = if (uniqueDays.contains(today)) today else yesterday
        for (day in uniqueDays) {
            if (day == expectedDay) {
                streak++
                val nextCal = java.util.Calendar.getInstance()
                nextCal.timeInMillis = expectedDay
                nextCal.add(java.util.Calendar.DAY_OF_YEAR, -1)
                expectedDay = nextCal.timeInMillis
            } else if (day < expectedDay) {
                break
            }
        }
        return streak
    }
}
