package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlCyan
import com.example.ui.theme.PlMint
import com.example.ui.theme.PlPink
import com.example.ui.theme.PlPlumBackground
import com.example.ui.theme.PlPlumBorder
import com.example.ui.theme.PlPlumCardElevated
import com.example.ui.theme.PlTextPrimary
import com.example.ui.theme.PlTextSecondary
import com.example.ui.theme.PlTextTertiary
import com.example.ui.theme.PlYellow

/**
 * Premier League Dark Neon Shareable Gameweek Summary Card.
 * Designed with deep purple (#1E0A24), neon green (#00FF87), and cyan (#04F5FF) accents.
 */
@Composable
fun GameweekSummaryCard(
    userName: String,
    gwNumber: Int,
    gwPoints: Int,
    seasonTotalPoints: Int,
    exactScoresCount: Int,
    correctOutcomesCount: Int,
    captainPoints: Int,
    captainMatchText: String? = null,
    chipsPlayedText: String? = null,
    miniLeagueRankText: String = "Rank #1 in Official Premier League",
    overallRank: Int = 42,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .testTag("gameweek_summary_card"),
        color = Color(0xFF1E0A24), // Premier League deep purple
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    PlMint,            // #00FF87 Neon green
                    PlCyan,            // #04F5FF Cyan
                    Color(0xFFE90052)  // Premier League pink accent
                )
            )
        ),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF280A30), // Dark plum purple
                            Color(0xFF1E0A24), // PL deep purple
                            Color(0xFF120317)  // Deep shadow base
                        )
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App / League Logo Badge Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.app_logo),
                        contentDescription = "Score Matrix Logo",
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "SCORE MATRIX",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                        Text(
                            text = "PREMIER LEAGUE 2026/27",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = PlCyan
                        )
                    }
                }

                // Official Gameweek Tag Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PlMint.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, PlMint.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(PlMint)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "GW $gwNumber COMPLETED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = PlMint
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User's display name & Summary Title
            Text(
                text = "$userName's Gameweek $gwNumber Performance",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PlCyan,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Big Bold Gameweek Points Score (e.g. "78 PTS")
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$gwPoints",
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Black,
                    color = PlMint,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = " PTS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
                )
            }

            // Overall Season context
            Text(
                text = "Total Season: $seasonTotalPoints PTS  •  Overall Rank: #$overallRank",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Highlights Grid (Exact bullseyes, Captain points, Chips played)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Exact Bullseye Scores
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = PlPlumCardElevated,
                    border = BorderStroke(1.dp, PlPlumBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CrisisAlert,
                            contentDescription = "Exact Scores",
                            tint = PlMint,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "$exactScoresCount Bullseyes",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Exact Scores",
                            fontSize = 9.sp,
                            color = PlTextTertiary
                        )
                    }
                }

                // Captain Match Points
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = PlPlumCardElevated,
                    border = BorderStroke(1.dp, PlPlumBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = "Captain Pick",
                            tint = PlYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (captainPoints > 0) "+$captainPoints PTS" else "--",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = PlYellow
                        )
                        Text(
                            text = "Captain Pick",
                            fontSize = 9.sp,
                            color = PlTextTertiary
                        )
                    }
                }

                // Active Chip Highlight
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = PlPlumCardElevated,
                    border = BorderStroke(1.dp, PlPlumBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (chipsPlayedText?.contains("Safety") == true) Icons.Default.Security else Icons.Default.Bolt,
                            contentDescription = "Chip Played",
                            tint = if (chipsPlayedText != null && chipsPlayedText != "None") PlPink else PlCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = chipsPlayedText ?: "Standard",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (chipsPlayedText != null && chipsPlayedText != "None") PlPink else PlCyan,
                            maxLines = 1
                        )
                        Text(
                            text = "Chip Strategy",
                            fontSize = 9.sp,
                            color = PlTextTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mini-Leagues Ranking Snippet (e.g. "Rank #1 in Official Premier League")
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF190620),
                border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(PlCyan.copy(alpha = 0.6f), PlMint.copy(alpha = 0.6f))))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "League Snippet",
                            tint = PlCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = miniLeagueRankText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "LEAGUE LEADER",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = PlMint,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer Watermark and Share Hook
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 plpredictor.app  •  Official Game",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = PlTextTertiary
                )
                Text(
                    text = "#PremierLeaguePredictor",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = PlMint
                )
            }
        }
    }
}
