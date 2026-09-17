package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FixtureEntity
import com.example.data.local.entity.GameweekEntity
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
import com.example.ui.theme.PlYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSimulatorSheet(
    sheetState: SheetState,
    gameweek: GameweekEntity?,
    fixtures: List<FixtureEntity>,
    isDeadlineLocked: Boolean,
    onDismiss: () -> Unit,
    onToggleDeadlineLock: (Boolean) -> Unit,
    onSimulateAllResults: () -> Unit,
    onRecalculateScores: () -> Unit,
    onForceSyncSeason: () -> Unit = {},
    onSwitchFixtureScenario: (String) -> Unit,
    onUpdateScore: (fixtureId: String, home: Int?, away: Int?, isFinished: Boolean) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PlPlumBackground,
        contentColor = PlTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Admin Drawer",
                        tint = PlCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Admin Simulator Drawer",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = PlTextPrimary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Admin Sheet",
                        tint = PlTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxHeight(0.85f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Quick Action Buttons Row
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PlPlumCard)
                            .border(1.dp, PlPlumBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "QUICK SIMULATION ACTIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlCyan,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 1-Click Simulate Realistic Results
                        Button(
                            onClick = onSimulateAllResults,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PlMint,
                                contentColor = PlPlumBackground
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_admin_simulate_all")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Simulate",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simulate Realistic Results & Recalculate", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Recalculate Points Button
                        Button(
                            onClick = onRecalculateScores,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PlPlumCardElevated,
                                contentColor = PlCyan
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, PlCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .testTag("btn_admin_recalculate")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = "Recalculate",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Recalculate All Points & League Standings", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Sync to 2026/2027 Season (GW 3 Finished, GW 4 Active)
                        Button(
                            onClick = onForceSyncSeason,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PlPlumCardElevated,
                                contentColor = PlMint
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, PlMint.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .testTag("btn_admin_sync_gw4")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync Season",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sync to Season: GW 3 Finished, Predict GW 4", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Deadline Lock Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isDeadlineLocked) "Deadline Passed (LOCKED)" else "Deadline Open",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDeadlineLocked) PlError else PlMint
                                )
                                Text(
                                    text = "Locks user predictions for Gameweek",
                                    fontSize = 11.sp,
                                    color = PlTextSecondary
                                )
                            }
                            Switch(
                                checked = isDeadlineLocked,
                                onCheckedChange = onToggleDeadlineLock,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PlError,
                                    checkedTrackColor = PlError.copy(alpha = 0.4f),
                                    uncheckedThumbColor = PlMint,
                                    uncheckedTrackColor = PlPlumCardElevated
                                ),
                                modifier = Modifier.testTag("switch_admin_deadline")
                            )
                        }
                    }
                }

                // Variable Fixture Scenarios (Standard 10, Double GW 12, Blank GW 8)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PlPlumCard)
                            .border(1.dp, PlPlumBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "DYNAMIC FIXTURE SCENARIOS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlYellow,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val currentType = gameweek?.fixtureCountType ?: "STANDARD"

                            Button(
                                onClick = { onSwitchFixtureScenario("STANDARD") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentType == "STANDARD") PlMint else PlPlumCardElevated,
                                    contentColor = if (currentType == "STANDARD") PlPlumBackground else PlTextPrimary
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f).testTag("btn_scenario_standard")
                            ) {
                                Text("Standard (10)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onSwitchFixtureScenario("DOUBLE") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentType == "DOUBLE") PlPink else PlPlumCardElevated,
                                    contentColor = if (currentType == "DOUBLE") Color.White else PlTextPrimary
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f).testTag("btn_scenario_double")
                            ) {
                                Text("Double (12)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onSwitchFixtureScenario("BLANK") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentType == "BLANK") PlYellow else PlPlumCardElevated,
                                    contentColor = if (currentType == "BLANK") Color.Black else PlTextPrimary
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f).testTag("btn_scenario_blank")
                            ) {
                                Text("Blank (8)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Fixture Score Input Editor
                item {
                    Text(
                        text = "MANUAL FIXTURE RESULT EDITOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlTextTertiary,
                        letterSpacing = 0.5.sp
                    )
                }

                items(fixtures, key = { it.id }) { fix ->
                    AdminFixtureItem(
                        fixture = fix,
                        onUpdateScore = { h, a, fin -> onUpdateScore(fix.id, h, a, fin) }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminFixtureItem(
    fixture: FixtureEntity,
    onUpdateScore: (home: Int?, away: Int?, isFinished: Boolean) -> Unit
) {
    var homeText by remember(fixture.homeScoreActual) {
        mutableStateOf(fixture.homeScoreActual?.toString() ?: "")
    }
    var awayText by remember(fixture.awayScoreActual) {
        mutableStateOf(fixture.awayScoreActual?.toString() ?: "")
    }
    var isFinished by remember(fixture.isFinished) {
        mutableStateOf(fixture.isFinished)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, PlPlumBorder, RoundedCornerShape(10.dp)),
        color = PlPlumCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Teams
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${fixture.homeCode} vs ${fixture.awayCode}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlTextPrimary
                )
                Text(
                    text = "${fixture.homeTeam} - ${fixture.awayTeam}",
                    fontSize = 10.sp,
                    color = PlTextSecondary,
                    maxLines = 1
                )
            }

            // Home Score
            OutlinedTextField(
                value = homeText,
                onValueChange = {
                    if (it.length <= 2 && (it.isEmpty() || it.all { c -> c.isDigit() })) {
                        homeText = it
                        val h = it.toIntOrNull()
                        val a = awayText.toIntOrNull()
                        onUpdateScore(h, a, isFinished)
                    }
                },
                modifier = Modifier
                    .width(46.dp)
                    .height(48.dp)
                    .testTag("admin_score_home_${fixture.id}"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PlTextPrimary,
                    unfocusedTextColor = PlTextPrimary,
                    focusedBorderColor = PlCyan,
                    unfocusedBorderColor = PlPlumBorder
                ),
                singleLine = true
            )

            Text(
                text = ":",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PlTextTertiary,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            // Away Score
            OutlinedTextField(
                value = awayText,
                onValueChange = {
                    if (it.length <= 2 && (it.isEmpty() || it.all { c -> c.isDigit() })) {
                        awayText = it
                        val h = homeText.toIntOrNull()
                        val a = it.toIntOrNull()
                        onUpdateScore(h, a, isFinished)
                    }
                },
                modifier = Modifier
                    .width(46.dp)
                    .height(48.dp)
                    .testTag("admin_score_away_${fixture.id}"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PlTextPrimary,
                    unfocusedTextColor = PlTextPrimary,
                    focusedBorderColor = PlMint,
                    unfocusedBorderColor = PlPlumBorder
                ),
                singleLine = true
            )

            // Finished Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Checkbox(
                    checked = isFinished,
                    onCheckedChange = {
                        isFinished = it
                        val h = homeText.toIntOrNull()
                        val a = awayText.toIntOrNull()
                        onUpdateScore(h, a, it)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PlMint,
                        uncheckedColor = PlPlumBorder
                    ),
                    modifier = Modifier.testTag("admin_check_finished_${fixture.id}")
                )
                Text(
                    text = "FT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFinished) PlMint else PlTextTertiary
                )
            }
        }
    }
}
