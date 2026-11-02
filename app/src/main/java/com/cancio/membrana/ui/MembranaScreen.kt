package com.cancio.membrana.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cancio.membrana.theme.MembranaTheme
import com.cancio.membrana.ui.components.ElasticWavesShaderBox

/**
 * Tela principal do app Membrana:
 * Experimento livre de Tensão de Membrana 360° utilizando AGSL Shader (Signed Distance Fields + Smooth Minimum).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembranaScreen() {
    var freeTension by remember { mutableFloatStateOf(48f) }
    var freeBoxWidthDp by remember { mutableFloatStateOf(260f) }
    var freeBoxHeightDp by remember { mutableFloatStateOf(70f) }
    var freeBallRadiusDp by remember { mutableFloatStateOf(34f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Membrana",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "360° Free Membrane Box (AGSL Shader)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9EA3B0)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            freeTension = 48f
                            freeBoxWidthDp = 260f
                            freeBoxHeightDp = 70f
                            freeBallRadiusDp = 34f
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Resetar Parâmetros",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF141720)
                )
            )
        },
        containerColor = Color(0xFF0F1117)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Card informativo
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E222D)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Column {
                        Text(
                            text = "Deformação livre em 360° além de todas as bordas.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Arraste a bola vermelha para fora do box branco em qualquer direção.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9EA3B0)
                        )
                    }
                }
            }

            // Área Central com o ElasticWavesShaderBox
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ElasticWavesShaderBox(
                    modifier = Modifier.fillMaxSize(),
                    boxWidth = freeBoxWidthDp.dp,
                    boxHeight = freeBoxHeightDp.dp,
                    boxCornerRadius = (freeBoxHeightDp / 2f).dp,
                    ballRadius = freeBallRadiusDp.dp,
                    elasticTension = freeTension,
                    boxColor = Color.White,
                    ballColor = Color(0xFFE53935)
                )
            }

            // Painel de Parâmetros da Membrana 360°
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1D26)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PARÂMETROS DA MEMBRANA 360°",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tensão da Membrana (Blend K)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                        Text(
                            text = "${freeTension.toInt()} dp",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = freeTension,
                        onValueChange = { freeTension = it },
                        valueRange = 8f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Raio da Bola Invasora",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                        Text(
                            text = "${freeBallRadiusDp.toInt()} dp",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = freeBallRadiusDp,
                        onValueChange = { freeBallRadiusDp = it },
                        valueRange = 20f..55f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Preview(name = "Membrana Screen", showBackground = true, backgroundColor = 0xFF0F1117)
@Composable
private fun MembranaScreenPreview() {
    MembranaTheme {
        MembranaScreen()
    }
}

