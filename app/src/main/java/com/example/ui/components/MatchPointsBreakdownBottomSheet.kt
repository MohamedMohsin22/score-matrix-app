package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FixtureEntity
import com.example.data.local.entity.PredictionEntity
import com.example.data.model.MatchScoringEngine
import com.example.data.model.ScoringResult
import com.example.ui.theme.PlCyan
import com.example.ui.theme.PlMint
import com.example.ui.theme.PlPink
import com.example.ui.theme.PlPlumBackground
import com.example.ui.theme.PlPlumBorder
import com.example.ui.theme.PlPlumCard
import com.example.ui.theme.PlPlumCardElevated
import com.example.ui.theme.PlTextPrimary
import com.example.ui.theme.PlTextSecondary
import com.example.ui.theme.PlTextTertiary
import com.example.ui.theme.PlYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchPointsBreakdownBottomSheet(
    fixture: FixtureEntity,
    prediction: PredictionEntity?,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val actualHome = fixture.homeScoreActual
    val actualAway = fixture.awayScoreActual
    val predHome = prediction?.homeScorePred
    val predAway = prediction?.awayScorePred

    val scoring: ScoringResult? = if (actualHome != null && actualAway != null && (predHome != null || prediction?.homeScorePredB != null)) {
        MatchScoringEngine.calculatePoints(
            predHome = predHome,
            predAway = predAway,
            actualHome = actualHome,
            actualAway = actualAway,
            isCaptain = prediction?.isCaptain == true,
            isSuperCaptain = prediction?.isSuperCaptain == true,
            isSafetyNet = prediction?.isSafetyNet == true,
            isDoubleShot = prediction?.isDoubleShot == true,
            predHomeB = prediction?.homeScorePredB,
            predAwayB = prediction?.awayScorePredB
        )
    } else null

    val pointsEarned = scoring?.totalPoints ?: prediction?.pointsEarned ?: 0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PlPlumCard,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.3f)
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
                .testTag("dialog_match_breakdown_sheet")
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MATCH SCORING BREAKDOWN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        color = PlCyan
                    )
                    Text(
                        text = "${fixture.homeTeam} vs ${fixture.awayTeam}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_match_breakdown")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Match Breakdown",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Score Comparison Card: Predicted vs Actual
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = PlPlumCardElevated,
                border = BorderStroke(1.dp, PlPlumBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home Team
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        TeamCrestBadge(
                            code = fixture.homeCode,
                            teamName = fixture.homeTeam,
                            isDarkText = false
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = fixture.homeTeam,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }

                    // Scoreboard center
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (fixture.isFinished) PlMint.copy(alpha = 0.2f) else PlPlumBackground
                        ) {
                            Text(
                                text = if (fixture.isFinished) "FINAL RESULT" else "MATCH PENDING",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (fixture.isFinished) PlMint else PlTextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Actual Score
                        Text(
                            text = if (actualHome != null && actualAway != null) "$actualHome - $actualAway" else "TBD",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Prediction
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = if (predHome != null && predAway != null) "Your Pred: $predHome - $predAway" else "No Prediction",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (predHome != null) PlMint else PlTextTertiary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Away Team
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        TeamCrestBadge(
                            code = fixture.awayCode,
                            teamName = fixture.awayTeam,
                            isDarkText = false
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = fixture.awayTeam,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total Points Hero Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = when {
                    pointsEarned >= 6 -> PlMint.copy(alpha = 0.2f)
                    pointsEarned > 0 -> PlCyan.copy(alpha = 0.18f)
                    else -> Color.White.copy(alpha = 0.05f)
                },
                border = BorderStroke(
                    1.dp,
                    when {
                        pointsEarned >= 6 -> PlMint
                        pointsEarned > 0 -> PlCyan
                        else -> PlPlumBorder
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL POINTS AWARDED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = PlTextSecondary
                        )
                        Text(
                            text = if (scoring != null) scoring.breakdownText else "Match not yet completed",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "+$pointsEarned",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = when {
                                pointsEarned >= 4 -> PlMint
                                pointsEarned > 0 -> PlCyan
                                else -> PlTextTertiary
                            }
                        )
                        Text(
                            text = " PTS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Itemized Earned Badges & Calculation Breakdown
            Text(
                text = "EARNED BADGES & STEP-BY-STEP CALCULATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = PlTextTertiary
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (scoring != null) {
                // 1. Exact Score or Outcome
                if (scoring.isExactScore) {
                    BreakdownItemRow(
                        icon = Icons.Default.CheckCircle,
                        iconTint = PlMint,
                        title = "Exact Score Badge",
                        description = "Accurately predicted $predHome-$predAway scoreline",
                        pointsAwardedText = "+3 pts",
                        isEarned = true
                    )
                } else if (scoring.isCorrectOutcome) {
                    BreakdownItemRow(
                        icon = Icons.Default.Star,
                        iconTint = PlYellow,
                        title = "Correct Outcome Badge",
                        description = "Correct match winner or draw result",
                        pointsAwardedText = "+1 pt",
                        isEarned = true
                    )
                } else {
                    BreakdownItemRow(
                        icon = Icons.Default.Close,
                        iconTint = PlPink,
                        title = "Incorrect Result",
                        description = "Neither outcome nor scoreline matched",
                        pointsAwardedText = "+0 pts",
                        isEarned = false
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 2. Clean Sheet Bonus
                if (scoring.cleanSheetBonus > 0) {
                    BreakdownItemRow(
                        icon = Icons.Default.Shield,
                        iconTint = PlCyan,
                        title = if (scoring.cleanSheetBonus == 2) "Both Clean Sheets Bonus" else "Win to Nil Clean Sheet Bonus",
                        description = if (scoring.cleanSheetBonus == 2) "Exact 0-0 draw bonus (+2 pts)" else "Exact shutout win bonus (+1 pt)",
                        pointsAwardedText = "+${scoring.cleanSheetBonus} pts",
                        isEarned = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // 3. Multipliers (Super Captain or Captain)
                if (prediction?.isSuperCaptain == true) {
                    BreakdownItemRow(
                        icon = Icons.Default.Bolt,
                        iconTint = PlMint,
                        title = "Super Captain Chip Active",
                        description = "Match points tripled (Base: ${scoring.basePoints + scoring.cleanSheetBonus} x 3)",
                        pointsAwardedText = "x3 Multiplier",
                        isEarned = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                } else if (prediction?.isCaptain == true) {
                    BreakdownItemRow(
                        icon = Icons.Default.Star,
                        iconTint = PlYellow,
                        title = "Captain Pick Active",
                        description = "Match points doubled (Base: ${scoring.basePoints + scoring.cleanSheetBonus} x 2)",
                        pointsAwardedText = "x2 Multiplier",
                        isEarned = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // 4. Safety Net Chip
                if (prediction?.isSafetyNet == true) {
                    BreakdownItemRow(
                        icon = Icons.Default.Security,
                        iconTint = PlCyan,
                        title = "Safety Net Chip Active",
                        description = if (scoring.safetyNetApplied) "Zero base score protected! Awarded guaranteed points" else "Not needed (scored > 0 pts)",
                        pointsAwardedText = if (scoring.safetyNetApplied) "+3 pts" else "Active",
                        isEarned = scoring.safetyNetApplied
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // 5. Double Shot Chip
                if (prediction?.isDoubleShot == true) {
                    BreakdownItemRow(
                        icon = Icons.Default.Star,
                        iconTint = PlMint,
                        title = "Double Shot Chip Active",
                        description = scoring.doubleShotBestPrediction?.let { "Evaluated both predictions: $it" } ?: "Secondary prediction submitted",
                        pointsAwardedText = "Best Score",
                        isEarned = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // 6. Auto Captain Chip
                if (prediction?.isAutoCaptain == true) {
                    BreakdownItemRow(
                        icon = Icons.Default.Bolt,
                        iconTint = PlYellow,
                        title = "Auto Captain Chip Active",
                        description = "Gameweek highest-scoring match dynamically selected for 2x multiplier",
                        pointsAwardedText = "Auto 2x",
                        isEarned = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = PlPlumCardElevated,
                    border = BorderStroke(1.dp, PlPlumBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsSoccer,
                            contentDescription = null,
                            tint = PlCyan,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Fixture Not Yet Completed",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Scoring breakdown will be automatically calculated once this fixture finishes.",
                            fontSize = 11.sp,
                            color = PlTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_dismiss_match_breakdown"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PlCyan,
                    contentColor = PlPlumBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Close",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BreakdownItemRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    pointsAwardedText: String,
    isEarned: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = PlPlumCardElevated,
        border = BorderStroke(
            1.dp,
            if (isEarned) iconTint.copy(alpha = 0.5f) else PlPlumBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = description,
                        fontSize = 10.sp,
                        color = PlTextSecondary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isEarned) iconTint.copy(alpha = 0.2f) else PlPlumBackground
            ) {
                Text(
                    text = pointsAwardedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isEarned) iconTint else PlTextTertiary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
