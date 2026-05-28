# Flashcards - Aplicativo de Estudo com Repetição Espaçada

Este projeto foi desenvolvido como parte de um desafio técnico para demonstrar conhecimentos avançados em **Modern Android Development (MAD)**, utilizando **Jetpack Compose**, **Clean Architecture** e persistência de dados robusta.

## 🎯 Objetivo do Aplicativo
O **Flashcards** resolve o problema de retenção de conhecimento a longo prazo. Ele permite que usuários criem baralhos personalizados de cartões de estudo (pergunta e resposta), gerenciem seu conteúdo e realizem sessões de quiz interativas para testar seus conhecimentos. É uma ferramenta ideal para estudantes, desenvolvedores e qualquer pessoa que deseje memorizar conceitos complexos de forma eficiente.

---

## 🏗️ Descritivo da Arquitetura e Código
O aplicativo segue os princípios da **Clean Architecture**, dividido em camadas bem definidas para garantir testabilidade e manutenção:

### 1. Camada de Domain (Modelos e Lógica de Negócio)
- **`Models.kt`**: Define as entidades principais (`Deck`, `Flashcard`) e o estado da UI (`QuizUiState`). Inclui validações de integridade (ex: impede nomes ou perguntas vazias).

### 2. Camada de Data (Persistência e Repositório)
- **Room Database**: Utiliza o SQLite para salvar dados permanentemente.
    - **Foreign Keys**: Implementadas com `CASCADE DELETE`, garantindo que ao excluir um baralho, todos os seus cartões sejam removidos automaticamente.
    - **DAO**: Interfaces reativas que retornam `Flow`, permitindo que a UI se atualize automaticamente quando o banco de dados muda.
- **Repository Pattern**: Centraliza o acesso aos dados, tratando erros e expondo resultados via o padrão `Result`.

### 3. Camada de UI (ViewModels e Screens)
- **ViewModels**: Gerenciam o estado da tela de forma reativa:
    - `DeckViewModel`: Listagem e criação de baralhos.
    - `QuizViewModel`: Lógica da sessão de estudo (embaralhamento e pontuação).
    - `CardManagementViewModel`: Gestão específica de cartões dentro de um baralho.
- **Navegação**: Utiliza o **Type-Safe Navigation Compose**, garantindo que a transição entre telas seja segura e livre de erros de digitação em rotas.

---

## 🎨 Utilização do Jetpack Compose
O aplicativo foi construído **100% em Jetpack Compose**, aproveitando as melhores funcionalidades do toolkit:

- **Declarative UI**: Toda a interface é definida via funções `@Composable`, facilitando a leitura e modificação.
- **State Hoisting**: O estado é mantido nas ViewModels e "hasteado" para os componentes, tornando-os stateless e fáceis de testar.
- **Animações**: Utilização de `AnimatedContent` e `AnimatedVisibility` para transições suaves entre perguntas e respostas, proporcionando uma experiência de usuário moderna.
- **Material Design 3**: Implementação rigorosa do Material 3, com suporte a cores dinâmicas, `ElevatedCards`, `LargeTopAppBars` e formas personalizadas (`shapes`).
- **UDF (Unidirectional Data Flow)**: Garante que os dados fluam em apenas uma direção (da ViewModel para a UI) e os eventos no sentido oposto.

---

## 🚀 Execução e Telas
O aplicativo possui um fluxo de usuário intuitivo composto por quatro áreas principais:

1.  **Tela de Seleção de Baralho**: Exibe a lista de temas disponíveis com suporte a criação dinâmica (Botão FAB) e exclusão.
2.  **Gestão de Cards**: Uma área administrativa para cada baralho, onde o usuário pode adicionar novas perguntas ou remover cartões existentes.
3.  **Sessão de Quiz**: Interface focada no estudo, mostrando o progresso atual, a pergunta e o botão de revelação animada.
4.  **Tela de Resultados**: Feedback visual sobre o desempenho do usuário após concluir o baralho, com opção de reiniciar ou voltar ao início.

---

## 🧪 Qualidade e Verificação
- **Integridade de Dados**: Banco de dados versionado (v2) com proteção contra orfandade de dados.
- **Segurança de Navegação**: Proteção contra múltiplas navegações acidentais via `LaunchedEffect`.
- **Testes Unitários**: Implementados para validar a lógica de negócios central nas ViewModels.

---
**Desenvolvido por:** Marllon Anisio
**Tecnologias:** Kotlin, Jetpack Compose, Room, Navigation Compose, Serialization, Coroutines & Flow.
