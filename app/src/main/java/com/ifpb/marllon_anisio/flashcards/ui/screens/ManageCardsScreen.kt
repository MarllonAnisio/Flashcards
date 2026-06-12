package com.ifpb.marllon_anisio.flashcards.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ifpb.marllon_anisio.flashcards.domain.models.Flashcard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCardsScreen(
    cards: List<Flashcard>,
    onAddCard: (String, String) -> Unit,
    onEditCard: (Flashcard) -> Unit,
    onDeleteCard: (Flashcard) -> Unit,
    onBack: () -> Unit,
    onDismissError: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }

    var cardToEdit by remember { mutableStateOf<Flashcard?>(null) }
    var editQuestion by remember { mutableStateOf("") }
    var editAnswer by remember { mutableStateOf("") }
    
    var cardToDelete by remember { mutableStateOf<Flashcard?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            onDismissError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Gerenciar Cards") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading && cards.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (cards.isEmpty()) {
                Text("Este baralho está vazio.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = cards, key = { it.id }) { card ->
                        val haptic = LocalHapticFeedback.current
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    cardToDelete = card
                                    false
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color = MaterialTheme.colorScheme.error
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color, MaterialTheme.shapes.medium)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.onError)
                                }
                            },
                            content = {
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(card.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                            Text(card.answer, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        IconButton(onClick = { 
                                            cardToEdit = card
                                            editQuestion = card.question
                                            editAnswer = card.answer
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
            if (isLoading && cards.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Novo Card") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = question, 
                        onValueChange = { question = it }, 
                        label = { Text("Pergunta") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = answer, 
                        onValueChange = { answer = it }, 
                        label = { Text("Resposta") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (question.isNotBlank() && answer.isNotBlank()) {
                        onAddCard(question, answer)
                        question = ""
                        answer = ""
                        showAddDialog = false
                    }
                }) { Text("Adicionar") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (cardToEdit != null) {
        AlertDialog(
            onDismissRequest = { cardToEdit = null },
            title = { Text("Editar Card") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editQuestion, 
                        onValueChange = { editQuestion = it }, 
                        label = { Text("Pergunta") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAnswer, 
                        onValueChange = { editAnswer = it }, 
                        label = { Text("Resposta") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (editQuestion.isNotBlank() && editAnswer.isNotBlank() && cardToEdit != null) {
                        onEditCard(cardToEdit!!.copy(question = editQuestion, answer = editAnswer))
                        cardToEdit = null
                    }
                }) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { cardToEdit = null }) { Text("Cancelar") }
            }
        )
    }

    if (cardToDelete != null) {
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = { Text("Excluir Cartão") },
            text = { Text("Tem certeza que deseja excluir este cartão permanentemente?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCard(cardToDelete!!)
                        cardToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { cardToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}
