package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlCyan
import com.example.ui.theme.PlMint
import com.example.ui.theme.PlPink
import com.example.ui.theme.PlYellow

enum class ChipPillType {
    CAPTAIN,
    SUPER_CAPTAIN,
    SAFETY_NET,
    AUTO_CAPTAIN,
    DOUBLE_SHOT,
    GW_MVP,
    EXACT_SCORE,
    CLEAN_SHEET
}

@Composable
fun ChipStatusPill(
    type: ChipPillType,
    text: String? = null,
    modifier: Modifier = Modifier
) {
    val (label, bg, border, tint, icon) = when (type) {
        ChipPillType.CAPTAIN -> Quintuple(
            text ?: "CAPTAIN x2",
            PlYellow.copy(alpha = 0.2f),
            PlYellow,
            PlYellow,
            Icons.Default.Star
        )
        ChipPillType.SUPER_CAPTAIN -> Quintuple(
            text ?: "SUPER CAPTAIN x3",
            PlPink.copy(alpha = 0.25f),
            PlPink,
            PlPink,
            Icons.Default.Bolt
        )
        ChipPillType.SAFETY_NET -> Quintuple(
            text ?: "SAFETY NET +3",
            PlCyan.copy(alpha = 0.2f),
            PlCyan,
            PlCyan,
            Icons.Default.Security
        )
        ChipPillType.AUTO_CAPTAIN -> Quintuple(
            text ?: "AUTO CAPTAIN",
            PlYellow.copy(alpha = 0.25f),
            PlYellow,
            PlYellow,
            Icons.Default.Star
        )
        ChipPillType.DOUBLE_SHOT -> Quintuple(
            text ?: "DOUBLE SHOT",
            PlMint.copy(alpha = 0.25f),
            PlMint,
            PlMint,
            Icons.Default.Bolt
        )
        ChipPillType.GW_MVP -> Quintuple(
            text ?: "GW MVP",
            PlMint.copy(alpha = 0.2f),
            PlMint,
            PlMint,
            Icons.Default.EmojiEvents
        )
        ChipPillType.EXACT_SCORE -> Quintuple(
            text ?: "EXACT (+3)",
            PlMint.copy(alpha = 0.2f),
            PlMint,
            PlMint,
            Icons.Default.CheckCircle
        )
        ChipPillType.CLEAN_SHEET -> Quintuple(
            text ?: "CLEAN SHEET BONUS",
            PlCyan.copy(alpha = 0.2f),
            PlCyan,
            PlCyan,
            Icons.Default.Security
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, border.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = tint,
                letterSpacing = 0.5.sp
            )
        }
    }
}

private data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
