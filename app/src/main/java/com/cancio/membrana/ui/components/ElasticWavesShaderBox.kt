package com.cancio.membrana.ui.components

import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language

/**
 * Shader AGSL com Signed Distance Fields (SDF) e Smooth Minimum (smin).
 * Produz a fusão líquida/elástica contínua entre a caixa arredondada e o objeto esférico.
 */
@Language("AGSL")
private const val ELASTIC_WAVES_AGSL = """
    uniform float2 uResolution;
    uniform float2 uBoxCenter;
    uniform float2 uBoxSize;     // half-extents (width/2, height/2)
    uniform float  uBoxRadius;
    uniform float2 uBallCenter;
    uniform float  uBallRadius;
    uniform float  uBlendK;      // Fator de tensão elástica (quanto maior, mais estica a membrana)
    uniform half4  uBoxColor;
    uniform half4  uBallColor;

    // Signed Distance Function para uma caixa com cantos arredondados
    float sdRoundBox(float2 p, float2 b, float r) {
        float2 q = abs(p) - b + r;
        return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
    }

    // Signed Distance Function para uma esfera
    float sdCircle(float2 p, float r) {
        return length(p) - r;
    }

    // Polinomial Smooth Minimum (Quartic / Quadratic smin)
    // Retorna a distância combinada e o fator de interpolação h in [0, 1]
    float2 smin(float a, float b, float k) {
        float h = max(k - abs(a - b), 0.0) / k;
        float m = h * h * 0.5;
        float s = m * k * (1.0 / 2.0);
        if (a < b) {
            return float2(a - s, m);
        } else {
            return float2(b - s, 1.0 - m);
        }
    }

    half4 main(float2 fragCoord) {
        // Distância até o box
        float dBox = sdRoundBox(fragCoord - uBoxCenter, uBoxSize, uBoxRadius);

        // Distância até o círculo
        float dBall = sdCircle(fragCoord - uBallCenter, uBallRadius);

        // Fusão elástica (smooth minimum)
        float2 combined = smin(dBox, dBall, uBlendK);
        float dist = combined.x;
        float blend = combined.y; // 0.0 -> box, 1.0 -> ball

        // Anti-aliasing suave na borda externa
        float aa = clamp(0.5 - dist, 0.0, 1.0);

        if (aa <= 0.0) {
            return half4(0.0, 0.0, 0.0, 0.0);
        }

        // Mistura de cor baseada na proximidade/influência
        half4 color = mix(uBoxColor, uBallColor, blend);

        return color * aa;
    }
"""

/**
 * ElasticWavesShaderBox:
 * Implementa a tecnologia de Shader AGSL com renderização por Signed Distance Fields.
 *
 * Em dispositivos Android 13+ (API 33+), executa o AGSL Shader diretamente na GPU.
 * Em versões anteriores, fornece um fallback vetorial via Canvas.
 */
@Composable
fun ElasticWavesShaderBox(
    modifier: Modifier = Modifier,
    boxWidth: Dp = 260.dp,
    boxHeight: Dp = 70.dp,
    boxCornerRadius: Dp = 35.dp,
    ballRadius: Dp = 32.dp,
    elasticTension: Float = 48f, // Grau de elasticidade/membrana (Blend K)
    boxColor: Color = Color.White,
    ballColor: Color = Color(0xFFE53935),
    onBallPositionChanged: (Offset) -> Unit = {}
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val ballX = remember { Animatable(0f) }
    val ballY = remember { Animatable(0f) }

    val boxWidthPx = with(density) { boxWidth.toPx() }
    val boxHeightPx = with(density) { boxHeight.toPx() }
    val boxCornerRadiusPx = with(density) { boxCornerRadius.toPx() }
    val ballRadiusPx = with(density) { ballRadius.toPx() }
    val elasticTensionPx = with(density) { elasticTension.dp.toPx() }

    val isAgslSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    val runtimeShader = remember(isAgslSupported) {
        if (isAgslSupported) {
            RuntimeShader(ELASTIC_WAVES_AGSL)
        } else null
    }

    val paint = remember(runtimeShader) {
        if (runtimeShader != null && isAgslSupported) {
            android.graphics.Paint().apply {
                shader = runtimeShader
                isAntiAlias = true
            }
        } else null
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { },
                    onDragEnd = {
                        // Soltou: snapback elástico com mola para o centro
                        scope.launch {
                            launch {
                                ballX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                            launch {
                                ballY.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        }
                    },
                    onDragCancel = {
                        scope.launch {
                            ballX.animateTo(0f)
                            ballY.animateTo(0f)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            ballX.snapTo(ballX.value + dragAmount.x)
                            ballY.snapTo(ballY.value + dragAmount.y)
                            onBallPositionChanged(Offset(ballX.value, ballY.value))
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val currentBallCenter = Offset(center.x + ballX.value, center.y + ballY.value)

            if (isAgslSupported && runtimeShader != null && paint != null) {
                runtimeShader.setFloatUniform("uResolution", size.width, size.height)
                runtimeShader.setFloatUniform("uBoxCenter", center.x, center.y)
                runtimeShader.setFloatUniform(
                    "uBoxSize",
                    boxWidthPx / 2f,
                    boxHeightPx / 2f
                )
                runtimeShader.setFloatUniform("uBoxRadius", boxCornerRadiusPx)
                runtimeShader.setFloatUniform("uBallCenter", currentBallCenter.x, currentBallCenter.y)
                runtimeShader.setFloatUniform("uBallRadius", ballRadiusPx)
                runtimeShader.setFloatUniform("uBlendK", elasticTensionPx)

                runtimeShader.setFloatUniform(
                    "uBoxColor",
                    boxColor.red,
                    boxColor.green,
                    boxColor.blue,
                    boxColor.alpha
                )
                runtimeShader.setFloatUniform(
                    "uBallColor",
                    ballColor.red,
                    ballColor.green,
                    ballColor.blue,
                    ballColor.alpha
                )

                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawRect(
                        0f, 0f, size.width, size.height, paint
                    )
                }
            } else {
                drawRoundRect(
                    color = boxColor,
                    topLeft = Offset(center.x - boxWidthPx / 2f, center.y - boxHeightPx / 2f),
                    size = Size(boxWidthPx, boxHeightPx),
                    cornerRadius = CornerRadius(boxCornerRadiusPx)
                )
                drawCircle(
                    color = ballColor,
                    radius = ballRadiusPx,
                    center = currentBallCenter
                )
            }
        }
    }
}

