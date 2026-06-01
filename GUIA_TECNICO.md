# 📐 Guia de Engenharia: Por trás dos Flashcards

Este guia é destinado a desenvolvedores e estudantes que desejam entender a estrutura técnica profunda por trás desta aplicação.

---

## 1. O Fluxo de Navegação (NavGraph & Type-Safety)

A navegação é o esqueleto do app. Em `NavGraph.kt`, utilizamos a biblioteca de navegação do Jetpack com **Kotlin Serialization**. 

*   **Vantagem:** Em vez de usar strings como `"quiz/{id}"`, usamos classes e objetos reais. Isso evita os famosos `NullPointerException` e erros de digitação durante o desenvolvimento.
*   **Gestão de Estado:** O `NavHost` observa os ViewModels e passa os dados necessários (como o ID do baralho) para as telas correspondentes.

## 2. Componentização da UI (Screens)

O arquivo `FlashcardScreens.kt` é um exemplo de **UI Baseada em Componentes**.

*   **Stateless Components:** Telas como `DeckCard` ou `QuizSessionScreen` não "sabem" de onde vêm os dados. Elas apenas recebem informações e emitem eventos. Isso as torna reutilizáveis e fáceis de visualizar no Android Studio Preview.
*   **Animações:** Utilizamos `AnimatedContent` para as transições de "Pergunta para Resposta", criando uma experiência tátil para o usuário.

## 3. Gestão de Estado (ViewModels)

O **ViewModel** é o coração da lógica. 
*   **QuizViewModel:** Gerencia a lógica de embaralhamento dos cards e a contagem de pontos. Ele expõe um único objeto `UiState`, garantindo que a tela sempre reflita o estado atual da sessão de estudo.
*   **Ciclo de Vida:** O uso do `viewModelScope` garante que operações de banco de dados sejam canceladas se o usuário sair da tela, evitando vazamentos de memória (Memory Leaks).

## 4. Persistência de Dados (Room Database)

A camada `data/` implementa o padrão **Repository**.
*   **Entities:** Representam as tabelas `decks` e `flashcards`.
*   **DAO (Data Access Object):** Onde as queries SQL são definidas. Usamos `Flow` para que a UI se atualize em tempo real sempre que um card for adicionado ou removido.
*   **Relacionamentos:** Utilizamos Chaves Estrangeiras com `CASCADE`, garantindo que ao deletar um baralho, todos os seus cartões sejam removidos automaticamente, mantendo o banco de dados íntegro.

---
*Este projeto é uma demonstração de como aplicar Clean Architecture em um cenário real e prático.*
