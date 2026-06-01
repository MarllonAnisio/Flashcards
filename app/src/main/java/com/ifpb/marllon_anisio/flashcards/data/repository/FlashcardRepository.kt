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
}
