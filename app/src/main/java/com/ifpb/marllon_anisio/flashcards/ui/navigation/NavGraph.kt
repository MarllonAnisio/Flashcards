package com.ifpb.marllon_anisio.flashcards.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ifpb.marllon_anisio.flashcards.ui.screens.*
import com.ifpb.marllon_anisio.flashcards.ui.viewmodels.CardManagementViewModel
import com.ifpb.marllon_anisio.flashcards.ui.viewmodels.DeckViewModel
import com.ifpb.marllon_anisio.flashcards.ui.viewmodels.QuizViewModel
import kotlinx.serialization.Serializable

@Serializable object DeckSelection
@Serializable data class QuizSession(val deckId: Int)
@Serializable data class Results(val score: Int, val total: Int)
@Serializable data class ManageCards(val deckId: Int)

@Composable
fun FlashcardNavGraph(
    navController: NavHostController,
    deckViewModel: DeckViewModel,
    quizViewModel: QuizViewModel,
    cardManagementViewModel: CardManagementViewModel
) {
    NavHost(navController = navController, startDestination = DeckSelection) {
        composable<DeckSelection> {
            val decks by deckViewModel.decks.collectAsState()
            val isLoading by deckViewModel.isLoading.collectAsState()
            val error by deckViewModel.error.collectAsState()

            DeckSelectionScreen(
                decks = decks,
                isLoading = isLoading,
                error = error,
                onDeckSelected = { deckId ->
                    quizViewModel.startSession(deckId)
                    navController.navigate(QuizSession(deckId))
                },
                onManageDeck = { deckId ->
                    navController.navigate(ManageCards(deckId))
                },
                onAddDeck = { name ->
                    deckViewModel.addDeck(name)
                },
                onDeleteDeck = { deck ->
                    deckViewModel.deleteDeck(deck)
                },
                onDismissError = { deckViewModel.clearError() }
            )
        }

        composable<ManageCards> { backStackEntry ->
            val data: ManageCards = backStackEntry.toRoute()
            val cards by cardManagementViewModel.cards.collectAsState()
            val isLoading by cardManagementViewModel.isLoading.collectAsState()
            val error by cardManagementViewModel.error.collectAsState()
            
            LaunchedEffect(data.deckId) {
                cardManagementViewModel.loadCards(data.deckId)
            }

            ManageCardsScreen(
                cards = cards,
                isLoading = isLoading,
                error = error,
                onAddCard = { q, a -> cardManagementViewModel.addCard(data.deckId, q, a) },
                onDeleteCard = { card -> cardManagementViewModel.deleteCard(card) },
                onBack = { navController.popBackStack() },
                onDismissError = { cardManagementViewModel.clearError() }
            )
        }

        composable<QuizSession> {
            val state by quizViewModel.uiState.collectAsState()
            
            LaunchedEffect(state.currentIndex, state.totalCards) {
                if (state.totalCards > 0 && state.currentIndex >= state.totalCards) {
                    navController.navigate(Results(state.correctAnswers, state.totalCards)) {
                        popUpTo(DeckSelection) { inclusive = false }
                    }
                }
            }

            QuizSessionScreen(
                state = state,
                onRevealClick = { quizViewModel.revealAnswer() },
                onAnswerClick = { correct -> quizViewModel.submitAnswer(correct) }
            )
        }

        composable<Results> { backStackEntry ->
            val results: Results = backStackEntry.toRoute()
            ResultsScreen(
                score = results.score,
                totalCards = results.total,
                onRestart = {
                    quizViewModel.resetSession()
                    navController.navigate(DeckSelection) {
                        popUpTo(DeckSelection) { inclusive = true }
                    }
                }
            )
        }
    }
}
