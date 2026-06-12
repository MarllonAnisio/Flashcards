package com.ifpb.marllon_anisio.flashcards.data.local

import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ifpb.marllon_anisio.flashcards.domain.models.Deck
import com.ifpb.marllon_anisio.flashcards.domain.models.Flashcard
/**
 * temos que prestar atenção na ordem dos atributos, pois o Room vai usar o nome dos atributos
 * para criar as colunas da tabela.
 *
 * a anotation @entity igual no spring data, faz com que o Room crie a tabela com o nome especificado,
 * e os atributos da classe vão virar as colunas da tabela, o @PrimaryKey é a chave primária da tabela,
 * e o autoGenerate = true faz com que o Room gere um id automaticamente para cada nova entrada.
 * */
@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
/**
 * um flashCard so existe dentro de um deck certo?, então, aqui eu usei o flashcard como uma tabela filha
 * se eu apagar o deck, ele vai apagar tudo que tem relacionamento com ele, nesse caso, os cards
 * perceba o "onDelete = ForeignKey.CASCADE" que faz com que o deck seja apagado, assim como os cards relacionados a ele,
 * ou seja, é uma cascata de deleção, isso é muito útil para manter a integridade dos dados,
 *
 * */
@Entity(
    tableName = "flashcards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["deckId"])]
)
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckId: Int,
    val question: String,
    val answer: String,
    // Campos persistidos no Room referentes ao estado do aprendizado (SRS)
    val nextReviewDate: Long = System.currentTimeMillis(),
    val interval: Int = 0,
    val easeFactor: Float = 2.5f,
    val repetitionCount: Int = 0
)

fun DeckEntity.toDomain() = Deck(id, name)
fun FlashcardEntity.toDomain() = Flashcard(id, deckId, question, answer)

fun Deck.toEntity() = DeckEntity(id, name)
fun Flashcard.toEntity() = FlashcardEntity(id, deckId, question, answer)
