package com.ifpb.marllon_anisio.flashcards.ui.viewmodels

import com.ifpb.marllon_anisio.flashcards.data.repository.FlashcardRepository
import com.ifpb.marllon_anisio.flashcards.domain.models.Flashcard
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private val repository: FlashcardRepository = mockk()
    private lateinit var viewModel: QuizViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = QuizViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startSession loads cards and updates state`() = runTest {
        val deckId = 1
        val mockCards = listOf(
            Flashcard(id = 1, deckId = deckId, question = "Q1", answer = "A1"),
            Flashcard(id = 2, deckId = deckId, question = "Q2", answer = "A2")
        )
        // Ensure the flow emits
        coEvery { repository.getCardsForDeckFlow(deckId) } returns flowOf(mockCards)

        viewModel.startSession(deckId)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.currentCard)
        assertEquals(2, state.totalCards)
        assertEquals(0, state.currentIndex)
    }

    @Test
    fun `submitAnswer updates index and score`() = runTest {
        val deckId = 1
        val mockCards = listOf(
            Flashcard(id = 1, deckId = deckId, question = "Q1", answer = "A1"),
            Flashcard(id = 2, deckId = deckId, question = "Q2", answer = "A2")
        )
        // Using flowOf with items ensures they are available
        coEvery { repository.getCardsForDeckFlow(deckId) } returns flowOf(mockCards)

        viewModel.startSession(deckId)
        advanceUntilIdle()
        
        // Ensure currentIndex is 0 before submit
        assertEquals(0, viewModel.uiState.value.currentIndex)

        viewModel.submitAnswer(true)
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(1, state.currentIndex)
        assertEquals(1, state.correctAnswers)
        assertEquals("Q2", state.currentCard?.question)
    }
}
