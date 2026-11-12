# MiniBíblia para Galaxy Watch 9 (Wear OS)

Versão da MiniBíblia para o **Samsung Galaxy Watch 9**, que roda **Wear OS** (não Zepp OS).
Esta pasta é independente do app Amazfit, que continua na raiz do repositório (`app.json`, `page/`, `assets/`).

- **Tecnologia:** Kotlin + Jetpack Compose para Wear OS
- **Dados:** os mesmos JSON do app Amazfit (ACF, 66 livros), copiados para `app/src/main/assets/` (`books.json` e `bible/{id}.json`)
- **100% offline e independente do celular**

## Telas

| Tela | Arquivo | O que faz |
|---|---|---|
| Livros | `ui/BooksScreen.kt` | "Continuar lendo", Antigo e Novo Testamento, ajuste de fonte |
| Capítulos | `ui/ChaptersScreen.kt` | Grade de 3 colunas. Livros de 1 capítulo abrem direto no texto |
| Leitor | `ui/ReaderScreen.kt` | Versículos em fonte serifada, números em dourado, botões de capítulo anterior e próximo |
| Fonte | `ui/SettingsScreen.kt` | 5 tamanhos com amostra ao vivo e "Manter tela ligada" |

A luneta giratória rola as listas e deslizar para a direita (ou o botão Voltar do relógio) volta uma tela.
O visual (preto AMOLED e dourado `#FFD700`) vem do protótipo aprovado e do app Amazfit.

## Estrutura

```
galaxy-watch-9/
├── settings.gradle.kts, build.gradle.kts, gradle.properties
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── assets/            books.json + bible/1..66.json
        ├── res/               ícone e nome do app
        └── java/br/com/minibiblia/watch/
            ├── MainActivity.kt        navegação entre as telas
            ├── data/BibleRepository.kt  leitura dos JSON (um livro por vez)
            ├── data/Prefs.kt            fonte, tela ligada, último capítulo
            └── ui/                      telas e tema
```

## Como rodar

Requisitos: **Android Studio** (Ladybug ou mais novo) com Android SDK 35.

1. No Android Studio, abra a pasta `galaxy-watch-9/` (não a raiz do repositório).
2. Aguarde o Gradle sincronizar. O wrapper do Gradle é gerado pelo próprio Android Studio.
3. Escolha um emulador **Wear OS (Large Round)** ou o seu relógio e clique em Run.

### No relógio real (sem Google Play)

1. No relógio: Configurações > Sobre o relógio > Software, toque 7 vezes em "Versão do software" para ativar o modo desenvolvedor.
2. Em Opções do desenvolvedor, ative **Depuração ADB** e **Depurar por Wi-Fi**.
3. No computador: `adb connect <ip-do-relógio>:<porta>`. O relógio aparece como dispositivo no Android Studio.

### Publicar na Google Play

Gere um Android App Bundle em Build > Generate Signed Bundle. É preciso uma conta de desenvolvedor Google Play (taxa única de US$ 25).

## Estado atual

O código foi escrito mas **ainda não foi compilado nem testado em um relógio**, porque o ambiente onde foi criado não tem Android SDK.
Na primeira abertura no Android Studio podem surgir pequenos ajustes de versão de dependências.
O protótipo visual que serviu de referência mostra como cada tela deve ficar.

## APK automático (GitHub Actions)

O workflow `.github/workflows/galaxy-watch-apk.yml` compila o APK a cada push que altere `galaxy-watch-9/`:

- **Download rápido:** aba **Actions** > execução mais recente > **Artifacts** > `MiniBiblia-GalaxyWatch9-apk`.
- **Em Releases (Assets):** crie e envie uma tag que comece com `watch-v`:
  ```bash
  git tag watch-v1.0.0 && git push origin watch-v1.0.0
  ```
  O APK aparece em **Releases** > **Assets**.

O APK é de *debug* (assinado com a chave de depuração), serve para instalar direto no relógio com `adb install`.
Para a Google Play é preciso um bundle assinado com a sua chave de release.
