# 📇 Flashcards: Domine qualquer assunto, um card por vez.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg)
![Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-green.svg)
![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)

O **Flashcards** é uma ferramenta de estudo focada em eficiência e retenção de memória a longo prazo. Este aplicativo foi desenvolvido para transformar o aprendizado de conceitos complexos em sessões rápidas, interativas e focadas.

---

## 💡 O que são Flashcards?

Um flashcard é, essencialmente, um cartão de memória com uma **pergunta** na frente e uma **resposta** no verso. É uma das técnicas de estudo mais poderosas que existem, baseada em dois conceitos científicos:

1.  **Active Recall (Recuperação Ativa):** Ao ver a pergunta, seu cérebro é forçado a recuperar a informação, fortalecendo as conexões neurais.
2.  **Repetição Espaçada:** O ato de revisar o que você errou com mais frequência garante que a informação saia da memória de curto prazo e vá para a de longo prazo.

---

## 📱 A Experiência do Aplicativo

Nesta aplicação, você tem o controle total do seu aprendizado:

*   **Organização por Baralhos:** Agrupe seus estudos por temas (ex: "Programação Kotlin", "Inglês Avançado", "História").
*   **Gestão Dinâmica:** Adicione, edite ou remova cards conforme seu conhecimento evolui.
*   **Modo de Estudo (Quiz):** 
    *   Interface limpa para evitar distrações.
    *   Sistema de revelação de resposta com um toque.
    *   Autoavaliação: Você decide se acertou ou errou, treinando sua honestidade intelectual.
*   **Feedback de Performance:** Ao final de cada sessão, veja sua taxa de acerto e identifique onde precisa melhorar.

---

## 🛠️ Por dentro da Engenharia (Stack Técnica)

Como arquiteto do projeto, a aplicação foi construída utilizando o que há de mais moderno no ecossistema Android para garantir fluidez e confiabilidade:

### 🎨 Interface Moderna (Jetpack Compose)
Esqueça as telas estáticas do passado. Toda a interface é **declarativa**, o que significa que as animações de transição de cards e os feedbacks visuais são suaves e integrados ao Material Design 3.

### 🧠 Cérebro e Memória (MVVM + Room)
*   **Arquitetura MVVM:** Separa a lógica de negócio (como a pontuação do quiz) da interface visual. Se o seu celular girar no meio de um estudo, você não perde seu progresso.
*   **Banco de Dados Local (Room):** Seus baralhos são guardados no dispositivo. Estude no metrô, no avião ou em qualquer lugar, sem precisar de internet.

### 🗺️ Navegação Segura (NavGraph)
Implementamos a nova **Type-Safe Navigation** do Google. Isso garante que, ao navegar entre os baralhos e as sessões de estudo, não ocorram falhas ou erros de dados corrompidos.

---

## 🚀 Começando

1.  Ao abrir o app, você encontrará baralhos de exemplo para entender a dinâmica.
2.  Crie seu próprio baralho no botão **"+"**.
3.  Adicione seus primeiros cards com pergunta e resposta.
4.  Toque no baralho e comece a desafiar sua mente!

---
*Desenvolvido com foco em engenharia de software de alta performance por **Marllon Anísio**.*
