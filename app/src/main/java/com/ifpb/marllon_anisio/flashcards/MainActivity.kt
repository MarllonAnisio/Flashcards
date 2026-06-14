package com.ifpb.marllon_anisio.flashcards

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.ifpb.marllon_anisio.flashcards.data.local.FlashcardDatabase
import com.ifpb.marllon_anisio.flashcards.data.local.UserPreferencesRepository // Importe o novo repositório
import com.ifpb.marllon_anisio.flashcards.data.repository.FlashcardRepository
import com.ifpb.marllon_anisio.flashcards.ui.navigation.FlashcardNavGraph
import com.ifpb.marllon_anisio.flashcards.ui.theme.FlashcardsTheme
import com.ifpb.marllon_anisio.flashcards.ui.viewmodels.CardManagementViewModel
import com.ifpb.marllon_anisio.flashcards.ui.viewmodels.DeckViewModel
import com.ifpb.marllon_anisio.flashcards.ui.viewmodels.QuizViewModel

class MainActivity : ComponentActivity() {

    private val database by lazy { FlashcardDatabase.getDatabase(this) }
    private val repository by lazy { FlashcardRepository(database.dao()) }

    // Inicialização segura e única do seu DataStore local
    private val userPreferencesRepository by lazy { UserPreferencesRepository(applicationContext) }

    private val viewModelFactory by lazy {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(DeckViewModel::class.java) ->
                        // Adicionado o repositório do DataStore aqui
                        DeckViewModel(repository, userPreferencesRepository) as T
                    modelClass.isAssignableFrom(QuizViewModel::class.java) ->
                        QuizViewModel(repository) as T
                    modelClass.isAssignableFrom(CardManagementViewModel::class.java) ->
                        CardManagementViewModel(repository) as T
                    else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }

    private val deckViewModel: DeckViewModel by viewModels { viewModelFactory }
    private val quizViewModel: QuizViewModel by viewModels { viewModelFactory }
    private val cardManagementViewModel: CardManagementViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            deckViewModel.seedDataIfEmpty()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error during startup", e)
            Toast.makeText(this, "Erro ao carregar banco de dados", Toast.LENGTH_LONG).show()
        }

        enableEdgeToEdge()
        setContent {
            // Coleta assíncrona do fluxo do DataStore. O Compose vai se re-desenhar
            // automaticamente sempre que o usuário alternar o tema!
            val currentTheme by userPreferencesRepository.appTheme.collectAsState(initial = "SYSTEM")

            // Repassamos a String do DataStore ("SYSTEM", "DARK" ou "LIGHT") para o seu Theme customizado
            FlashcardsTheme(themePreference = currentTheme) {
                val navController = rememberNavController()
                FlashcardNavGraph(
                    navController = navController,
                    deckViewModel = deckViewModel,
                    quizViewModel = quizViewModel,
                    cardManagementViewModel = cardManagementViewModel
                )
            }
        }
    }
}