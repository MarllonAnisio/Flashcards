package com.ifpb.marllon_anisio.flashcards.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ifpb.marllon_anisio.flashcards.domain.models.Deck
import com.ifpb.marllon_anisio.flashcards.ui.theme.FlashcardsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckSelectionScreen(
    onDeckSelected: (Int) -> Unit,
    onManageDeck: (Int) -> Unit,
    onAddDeck: (String) -> Unit,
    onDeleteDeck: (Deck) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
    decks: List<Deck> = emptyList(),
    isLoading: Boolean = false,
    error: String? = null
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newDeckName by remember { mutableStateOf("") }

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
            LargeTopAppBar(
                title = { 
                    Text(
                        "Meus Baralhos",
                        fontWeight = FontWeight.Bold
                    ) 
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Novo Baralho")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading && decks.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (decks.isEmpty()) {
                Text(
                    "Nenhum baralho encontrado. Crie um novo!", 
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(decks) { deck ->
                        DeckCard(
                            deck = deck,
                            onClick = { onDeckSelected(deck.id) },
                            onManage = { onManageDeck(deck.id) },
                            onDelete = { onDeleteDeck(deck) }
                        )
                    }
                }
            }
            
            if (isLoading && decks.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Novo Baralho") },
            text = {
                OutlinedTextField(
                    value = newDeckName,
                    onValueChange = { newDeckName = it },
                    label = { Text("Nome do Baralho") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDeckName.isNotBlank()) {
                            onAddDeck(newDeckName)
                            newDeckName = ""
                            showAddDialog = false
                        }
                    }
                ) { Text("Criar") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun DeckCard(
    deck: Deck,
    onClick: () -> Unit,
    onManage: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = deck.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Toque para estudar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opções")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Gerenciar Cards") },
                        onClick = { 
                            showMenu = false
                            onManage() 
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, null) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Excluir Baralho") },
                        onClick = { 
                            showMenu = false
                            onDelete() 
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, null) },
                        colors = MenuDefaults.itemColors(
                            leadingIconColor = MaterialTheme.colorScheme.error,
                            textColor = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeckSelectionPreview() {
    FlashcardsTheme {
        DeckSelectionScreen(
            onDeckSelected = {},
            onManageDeck = {},
            onAddDeck = {},
            onDeleteDeck = {},
            onDismissError = {},
            decks = listOf(Deck(1, "Preview Deck"))
        )
    }
}
