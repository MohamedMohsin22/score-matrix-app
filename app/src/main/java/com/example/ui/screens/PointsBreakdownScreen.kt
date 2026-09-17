package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Picture
import kotlinx.coroutines.launch
import com.example.data.local.entity.FixtureEntity
import com.example.data.local.entity.PredictionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.MatchScoringEngine
import com.example.ui.components.ChipPillType
import com.example.ui.components.ChipStatusPill
import com.example.ui.components.GameweekSummaryCard
import com.example.ui.components.MatchPointsBreakdownBottomSheet
import com.example.ui.components.RulesHelpBottomSheet
import com.example.ui.components.ShareHelper
import com.example.ui.components.TeamCrestBadge
import com.example.ui.theme.PlCardDarkBorder
import com.example.ui.theme.PlCardDarkFrosted
import com.example.ui.theme.PlCyan
import com.example.ui.theme.PlError
import com.example.ui.theme.PlMint
import com.example.ui.theme.PlPink
import com.example.ui.theme.PlPlumBackground
import com.example.ui.theme.PlPlumBorder
import com.example.ui.theme.PlPlumCard
import com.example.ui.theme.PlPlumCardElevated
import com.example.ui.theme.PlTextPrimary
import com.example.ui.theme.PlTextSecondary
import com.example.ui.theme.PlTextTertiary
import com.example.ui.theme.PlWarning
import com.example.ui.theme.PlYellow

@Composable
fun PointsBreakdownScreen(
    fixtures: List<FixtureEntity>,
    predictions: Map<String, PredictionEntity>,
    currentUser: UserEntity?,
    gwNumber: Int,
    onSelectGw: (Int) -> Unit = {},
    onOpenRules: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFixtureForDetail by remember { mutableStateOf<FixtureEntity?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val picture = remember { Picture() }

    // Stats calculations
    var exactCount = 0
    var outcomeCount = 0
    var finishedCount = 0
    var computedPoints = 0
    var captainPoints = 0
    var captainMatchText: String? = null
    var chipPlayedText: String? = null

    fixtures.forEach { fix ->
        val pred = predictions[fix.id]
        if (fix.isFinished && fix.homeScoreActual != null && fix.awayScoreActual != null) {
            finishedCount++
            if ((pred?.homeScorePred != null && pred.awayScorePred != null) || (pred?.homeScorePredB != null && pred.awayScorePredB != null)) {
                val scoring = MatchScoringEngine.calculatePoints(
                    predHome = pred?.homeScorePred,
                    predAway = pred?.awayScorePred,
                    actualHome = fix.homeScoreActual,
                    actualAway = fix.awayScoreActual,
                    isCaptain = pred?.isCaptain == true,
                    isSuperCaptain = pred?.isSuperCaptain == true,
                    isSafetyNet = pred?.isSafetyNet == true,
                    isDoubleShot = pred?.isDoubleShot == true,
                    predHomeB = pred?.homeScorePredB,
                    predAwayB = pred?.awayScorePredB
                )
                computedPoints += scoring.totalPoints
                if (scoring.isExactScore) exactCount++
                else if (scoring.isCorrectOutcome) outcomeCount++

                if (pred?.isCaptain == true || pred?.isSuperCaptain == true || pred?.isAutoCaptain == true) {
                    captainPoints = scoring.totalPoints
                    captainMatchText = "${fix.homeCode} vs ${fix.awayCode}"
                }
            }
        }

        if (pred != null) {
            if (pred.isSuperCaptain) {
                chipPlayedText = "Super Captain (3x)"
            } else if (pred.isSafetyNet) {
                chipPlayedText = "Safety Net (+3 PTS)"
            } else if (pred.isAutoCaptain) {
                chipPlayedText = "Auto Captain (2x)"
            } else if (pred.isDoubleShot) {
                chipPlayedText = "Double Shot (Best of 2)"
            }
        }
    }

    val totalGwPoints = if (finishedCount > 0) computedPoints else (if (gwNumber == 3) (currentUser?.currentGwScore ?: 22) else 0)
    val totalSeasonPoints = currentUser?.totalScore ?: 68

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PlPlumBackground),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Gameweek Selector Toggle Pills (GW3 Finished vs GW4 Upcoming)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = { onSelectGw(3) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (gwNumber == 3) PlMint else PlPlumCardElevated,
                    border = BorderStroke(1.dp, if (gwNumber == 3) PlMint else PlPlumBorder),
                    modifier = Modifier.padding(end = 10.dp).testTag("tab_gw3_completed")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (gwNumber == 3) PlPlumBackground else PlMint)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GW 3 (Finished)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (gwNumber == 3) PlPlumBackground else Color.White
                        )
                    }
                }

                Surface(
                    onClick = { onSelectGw(4) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (gwNumber == 4) PlMint else PlPlumCardElevated,
                    border = BorderStroke(1.dp, if (gwNumber == 4) PlMint else PlPlumBorder),
                    modifier = Modifier.testTag("tab_gw4_upcoming")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (gwNumber == 4) PlPlumBackground else PlCyan)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GW 4 (Predicting)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (gwNumber == 4) PlPlumBackground else Color.White
                        )
                    }
                }
            }
        }

        // Top Gameweek Points Hero Card with Dark Neon Styling & Bitmap Export
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                // The Visual Gameweek Summary Card recorded into Picture for image export
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithCache {
                            val width = size.width.toInt().coerceAtLeast(1)
                            val height = size.height.toInt().coerceAtLeast(1)
                            onDrawWithContent {
                                val pictureCanvas = Canvas(picture.beginRecording(width, height))
                                draw(this, layoutDirection, pictureCanvas, size) {
                                    this@onDrawWithContent.drawContent()
                                }
                                picture.endRecording()
                                drawIntoCanvas { canvas ->
                                    canvas.nativeCanvas.drawPicture(picture)
                                }
                            }
                        }
                ) {
                    GameweekSummaryCard(
                        userName = currentUser?.username ?: "PremierMaster",
                        gwNumber = gwNumber,
                        gwPoints = totalGwPoints,
                        seasonTotalPoints = totalSeasonPoints,
                        exactScoresCount = exactCount,
                        correctOutcomesCount = outcomeCount,
                        captainPoints = captainPoints,
                        captainMatchText = captainMatchText,
                        chipsPlayedText = chipPlayedText,
                        miniLeagueRankText = "Rank #1 in Official Premier League",
                        overallRank = currentUser?.overallRank ?: 42
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Prominent "Share Gameweek" Button with Share Icon
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val bitmap = ShareHelper.createBitmapFromPicture(picture)
                                val uri = ShareHelper.saveBitmapToCache(
                                    context = context,
                                    bitmap = bitmap,
                                    fileName = "pl_gw${gwNumber}_summary.png"
                                )
                                val text = "🏆 Check out my Premier League Gameweek $gwNumber score: $totalGwPoints PTS! " +
                                        "🎯 $exactCount Exact Bullseye scores predicted. Can you beat my score?"
                                ShareHelper.shareSummaryCard(context, uri, text)
                            } catch (e: Exception) {
                                val text = "🏆 Check out my Premier League Gameweek $gwNumber score: $totalGwPoints PTS! " +
                                        "🎯 $exactCount Exact Bullseye scores predicted. Can you beat my score?"
                                ShareHelper.shareSummaryCard(context, null, text)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PlMint,
                        contentColor = PlPlumBackground
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_share_gameweek")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = PlPlumBackground,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share Gameweek $gwNumber Summary",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = PlPlumBackground
                    )
                }
            }
        }

        // Section Title + Rules Help Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ITEMIZED MATCH BREAKDOWNS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = PlTextTertiary
                )

                Surface(
                    onClick = onOpenRules,
                    shape = RoundedCornerShape(12.dp),
                    color = PlPlumCardElevated,
                    border = BorderStroke(1.dp, PlPlumBorder),
                    modifier = Modifier.testTag("btn_rules_breakdown_section")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Rules",
                            tint = PlYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Scoring Rules",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlYellow
                        )
                    }
                }
            }
        }

        // Match cards
        items(fixtures, key = { it.id }) { fixture ->
            val pred = predictions[fixture.id]
            MatchBreakdownCard(
                fixture = fixture,
                prediction = pred,
                onClick = {
                    selectedFixtureForDetail = fixture
                }
            )
        }
    }

    // Modal BottomSheet for Itemized Match Points Breakdown
    selectedFixtureForDetail?.let { fixture ->
        val pred = predictions[fixture.id]
        MatchPointsBreakdownBottomSheet(
            fixture = fixture,
            prediction = pred,
            onDismiss = { selectedFixtureForDetail = null }
        )
    }
}

@Composable
private fun StatCapsule(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.6f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun MatchBreakdownCard(
    fixture: FixtureEntity,
    prediction: PredictionEntity?,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val actualHome = fixture.homeScoreActual
    val actualAway = fixture.awayScoreActual
    val predHome = prediction?.homeScorePred
    val predAway = prediction?.awayScorePred

    val scoring = if (fixture.isFinished && actualHome != null && actualAway != null && ((predHome != null && predAway != null) || (prediction?.homeScorePredB != null && prediction.awayScorePredB != null))) {
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

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = when {
                    scoring?.isExactScore == true -> PlMint.copy(alpha = 0.6f)
                    scoring?.isCorrectOutcome == true -> PlCyan.copy(alpha = 0.5f)
                    else -> Color.White.copy(alpha = 0.08f)
                },
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("card_match_breakdown_${fixture.id}"),
        color = PlCardDarkFrosted
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Status badge & points earned
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (fixture.isFinished) PlMint.copy(alpha = 0.2f) else PlPlumCardElevated
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (fixture.isFinished) "FT • FINAL" else fixture.kickoffTimeDisplay,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (fixture.isFinished) PlMint else PlTextSecondary
                        )
                    }

                    if (prediction?.isSuperCaptain == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        ChipStatusPill(type = ChipPillType.SUPER_CAPTAIN)
                    } else if (prediction?.isCaptain == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        ChipStatusPill(type = ChipPillType.CAPTAIN)
                    } else if (prediction?.isAutoCaptain == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        ChipStatusPill(type = ChipPillType.AUTO_CAPTAIN)
                    }

                    if (prediction?.isSafetyNet == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        ChipStatusPill(type = ChipPillType.SAFETY_NET)
                    }

                    if (prediction?.isDoubleShot == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        ChipStatusPill(type = ChipPillType.DOUBLE_SHOT)
                    }
                }

                // Points Badge with Click Indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    pointsEarned >= 4 -> PlMint.copy(alpha = 0.25f)
                                    pointsEarned > 0 -> PlCyan.copy(alpha = 0.2f)
                                    else -> PlPlumCardElevated
                                }
                            )
                            .border(
                                1.dp,
                                when {
                                    pointsEarned >= 4 -> PlMint
                                    pointsEarned > 0 -> PlCyan
                                    else -> PlPlumBorder
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "+$pointsEarned PTS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = when {
                                pointsEarned >= 4 -> PlMint
                                pointsEarned > 0 -> PlCyan
                                else -> PlTextTertiary
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View Scoring Breakdown",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Teams & Score Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Team Crest Badge
                TeamCrestBadge(
                    code = fixture.homeCode,
                    teamName = fixture.homeTeam,
                    isDarkText = false,
                    modifier = Modifier.weight(1f)
                )

                // Scores comparison: Actual vs Prediction
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    // Actual Score
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.10f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (fixture.homeScoreActual != null) "${fixture.homeScoreActual}" else "-",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = " - ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        Text(
                            text = if (fixture.awayScoreActual != null) "${fixture.awayScoreActual}" else "-",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Predicted Score
                    if (prediction?.isDoubleShot == true && prediction.homeScorePredB != null) {
                        Text(
                            text = "A: ${predHome ?: 0}-${predAway ?: 0} | B: ${prediction.homeScorePredB}-${prediction.awayScorePredB}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PlMint
                        )
                    } else {
                        Text(
                            text = if (predHome != null && predAway != null) "Pred: $predHome - $predAway" else "No Prediction",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (predHome != null) PlMint else Color.White.copy(alpha = 0.4f)
                        )
                    }
                }

                // Away Team Crest Badge
                TeamCrestBadge(
                    code = fixture.awayCode,
                    teamName = fixture.awayTeam,
                    isDarkText = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Breakdown Itemization Text / Tags
            if (scoring != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PlPlumCardElevated)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                scoring.isExactScore -> Icons.Default.CheckCircle
                                scoring.safetyNetApplied -> Icons.Default.Security
                                scoring.isCorrectOutcome -> Icons.Default.Star
                                else -> Icons.Default.Close
                            },
                            contentDescription = "Breakdown Info",
                            tint = when {
                                scoring.isExactScore -> PlMint
                                scoring.safetyNetApplied -> PlCyan
                                scoring.isCorrectOutcome -> PlCyan
                                else -> PlTextTertiary
                            },
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = scoring.breakdownText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PlTextPrimary
                        )
                    }
                }
            } else if (!fixture.isFinished) {
                Text(
                    text = "Awaiting actual match kickoff to calculate points.",
                    fontSize = 11.sp,
                    color = PlTextTertiary
                )
            }
        }
    }
}
