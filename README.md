# Membrana

Experimento interativo de **Tensão de Membrana 360°** em Android (Jetpack Compose + AGSL RuntimeShader).

## Visão Geral

Utiliza **Signed Distance Fields (SDF)** combinados com **Polynomial Smooth Minimum (`smin`)** em um shader AGSL executado na GPU (Android 13+ / API 33+, com fallback em Canvas para versões anteriores) para simular a deformação elástica e fusão líquida contínua entre uma pílula central e uma esfera arrastável com física de mola (`Spring`).

## Controles na Tela

- **Tensão da Membrana (`uBlendK`)**: Ajusta a intensidade da fusão elástica entre a caixa e a esfera (8 dp a 100 dp).
- **Raio da Bola Invasora (`uBallRadius`)**: Ajusta o tamanho da esfera arrastável (20 dp a 55 dp).
- **Reset**: Restaura os parâmetros para a calibração padrão.
