package com.ifpb.marllon_anisio.flashcards.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ifpb.marllon_anisio.flashcards.domain.models.Flashcard
import com.ifpb.marllon_anisio.flashcards.domain.models.QuizUiState
import com.ifpb.marllon_anisio.flashcards.ui.theme.FlashcardsTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuizSessionScreen(
    state: QuizUiState,
    onRevealClick: () -> Unit,
    onAnswerClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                val progress = if (state.totalCards > 0) 
                    (state.currentIndex + 1).toFloat() / state.totalCards 
                    else 0f
                
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "Cartão ${state.currentIndex + 1} de ${state.totalCards}",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    state.error, 
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            } else if (state.currentCard != null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            ).padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(
                                targetState = state.isAnswerRevealed,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "CardContentAnimation"
                            ) { revealed ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (revealed) "Resposta" else "Pergunta",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (revealed) state.currentCard.answer else state.currentCard.question,
                                        style = MaterialTheme.typography.headlineMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (!state.isAnswerRevealed) {
                        Button(
                            onClick = onRevealClick,
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text("Revelar Resposta")
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = { onAnswerClick(false) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                contentPadding = PaddingValues(16.dp)
                            ) { Text("Errei") }
                            Button(
                                onClick = { onAnswerClick(true) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                contentPadding = PaddingValues(16.dp)
                            ) { Text("Acertei") }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Quiz Session - Hidden")
@Composable
fun QuizSessionHiddenPreview() {
    FlashcardsTheme {
        QuizSessionScreen(
            state = QuizUiState(
                currentCard = Flashcard(id = 1, deckId = 1, question = "O que é Jetpack Compose?", answer = "Um toolkit moderno para UI nativa."),
                isAnswerRevealed = false,
                currentIndex = 1,
                totalCards = 10
            ),
            onRevealClick = {},
            onAnswerClick = {}
        )
    }
}
