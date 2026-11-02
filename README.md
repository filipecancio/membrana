# Membrana 🫧🔬

[![Documentação Wiki](https://img.shields.io/badge/Documentação-Wiki%20Oficial-purple?logo=gitbook)](https://github.com/filipecancio/membrana/wiki)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3-orange?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack-Compose%20M3-4285F4?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![AGSL](https://img.shields.io/badge/Shader-AGSL%20RuntimeShader-00E676?logo=android)](https://developer.android.com/develop/ui/views/graphics/agsl)

Experimento interativo de **Tensão de Membrana 360°** em Android nativo, desenvolvido com **Jetpack Compose** e **AGSL (`RuntimeShader`)**.

---

## ✨ Visão Geral

O **Membrana** demonstra como substituir recortes geométricos rígidos por superfícies implícitas contínuas avaliadas na GPU por pixel:
* **Signed Distance Fields (SDF)**: A pílula central (`sdRoundBox`) e a esfera invasora (`sdCircle`) são descritas analiticamente por campos escalares de distância euclidiana com sinal.
* **Fusão Líquida (`smin`)**: Um operador *Polynomial Smooth Minimum* quadrático combina os dois campos de distância dentro do raio de influência `uBlendK`, formando pontes líquidas, meniscos côncavos e mistura cromática contínua antes da ruptura (*pinch-off*).
* **Anti-Aliasing Analítico**: Suavização sub-pixel de borda em 1 linha (`clamp(0.5 - dist, 0.0, 1.0)`) com *premultiplied alpha*.
* **Interação 360° com Física de Mola**: Arraste livre em qualquer direção com retorno governado por um oscilador harmônico subamortecido (`Spring.DampingRatioMediumBouncy`, `Spring.StiffnessLow`) sem recomposição de layout.

---

## 🎛️ Controles de Calibração na Tela

- **Tensão da Membrana (`uBlendK`)**: Ajusta a distância de alcance da fusão elástica entre a caixa e a esfera (`8 dp` a `100 dp`).
- **Raio da Bola Invasora (`uBallRadius`)**: Ajusta o raio da esfera arrastável (`20 dp` a `55 dp`).
- **Reset**: Restaura instantaneamente os parâmetros para a calibração padrão (`48 dp` de tensão e `34 dp` de raio).

---

## 📚 Documentação Técnica & Matemática (Wiki)

Toda a dedução matemática das funções de distância, álgebra da fusão suave, arquitetura do pipeline GPU no Android e equações diferenciais da mola está documentada passo a passo na **[Wiki Oficial do Projeto](https://github.com/filipecancio/membrana/wiki)**:

| Subpágina da Wiki | Conteúdo Abordado |
| :--- | :--- |
| 🏠 **[Início / Visão Geral](https://github.com/filipecancio/membrana/wiki)** | Visão macro do pipeline gráfico (do toque na tela à execução por fragmento na GPU) e mapa da arquitetura. |
| 📐 **[1. Fundamentos Matemáticos: Signed Distance Fields (SDF)](https://github.com/filipecancio/membrana/wiki/Fundamentos-Matematicos-SDF)** | Definição formal de SDF em $\mathbb{R}^2$, Equação Eikonal ($\|\nabla f\| = 1$) e dedução geométrica de `sdCircle` e `sdRoundBox`. |
| 🧪 **[2. Fusão Líquida e Tensão de Membrana (`smin`)](https://github.com/filipecancio/membrana/wiki/Fusao-Liquida-Smooth-Minimum)** | Derivação algébrica do *Polynomial Smooth Minimum* quadrático, cálculo do ponto crítico de ruptura ($\Delta = k/2$), mistura cromática e anti-aliasing analítico. |
| ⚡ **[3. Pipeline GPU no Android: AGSL & `RuntimeShader`](https://github.com/filipecancio/membrana/wiki/AGSL-RuntimeShader-Pipeline-GPU)** | Integração do AGSL com o RenderThread/Skia (API 33+), conversão `Dp` $\to$ `Px`, sincronização de *uniforms* e fallback vetorial (APIs 24–32). |
| 🎢 **[4. Interação 360°, Gestos e Física de Mola (`Spring`)](https://github.com/filipecancio/membrana/wiki/Fisica-de-Mola-e-Gestos-Compose)** | Equação diferencial do oscilador harmônico amortecido, paralelismo em corrotinas 2D e invalidação exclusiva da fase de *Draw* no Compose. |

---

## 🚀 Como Executar o Projeto

```bash
# 1. Clone o repositório
git clone https://github.com/filipecancio/membrana.git
cd membrana

# 2. Gere o APK de Debug
./gradlew assembleDebug
```
