package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.getClubColor
import com.example.ui.theme.getClubTextColor

@Composable
fun TeamCrestBadge(
    code: String,
    teamName: String,
    isDarkText: Boolean = false,
    modifier: Modifier = Modifier
) {
    val clubColor = getClubColor(code)
    val badgeTextColor = getClubTextColor(code)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular Club Badge
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(clubColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = code.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = badgeTextColor,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Club Name
        Text(
            text = teamName,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkText) Color(0xFF38003C) else Color.White,
            letterSpacing = (-0.3).sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
