package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlCyan
import com.example.ui.theme.PlMint

@Composable
fun CommunityConsensusBar(
    homeWinPct: Int,
    drawPct: Int,
    awayWinPct: Int,
    homeCode: String,
    awayCode: String,
    isDarkCard: Boolean = false,
    modifier: Modifier = Modifier
) {
    val labelColor = if (isDarkCard) Color.White.copy(alpha = 0.5f) else Color(0xFF38003C).copy(alpha = 0.45f)
    val valueColor = if (isDarkCard) Color.White else Color(0xFF38003C)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "COMMUNITY",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = labelColor,
                letterSpacing = 1.sp
            )
            Text(
                text = "$homeWinPct% • $drawPct% • $awayWinPct%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = valueColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Segmented Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
        ) {
            if (homeWinPct > 0) {
                Box(
                    modifier = Modifier
                        .weight(homeWinPct.toFloat())
                        .height(4.dp)
                        .background(if (isDarkCard) PlCyan else Color(0xFF034694))
                )
            }
            if (drawPct > 0) {
                Box(
                    modifier = Modifier
                        .weight(drawPct.toFloat())
                        .height(4.dp)
                        .background(if (isDarkCard) Color.White.copy(alpha = 0.2f) else Color(0xFF38003C).copy(alpha = 0.15f))
                )
            }
            if (awayWinPct > 0) {
                Box(
                    modifier = Modifier
                        .weight(awayWinPct.toFloat())
                        .height(4.dp)
                        .background(if (isDarkCard) PlMint else Color(0xFF00B050))
                )
            }
        }
    }
}

