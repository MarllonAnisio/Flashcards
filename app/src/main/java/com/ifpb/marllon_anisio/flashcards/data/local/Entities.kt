package com.ifpb.marllon_anisio.flashcards.data.local

import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ifpb.marllon_anisio.flashcards.domain.models.Deck
import com.ifpb.marllon_anisio.flashcards.domain.models.Flashcard

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

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
    val answer: String
)

fun DeckEntity.toDomain() = Deck(id, name)
fun FlashcardEntity.toDomain() = Flashcard(id, deckId, question, answer)

fun Deck.toEntity() = DeckEntity(id, name)
fun Flashcard.toEntity() = FlashcardEntity(id, deckId, question, answer)
