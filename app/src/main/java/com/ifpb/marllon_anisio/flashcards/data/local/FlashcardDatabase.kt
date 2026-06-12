package com.ifpb.marllon_anisio.flashcards.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DeckEntity::class, FlashcardEntity::class], version = 3)
abstract class FlashcardDatabase : RoomDatabase() {
    abstract fun dao(): FlashcardDao

    companion object {
        @Volatile
        private var INSTANCE: FlashcardDatabase? = null
        /**
         * reutilizando o singleton depois de 9 anos de padroes de projeto kkkkkkkk
         * */
        fun getDatabase(context: Context): FlashcardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlashcardDatabase::class.java,
                    "flashcard_database"
                )
                .fallbackToDestructiveMigration() // aqui é foda, eu to usando mas o significado é: "Ei negão...
                .fallbackToDestructiveMigrationOnDowngrade() // Garante que downgrades na versão (ex: 3 -> 1) não quebrem o app em fase de desenvolvimento
                .build()
                INSTANCE = instance
                instance

                /**
                 * coisa ainda pra aprender: Injeção de Dependência" (Hilt ou Koin), isso é um assunto mais avançado, mas basicamente, ao invés de criar o banco de dados diretamente dentro do código, a gente pode usar uma biblioteca de injeção de dependência para gerenciar a criação e o ciclo de vida do banco de dados, isso ajuda a manter o código mais limpo e testável, além de facilitar a troca de implementações no futuro,
                 * como por exemplo, trocar o Room por outro banco de dados sem precisar mudar o código que usa o banco.
                 * */
            }
        }
    }
}
