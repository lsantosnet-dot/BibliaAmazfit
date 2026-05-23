# MiniBíblia 📖✨ (Zepp OS 4)

O **MiniBíblia** é um mini-programa desenvolvido nativamente em **JavaScript** para o sistema operacional **Zepp OS 4**, otimizado para o relógio inteligente **Amazfit T-Rex 3** (resolução circular de 480x480).

Este aplicativo foi projetado com foco em **extrema economia de memória RAM** e permite a leitura offline completa da Bíblia Sagrada na tradução **Almeida Corrigida Fiel (ACF)**, dispensando conexão com a internet ou smartphone durante o uso.

---

## 🚀 Diferenciais Técnicas e Otimizações

### 🧠 Otimização Extrema de RAM
A Bíblia completa em texto bruto possui mais de 4MB. Em dispositivos vestíveis (*wearables*), carregar um arquivo desse tamanho em um único bloco de memória resultaria em estouro de RAM e travamento imediato. 
* **Divisão em Módulos:** Dividimos o texto completo da Bíblia em **66 arquivos JSON individuais** (um para cada livro) localizados em `/assets/t-rex-3/bible/`, e um índice leve (`books.json` com ~4.5KB).
* **Consumo sob Demanda:** O aplicativo carrega exclusivamente o livro que está sendo lido. No pior cenário (o livro de Salmos, que é o maior), o pico de memória utilizada para leitura do buffer fica abaixo de **200KB**, garantindo fluidez e estabilidade total no relógio.

### 🔠 Correção UTF-8 Nativa em JS
Implementamos um decodificador de array binário UTF-8 sob medida (`bufferToString`) em JavaScript. Isso resolve o problema de caracteres acentuados em português (como `ç`, `ã`, `é`, `í`, `ó`) que costumam ficar truncados em leituras diretas em ASCII no Zepp OS. A conversão de strings é realizada em lotes (*batching*) de 1024 caracteres, preservando a pilha de execução e eliminando vazamentos de memória.

### 🎨 Design Premium AMOLED e Tela Circular
* **Preto Absoluto:** Interface baseada em fundo preto profundo (`0x000000`) para economia drástica de bateria em telas AMOLED e conforto visual em ambientes escuros.
* **Estilo Dourado:** Detalhes e destaques em Ouro Imperial (`0xFFD700`) e textos em Branco para contraste elevado sob a luz do sol.
* **Layout Adaptativo Circular:** A tela do T-Rex 3 é redonda. Para impedir que as curvas cortem o texto, aplicamos uma margem de segurança lateral de `40px` (largura útil de `400px` centralizada) em todas as exibições de versículos.
* **Alturas Dinâmicas no Leitor:** Os versículos são renderizados em uma lista de rolagem com alturas calculadas dinamicamente com base no número de caracteres de cada versículo (evitando áreas em branco ou cortes).
* **Scroll Padding:** Adição de espaçadores no topo e base da lista para permitir que o primeiro e o último versículo fiquem centralizados perfeitamente na linha dos olhos.

---

## 📁 Estrutura do Projeto

* `app.json`: Arquivo de manifesto global com as definições do mini-programa, registro das páginas e listagem de relógios compatíveis.
* `app.js`: Inicialização e ciclo de vida do aplicativo com o estado global da navegação (`globalData`).
* `page/index.js`: **Tela 1 - Livros:** Exibe os 66 livros da Bíblia em uma lista vertical fluida com suporte a toque.
* `page/chapter.js`: **Tela 2 - Capítulos:** Apresenta uma matriz circular de botões (3 colunas) para seleção intuitiva e rápida do capítulo.
* `page/reader.js`: **Tela 3 - Leitor:** Carrega o JSON do livro selecionado em memória e renderiza os versículos de forma dinâmica e formatada.
* `assets/t-rex-3/`: Pasta de assets compilados exclusiva do relógio T-Rex 3, contendo o ícone, o índice `books.json` e a base de dados em `bible/`.

---

## 🛠️ Como Rodar e Desenvolver

Para compilar, emular e testar o projeto no seu relógio ou simulador, você precisará da ferramenta oficial de linha de comando da Zepp: o **Zeus CLI**.

### Pré-requisitos
* **Node.js** (versão 16.x ou superior recomendada)
* **npm** (instalado junto com o Node)

### 1. Instalar o Zeus CLI globalmente
Abra o seu terminal ou prompt de comando (PowerShell/CMD) e execute:
```bash
npm install -g @zeppos/zeus-cli
```
*Caso já tenha o zeus instalado, certifique-se de que ele esteja na versão mais recente.*

### 2. Clonar o Repositório e Navegar até a Pasta
```bash
git clone https://github.com/lsantosnet-dot/BibliaAmazfit.git
cd BibliaAmazfit
```

### 3. Visualizar no Simulador (Zepp OS Simulator)
Abra o simulador oficial do Zepp OS no seu computador, certifique-se de que a conta de desenvolvedor esteja conectada, e execute no terminal do projeto:
```bash
zeus dev
```
O simulador carregará o aplicativo automaticamente para teste interativo.

### 4. Compilar o Pacote de Distribuição (`.rpk`)
Para gerar o pacote final que pode ser distribuído para o relógio físico, execute:
```bash
zeus build
```
Este comando criará uma pasta `dist/` com os pacotes compilados para o Amazfit T-Rex 3 e outros alvos suportados.

### 5. Instalar no Relógio Físico (Pré-visualização via QR Code)
Você pode enviar o aplicativo em tempo real diretamente para o relógio via Bluetooth:
```bash
zeus preview
```
1. O terminal perguntará qual relógio deseja emular. Escolha **Amazfit T-Rex 3** e pressione Enter.
2. Um código QR será renderizado no próprio terminal.
3. Abra o aplicativo **Zepp** em seu celular emparelhado com o relógio.
4. Vá em **Perfil** > **Configurações** > **Ferramentas de Desenvolvedor**.
5. Toque em **Escanear Código QR** e aponte a câmera do seu celular para o código gerado no terminal. O app será transferido e abrirá instantaneamente no seu pulso!

---

## 📜 Licença
Este projeto possui fins educacionais e de uso pessoal. A base de dados utilizada é baseada na tradução pública de domínio livre **Almeida Corrigida Fiel (ACF)**.
