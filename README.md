# 🎸 Guitar Hero Clone (Java)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Java%20Swing-007396?style=for-the-badge&logo=java&logoColor=white)
![Status](https://img.shields.io/badge/Status-Concluído-success?style=for-the-badge)
![IDE](https://img.shields.io/badge/IDE-IntelliJ%20IDEA-purple?style=for-the-badge&logo=intellijidea)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

Clone do clássico **Guitar Hero**, desenvolvido em **Java** com **Java Swing**, focado em lógica de jogos, orientação a objetos e geração automatizada de notas com base no ritmo da música.

---

## 🚀 Funcionalidades

- 🎶 Sistema de jogo no estilo *rhythm game*
- ⏱️ Notas sincronizadas com a batida da música
- 📄 Geração automática de notas a partir de um arquivo `.txt`
- 🧠 Código organizado com separação de responsabilidades
- 🖥️ Interface gráfica desktop usando **Java Swing**
- 🎯 Sistema de pontuação e feedback visual

---

## 🛠️ Funcionalidades Detalhadas

### 🎮 Interface Gráfica (Java Swing)
O jogo utiliza **Java Swing** para renderizar:
- Janela principal
- Área de notas
- Feedback visual de acertos
- Tela de pontuação

---

### 🎵 Geração Automática de Notas
As notas não são criadas manualmente.

O jogo:
1. Lê um **arquivo de texto** contendo os tempos das batidas
2. Processa os dados
3. Cria as notas dinamicamente de acordo com o ritmo da música

Isso facilita:
- Troca de músicas
- Ajustes de dificuldade
- Escalabilidade do projeto

---

### 🧩 Organização Orientada a Objetos
O projeto foi estruturado em aproximadamente **8 classes**, cada uma com uma responsabilidade clara, seguindo princípios de **POO**.

---

## 📁 Estrutura do Projeto
Guitar-Hero-Clone/
├── .idea/ # Configurações da IDE
├── src/
│ └── Main/
│ ├── Java/
│ │ ├── Core/ # Loop principal do jogo
│ │ ├── Notes/ # Lógica e eventos das notas
│ │ ├── Audio/ # Reprodução de música
│ │ └── UI/ # Interface gráfica
├── .gitignore
├── Guitar-Hero-Clone.iml
└── README.md
