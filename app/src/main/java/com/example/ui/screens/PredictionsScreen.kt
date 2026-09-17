package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Filter2
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.local.entity.ChipEntity
import com.example.data.local.entity.FixtureEntity
import com.example.data.local.entity.GameweekEntity
import com.example.data.local.entity.PredictionEntity
import com.example.data.repository.FplSyncState
import com.example.ui.components.ChipPillType
import com.example.ui.components.ChipStatusPill
import com.example.ui.components.CommunityConsensusBar
import com.example.ui.components.TacticalChipsHub
import com.example.ui.components.TeamCrestBadge
import com.example.ui.theme.PlCardDarkBorder
import com.example.ui.theme.PlCardDarkFrosted
import com.example.ui.theme.PlCardWhite
import com.example.ui.theme.PlCyan
import com.example.ui.theme.PlError
import com.example.ui.theme.PlMint
import com.example.ui.theme.PlPink
import com.example.ui.theme.PlPlumBackground
import com.example.ui.theme.PlPlumBorder
import com.example.ui.theme.PlPlumCard
import com.example.ui.theme.PlPlumCardElevated
import com.example.ui.theme.PlScoreBgWhite
import com.example.ui.theme.PlScoreBorderWhite
import com.example.ui.theme.PlTextOnWhite
import com.example.ui.theme.PlTextPrimary
import com.example.ui.theme.PlTextSecondary
import com.example.ui.theme.PlTextTertiary
import com.example.ui.theme.PlYellow
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionsScreen(
    fixtures: List<FixtureEntity>,
    predictions: Map<String, PredictionEntity>,
    chips: List<ChipEntity>,
    isDeadlineLocked: Boolean,
    currentGwNumber: Int,
    gameweek: GameweekEntity? = null,
    isRefreshing: Boolean = false,
    fplSyncState: FplSyncState = FplSyncState.Idle,
    targetSelectionChip: String? = null,
    onRefresh: () -> Unit = {},
    onScoreChanged: (fixtureId: String, home: Int?, away: Int?) -> Unit,
    onScoreChangedB: (fixtureId: String, homeB: Int?, awayB: Int?) -> Unit = { _, _, _ -> },
    onCaptainToggled: (fixtureId: String) -> Unit,
    onChipClicked: (chipType: String) -> Unit = {},
    onFixtureSelectedForChip: (fixtureId: String) -> Unit = {},
    onCancelTargetSelection: () -> Unit = {},
    onSubmitPredictions: () -> Unit = {},
    onOpenRules: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentHalf = if (currentGwNumber <= 19) 1 else 2
    val superCaptainChip = chips.find { it.chipType == "SUPER_CAPTAIN" && it.half == currentHalf }
    val safetyNetChip = chips.find { it.chipType == "SAFETY_NET" && it.half == currentHalf }
    val autoCaptainChip = chips.find { it.chipType == "AUTO_CAPTAIN" && it.half == currentHalf }
    val doubleShotChip = chips.find { it.chipType == "DOUBLE_SHOT" && it.half == currentHalf }

    val activeSuperCaptainFixId = predictions.values.find { it.isSuperCaptain }?.fixtureId
    val activeSafetyNetFixId = predictions.values.find { it.isSafetyNet }?.fixtureId
    val isAutoCaptainActive = predictions.values.any { it.isAutoCaptain }
    val activeDoubleShotFixId = predictions.values.find { it.isDoubleShot }?.fixtureId

    val activeChipName: String? = when {
        activeSuperCaptainFixId != null -> "SUPER_CAPTAIN"
        activeSafetyNetFixId != null -> "SAFETY_NET"
        isAutoCaptainActive -> "AUTO_CAPTAIN"
        activeDoubleShotFixId != null -> "DOUBLE_SHOT"
        else -> null
    }

    val captainFixture = fixtures.find { fix ->
        val pred = predictions[fix.id]
        pred?.isCaptain == true || pred?.isSuperCaptain == true
    }

    val dateRangeText = if (fixtures.isNotEmpty()) {
        val minKickoff = fixtures.minOf { it.kickoffEpochMs }
        val maxKickoff = fixtures.maxOf { it.kickoffEpochMs }
        val zoneId = ZoneId.systemDefault()
        val minZdt = Instant.ofEpochMilli(minKickoff).atZone(zoneId)
        val maxZdt = Instant.ofEpochMilli(maxKickoff).atZone(zoneId)
        val dtf = DateTimeFormatter.ofPattern("EEE d MMM", Locale.getDefault())
        "${minZdt.format(dtf)} - ${maxZdt.format(dtf)}"
    } else {
        "Upcoming Round"
    }

    val deadlineText = gameweek?.let {
        val zoneId = ZoneId.systemDefault()
        val dZdt = Instant.ofEpochMilli(it.deadlineEpochMs).atZone(zoneId)
        val dtf = DateTimeFormatter.ofPattern("EEE d MMM, HH:mm", Locale.getDefault())
        "Deadline: ${dZdt.format(dtf)}"
    } ?: "Deadline: Prior to Kickoff"

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .background(PlPlumBackground)
            .testTag("pull_to_refresh_fixtures")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Loading progress banner when syncing with FPL API
            if (isRefreshing || fplSyncState is FplSyncState.Loading) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PlPlumCardElevated,
                        border = BorderStroke(1.dp, PlCyan.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("banner_syncing_fpl")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        color = PlCyan,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Syncing with Official Premier League API...",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PlCyan
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = PlMint,
                                trackColor = PlPlumBackground
                            )
                        }
                    }
                }
            }

            // Error banner with Retry button if offline or API failed
            if (fplSyncState is FplSyncState.Error) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PlPlumCardElevated,
                        border = BorderStroke(1.dp, PlPink.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("banner_fpl_error")
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = "Offline / Connection Error",
                                    tint = PlPink,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (fplSyncState.hasCachedData) "Offline Mode" else "Sync Failed",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PlPink
                                    )
                                    Text(
                                        text = fplSyncState.message,
                                        fontSize = 11.sp,
                                        color = PlTextSecondary,
                                        maxLines = 2
                                    )
                                }
                            }

                            if (fplSyncState.canRetry) {
                                Spacer(modifier = Modifier.width(8.dp))
                                ElevatedButton(
                                    onClick = onRefresh,
                                    colors = ButtonDefaults.elevatedButtonColors(
                                        containerColor = PlPink,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_retry_fpl_sync")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Retry Sync",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Retry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Gameweek Schedule Header Card (Dynamic from Official FPL API)
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PlPlumCard,
                    border = BorderStroke(1.dp, PlPlumBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_gw4_schedule_header")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = gameweek?.name ?: "Gameweek $currentGwNumber",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )

                            // Tag for Blank or Double Gameweek
                            val countType = gameweek?.fixtureCountType ?: "STANDARD"
                            if (countType == "DOUBLE") {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PlPink,
                                    modifier = Modifier.testTag("badge_dgw_header")
                                ) {
                                    Text(
                                        text = "DOUBLE GW (${fixtures.size})",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else if (countType == "BLANK") {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PlYellow,
                                    modifier = Modifier.testTag("badge_blank_header")
                                ) {
                                    Text(
                                        text = "BLANK GW (${fixtures.size})",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dateRangeText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = PlTextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = PlPlumCardElevated,
                            border = BorderStroke(1.dp, PlCyan.copy(alpha = 0.35f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = PlCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = deadlineText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PlCyan
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(PlMint)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Official FPL Live Sync • Swipe to refresh",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PlMint
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            onClick = onOpenRules,
                            shape = RoundedCornerShape(20.dp),
                            color = PlPlumCardElevated,
                            border = BorderStroke(1.dp, PlYellow.copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("btn_scoring_rules_predictions")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "Rules",
                                    tint = PlYellow,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Rules & Point Breakdown",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PlYellow
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "*All kickoff times are displayed in your local time zone",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Empty / Loading state placeholder cards
            if (fixtures.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PlPlumCard,
                        border = BorderStroke(1.dp, PlPlumBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isRefreshing || fplSyncState is FplSyncState.Loading) {
                                CircularProgressIndicator(
                                    color = PlCyan,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Fetching Premier League fixtures...",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Connecting to Fantasy Premier League public API",
                                    fontSize = 12.sp,
                                    color = PlTextSecondary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = null,
                                    tint = PlPink,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No fixtures available yet",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Pull down or tap below to load upcoming matches.",
                                    fontSize = 12.sp,
                                    color = PlTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                ElevatedButton(
                                    onClick = onRefresh,
                                    colors = ButtonDefaults.elevatedButtonColors(
                                        containerColor = PlCyan,
                                        contentColor = PlPlumBackground
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Sync Fixtures Now", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Compact 4-Chip Tactical Hub (Single row showing all 4 chips side-by-side)
            item {
                TacticalChipsHub(
                    currentHalf = currentHalf,
                    superCaptainChip = superCaptainChip,
                    safetyNetChip = safetyNetChip,
                    autoCaptainChip = autoCaptainChip,
                    doubleShotChip = doubleShotChip,
                    isSuperCaptainActive = activeSuperCaptainFixId != null,
                    isSafetyNetActive = activeSafetyNetFixId != null,
                    isAutoCaptainActive = isAutoCaptainActive,
                    isDoubleShotActive = activeDoubleShotFixId != null,
                    isDeadlineLocked = isDeadlineLocked,
                    onChipClicked = onChipClicked
                )
            }

            // Target Selection Mode Guidance Banner
            if (targetSelectionChip != null) {
                item {
                    val chipLabel = if (targetSelectionChip == "SAFETY_NET") "Safety Net (+3 PTS Floor)" else "Double Shot (Dual Predictions)"
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .testTag("banner_target_selection"),
                        color = Color(0xFF1E0A24),
                        border = BorderStroke(1.5.dp, PlCyan),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(PlCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TouchApp,
                                        contentDescription = "Select fixture",
                                        tint = PlCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "CHOOSE TARGET FIXTURE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp,
                                        color = PlCyan
                                    )
                                    Text(
                                        text = "Tap any match card below to activate $chipLabel.",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }
                            IconButton(
                                onClick = onCancelTargetSelection,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("btn_cancel_target_selection")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

        // Locked Alert if deadline passed
        if (isDeadlineLocked) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PlError.copy(alpha = 0.15f))
                        .border(1.dp, PlError.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = PlError,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Predictions Locked",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlError
                            )
                            Text(
                                text = "Gameweek is underway. You can view points in the 'Points' tab.",
                                fontSize = 11.sp,
                                color = PlTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Fixtures List (Featured 1st card in Editorial White, subsequent cards in Editorial Frosted Glass)
        itemsIndexed(fixtures, key = { _, fixture -> fixture.id }) { index, fixture ->
            val pred = predictions[fixture.id]
            val isThisCaptain = pred?.isCaptain == true
            val isThisSuperCaptain = pred?.isSuperCaptain == true
            val isThisSafetyNet = pred?.isSafetyNet == true
            val isThisDoubleShot = pred?.isDoubleShot == true

            FixturePredictionCard(
                fixture = fixture,
                predHome = pred?.homeScorePred,
                predAway = pred?.awayScorePred,
                isCaptain = isThisCaptain,
                isSuperCaptain = isThisSuperCaptain,
                isSafetyNet = isThisSafetyNet,
                isDoubleShot = isThisDoubleShot,
                predHomeB = pred?.homeScorePredB,
                predAwayB = pred?.awayScorePredB,
                isAutoCaptainActive = isAutoCaptainActive,
                isSuperCaptainChipActive = (activeChipName == "SUPER_CAPTAIN"),
                isTargetSelectionMode = (targetSelectionChip != null),
                isLocked = isDeadlineLocked,
                isWhiteCard = (index == 0),
                onScoreChange = { home, away -> onScoreChanged(fixture.id, home, away) },
                onScoreChangeB = { homeB, awayB -> onScoreChangedB(fixture.id, homeB, awayB) },
                onCaptainClick = { onCaptainToggled(fixture.id) },
                onCardClick = {
                    if (targetSelectionChip != null) {
                        onFixtureSelectedForChip(fixture.id)
                    }
                }
            )
        }

        // Bottom Submit Predictions Bar
        item {
            val predictedCount = predictions.values.count { it.homeScorePred != null && it.awayScorePred != null }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("submit_predictions_panel"),
                color = PlPlumCardElevated,
                border = BorderStroke(1.dp, PlPlumBorder),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$predictedCount of ${fixtures.size} Predictions Made",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = when {
                                    isAutoCaptainActive -> "Captain: Auto Captain (2x highest match)"
                                    captainFixture != null -> "Captain: ${captainFixture.homeCode} vs ${captainFixture.awayCode} ${if (activeChipName == "SUPER_CAPTAIN") "(3x Super Captain)" else "(2x)"}"
                                    else -> "Captain: Not selected yet (Tap 'C')"
                                },
                                fontSize = 11.sp,
                                color = if (isAutoCaptainActive || captainFixture != null) PlMint else PlYellow
                            )
                        }

                        if (isAutoCaptainActive || captainFixture != null) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(PlMint.copy(alpha = 0.2f))
                                    .padding(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Captain Ready",
                                    tint = PlMint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onSubmitPredictions,
                        enabled = !isDeadlineLocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PlMint,
                            contentColor = PlPlumBackground,
                            disabledContainerColor = Color.White.copy(alpha = 0.12f),
                            disabledContentColor = Color.White.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_submit_predictions")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isDeadlineLocked) "PREDICTIONS LOCKED" else "SUBMIT PREDICTIONS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
private fun EditorialChipCard(
    title: String,
    shortCode: String,
    perk: String,
    isUsed: Boolean,
    usedGw: Int?,
    isActiveThisGw: Boolean,
    badgeColor: Color,
    isLocked: Boolean = false,
    testTag: String = "",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isFaded = isUsed && !isActiveThisGw

    Box(
        modifier = modifier
            .width(155.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                when {
                    isActiveThisGw -> PlMint.copy(alpha = 0.15f)
                    isFaded -> Color.White.copy(alpha = 0.04f)
                    else -> Color.White.copy(alpha = 0.08f)
                }
            )
            .border(
                width = if (isActiveThisGw) 1.5.dp else 1.dp,
                color = when {
                    isActiveThisGw -> PlMint
                    isFaded -> Color.White.copy(alpha = 0.05f)
                    else -> Color.White.copy(alpha = 0.12f)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = !isLocked && (!isUsed || isActiveThisGw), onClick = onClick)
            .padding(10.dp)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Circle Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActiveThisGw -> PlMint
                            isUsed -> Color.White.copy(alpha = 0.20f)
                            else -> badgeColor
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = shortCode,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = when {
                        isActiveThisGw -> Color(0xFF38003C)
                        isUsed -> Color.White.copy(alpha = 0.45f)
                        else -> Color(0xFF38003C)
                    }
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = perk,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isActiveThisGw) PlMint else PlCyan
                )
                Text(
                    text = when {
                        isActiveThisGw -> "ACTIVE (GW)"
                        isUsed -> "USED (GW$usedGw)"
                        else -> "AVAILABLE"
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.2).sp,
                    color = when {
                        isActiveThisGw -> PlMint
                        isUsed -> Color.White.copy(alpha = 0.4f)
                        else -> Color.White.copy(alpha = 0.7f)
                    }
                )
            }
        }
    }
}

@Composable
fun FixturePredictionCard(
    fixture: FixtureEntity,
    predHome: Int?,
    predAway: Int?,
    isCaptain: Boolean,
    isSuperCaptain: Boolean,
    isSafetyNet: Boolean,
    isDoubleShot: Boolean = false,
    predHomeB: Int? = null,
    predAwayB: Int? = null,
    isAutoCaptainActive: Boolean = false,
    isSuperCaptainChipActive: Boolean = false,
    isTargetSelectionMode: Boolean = false,
    isLocked: Boolean,
    isWhiteCard: Boolean = false,
    onScoreChange: (home: Int?, away: Int?) -> Unit,
    onScoreChangeB: (home: Int?, away: Int?) -> Unit = { _, _ -> },
    onCaptainClick: () -> Unit,
    onCardClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cardBackground = if (isWhiteCard) PlCardWhite else PlCardDarkFrosted
    val cardTextColor = if (isWhiteCard) PlTextOnWhite else Color.White
    val cardBorder = when {
        isTargetSelectionMode -> PlCyan
        isWhiteCard -> Color.Transparent
        else -> PlCardDarkBorder
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = if (isTargetSelectionMode) 2.dp else (if (isWhiteCard) 0.dp else 1.dp),
                color = cardBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(enabled = isTargetSelectionMode, onClick = onCardClick)
            .testTag("fixture_card_${fixture.id}"),
        color = cardBackground,
        shadowElevation = if (isWhiteCard) 8.dp else 0.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Target Selection Helper Pill if in Selection Mode
            if (isTargetSelectionMode) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PlCyan.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, PlCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = PlCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TAP THIS MATCH TO APPLY CHIP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = PlCyan,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Match Subtitle: Kickoff Time & Optional DGW / Chip Status Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isWhiteCard) Color(0xFF38003C).copy(alpha = 0.06f)
                                else Color.White.copy(alpha = 0.10f)
                            )
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = fixture.kickoffTimeDisplay,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isWhiteCard) Color(0xFF38003C).copy(alpha = 0.7f) else Color.White.copy(alpha = 0.7f)
                        )
                    }

                    if (fixture.isDoubleGameweekExtra) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PlPink.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "DGW EXTRA",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlPink
                            )
                        }
                    }
                }

                // Active Power-Up Pills
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (isSuperCaptain) {
                        ChipStatusPill(type = ChipPillType.SUPER_CAPTAIN)
                    } else if (isCaptain) {
                        ChipStatusPill(type = ChipPillType.CAPTAIN)
                    }
                    if (isSafetyNet) {
                        ChipStatusPill(type = ChipPillType.SAFETY_NET)
                    }
                    if (isDoubleShot) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PlMint.copy(alpha = 0.2f))
                                .border(1.dp, PlMint.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "DOUBLE SHOT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = PlMint
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Score Prediction Row (Prediction A): Home Crest - Inputs - Away Crest
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Club Badge & Name
                TeamCrestBadge(
                    code = fixture.homeCode,
                    teamName = fixture.homeTeam,
                    isDarkText = isWhiteCard,
                    modifier = Modifier.weight(1f)
                )

                // Score Steppers (Prediction A)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 6.dp)
                ) {
                    ScoreStepper(
                        score = predHome,
                        isLocked = isLocked,
                        fixtureId = fixture.id,
                        side = "home",
                        isWhiteCard = isWhiteCard,
                        onScoreChanged = { newScore -> onScoreChange(newScore, predAway ?: 0) }
                    )

                    Text(
                        text = "-",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isWhiteCard) Color(0xFF38003C).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    ScoreStepper(
                        score = predAway,
                        isLocked = isLocked,
                        fixtureId = fixture.id,
                        side = "away",
                        isWhiteCard = isWhiteCard,
                        onScoreChanged = { newScore -> onScoreChange(predHome ?: 0, newScore) }
                    )
                }

                // Away Club Badge & Name
                TeamCrestBadge(
                    code = fixture.awayCode,
                    teamName = fixture.awayTeam,
                    isDarkText = isWhiteCard,
                    modifier = Modifier.weight(1f)
                )
            }

            // If Double Shot is active on this fixture: render Prediction B row!
            if (isDoubleShot) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                    color = Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, PlMint.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "PREDICTION B (DOUBLE SHOT)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = PlMint
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            ScoreStepper(
                                score = predHomeB,
                                isLocked = isLocked,
                                fixtureId = "${fixture.id}_b",
                                side = "home_b",
                                isWhiteCard = isWhiteCard,
                                onScoreChanged = { newScore -> onScoreChangeB(newScore, predAwayB ?: 0) }
                            )

                            Text(
                                text = "-",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            ScoreStepper(
                                score = predAwayB,
                                isLocked = isLocked,
                                fixtureId = "${fixture.id}_b",
                                side = "away_b",
                                isWhiteCard = isWhiteCard,
                                onScoreChanged = { newScore -> onScoreChangeB(predHomeB ?: 0, newScore) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        if (isWhiteCard) Color(0xFF38003C).copy(alpha = 0.08f)
                        else Color.White.copy(alpha = 0.08f)
                    )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Captain Button & Community Consensus
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Captain Action
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isAutoCaptainActive) {
                        // Auto Captain is active: display AC pill locked
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = PlCyan.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, PlCyan)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PlCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AUTO (2x TOP)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PlCyan
                                )
                            }
                        }
                    } else {
                        // Manual Captain selection with strict mutual exclusivity
                        val isEffectiveSuper = isSuperCaptain || (isCaptain && isSuperCaptainChipActive)
                        EditorialCircleButton(
                            label = if (isEffectiveSuper) "SC" else "C",
                            isSelected = isCaptain || isSuperCaptain,
                            selectedBg = if (isEffectiveSuper) PlPink else (if (isWhiteCard) Color(0xFF38003C) else PlMint),
                            selectedText = if (isEffectiveSuper) Color.White else (if (isWhiteCard) PlMint else Color(0xFF38003C)),
                            isWhiteCard = isWhiteCard,
                            enabled = !isLocked,
                            testTag = "btn_captain_${fixture.id}",
                            onClick = onCaptainClick
                        )

                        Text(
                            text = if (isCaptain || isSuperCaptain) (if (isEffectiveSuper) "SUPER CAPTAIN (3x)" else "CAPTAIN (2x)") else "MAKE CAPTAIN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCaptain || isSuperCaptain) (if (isEffectiveSuper) PlPink else PlMint) else cardTextColor.copy(alpha = 0.5f)
                        )
                    }
                }

                // Community Consensus Bar
                Box(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    CommunityConsensusBar(
                        homeWinPct = fixture.homeWinPct,
                        drawPct = fixture.drawPct,
                        awayWinPct = fixture.awayWinPct,
                        homeCode = fixture.homeCode,
                        awayCode = fixture.awayCode,
                        isDarkCard = !isWhiteCard
                    )
                }
            }
        }
    }
}

@Composable
private fun EditorialCircleButton(
    label: String,
    isSelected: Boolean,
    selectedBg: Color,
    selectedText: Color,
    isWhiteCard: Boolean,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val unselectedBg = if (isWhiteCard) Color(0xFFF1F5F9) else Color.White.copy(alpha = 0.10f)
    val unselectedText = if (isWhiteCard) Color(0xFF38003C).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.35f)

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isSelected) selectedBg else unselectedBg)
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = if (label.length > 1) 11.sp else 13.sp,
            fontWeight = FontWeight.Black,
            color = if (isSelected) selectedText else unselectedText
        )
    }
}

@Composable
fun ScoreStepper(
    score: Int?,
    isLocked: Boolean,
    fixtureId: String,
    side: String,
    isWhiteCard: Boolean = false,
    onScoreChanged: (Int) -> Unit
) {
    val current = score ?: 0
    val scoreBg = if (isWhiteCard) PlScoreBgWhite else Color.White.copy(alpha = 0.08f)
    val scoreBorder = if (isWhiteCard) PlScoreBorderWhite else Color.White.copy(alpha = 0.12f)
    val scoreTextColor = if (isWhiteCard) Color(0xFF38003C) else Color.White

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Minus Button
        IconButton(
            onClick = { if (!isLocked && current > 0) onScoreChanged(current - 1) },
            enabled = !isLocked && current > 0,
            modifier = Modifier
                .size(28.dp)
                .testTag("btn_minus_${side}_$fixtureId")
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease $side score",
                tint = if (!isLocked && current > 0) scoreTextColor else scoreTextColor.copy(alpha = 0.3f),
                modifier = Modifier.size(14.dp)
            )
        }

        // Score Input Box
        Box(
            modifier = Modifier
                .size(width = 38.dp, height = 44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(scoreBg)
                .border(2.dp, scoreBorder, RoundedCornerShape(12.dp))
                .testTag("text_score_${side}_$fixtureId"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = score?.toString() ?: "-",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = scoreTextColor
            )
        }

        // Plus Button
        IconButton(
            onClick = { if (!isLocked && current < 9) onScoreChanged(current + 1) },
            enabled = !isLocked && current < 9,
            modifier = Modifier
                .size(28.dp)
                .testTag("btn_plus_${side}_$fixtureId")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase $side score",
                tint = if (!isLocked && current < 9) (if (isWhiteCard) Color(0xFF38003C) else PlMint) else scoreTextColor.copy(alpha = 0.3f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

