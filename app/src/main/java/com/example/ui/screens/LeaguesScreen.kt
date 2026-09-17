package com.example.ui.screens

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.H2HMatchupEntity
import com.example.data.local.entity.LeagueEntity
import com.example.data.local.entity.LeagueMemberEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.ChipPillType
import com.example.ui.components.ChipStatusPill
import com.example.ui.components.ShareHelper
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

@Composable
fun LeaguesScreen(
    leagues: List<LeagueEntity>,
    selectedLeague: LeagueEntity?,
    classicStandings: List<LeagueMemberEntity>,
    h2hStandings: List<LeagueMemberEntity>,
    h2hMatchups: List<H2HMatchupEntity>,
    currentUser: UserEntity?,
    gwNumber: Int,
    isCreateDialogOpen: Boolean,
    isJoinDialogOpen: Boolean,
    onSelectLeague: (LeagueEntity) -> Unit,
    onOpenCreateDialog: () -> Unit,
    onCloseCreateDialog: () -> Unit,
    onOpenJoinDialog: () -> Unit,
    onCloseJoinDialog: () -> Unit,
    onCreateLeague: (name: String, type: String) -> Unit,
    onJoinLeague: (code: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var leagueTypeFilter by remember { mutableStateOf("ALL") } // ALL, CLASSIC, H2H
    var h2hSubTab by remember { mutableIntStateOf(0) } // 0 = Standings, 1 = GW Matchups

    val filteredLeagues = remember(leagues, leagueTypeFilter) {
        when (leagueTypeFilter) {
            "CLASSIC" -> leagues.filter { it.type == "CLASSIC" }
            "H2H" -> leagues.filter { it.type == "H2H" }
            else -> leagues
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PlPlumBackground),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Action Bar: Create & Join Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Create League Button
                Button(
                    onClick = onOpenCreateDialog,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PlMint,
                        contentColor = PlPlumBackground
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_create_league")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create League",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Create League",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Join League Button
                Button(
                    onClick = onOpenJoinDialog,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PlPlumCardElevated,
                        contentColor = PlCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .border(1.dp, PlCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .testTag("btn_join_league")
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Join League",
                        tint = PlCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Join With Code",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // League Type Selector Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("ALL", "All Leagues"),
                    Pair("CLASSIC", "Classic"),
                    Pair("H2H", "Head-to-Head")
                ).forEach { (type, label) ->
                    val isSelected = leagueTypeFilter == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PlPlumCardElevated else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) PlCyan else PlPlumBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { leagueTypeFilter = type }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PlCyan else PlTextSecondary
                        )
                    }
                }
            }
        }

        // Horizontal League Chips List
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredLeagues, key = { it.id }) { league ->
                    val isSelected = selectedLeague?.id == league.id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                if (isSelected) PlMint else PlPlumBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectLeague(league) }
                            .testTag("chip_league_${league.id}"),
                        color = if (isSelected) PlPlumCardElevated else PlPlumCard
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (league.type == "H2H") Icons.Default.SportsKabaddi else Icons.Default.EmojiEvents,
                                contentDescription = league.type,
                                tint = if (league.type == "H2H") PlPink else PlYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = league.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) PlMint else PlTextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (league.type == "H2H") "H2H Matchups" else "Classic Cumulative",
                                    fontSize = 10.sp,
                                    color = PlTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Selected League Details Card
        if (selectedLeague != null) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, PlPlumBorder, RoundedCornerShape(14.dp)),
                    color = PlPlumCard
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = selectedLeague.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PlTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (selectedLeague.type == "H2H") PlPink.copy(alpha = 0.2f)
                                                else PlYellow.copy(alpha = 0.2f)
                                            )
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = selectedLeague.type,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedLeague.type == "H2H") PlPink else PlYellow
                                        )
                                    }
                                }
                                Text(
                                    text = "Invite Code: ${selectedLeague.inviteCode}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PlCyan
                                )
                            }

                            // Share Invite Button (Deep link via Android Share Sheet)
                            IconButton(
                                onClick = {
                                    ShareHelper.shareLeagueInvite(
                                        context = context,
                                        leagueName = selectedLeague.name,
                                        inviteCode = selectedLeague.inviteCode
                                    )
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(PlPlumCardElevated)
                                    .border(1.dp, PlCyan.copy(alpha = 0.4f), CircleShape)
                                    .testTag("btn_share_league")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share League Deep Link",
                                    tint = PlCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // For H2H leagues: toggle between Standings table and H2H Matchups
                        if (selectedLeague.type == "H2H") {
                            Spacer(modifier = Modifier.height(10.dp))
                            TabRow(
                                selectedTabIndex = h2hSubTab,
                                containerColor = PlPlumCardElevated,
                                contentColor = PlCyan,
                                indicator = { tabPositions ->
                                    TabRowDefaults.SecondaryIndicator(
                                        modifier = Modifier.tabIndicatorOffset(tabPositions[h2hSubTab]),
                                        color = PlCyan
                                    )
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .height(36.dp)
                            ) {
                                Tab(
                                    selected = h2hSubTab == 0,
                                    onClick = { h2hSubTab = 0 },
                                    text = { Text("Standings Table", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = h2hSubTab == 1,
                                    onClick = { h2hSubTab = 1 },
                                    text = { Text("GW $gwNumber Matchups", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                )
                            }
                        }
                    }
                }
            }

            // Standings / Matchups Content
            if (selectedLeague.type == "H2H" && h2hSubTab == 1) {
                // H2H Matchups list
                item {
                    Text(
                        text = "GAMEWEEK $gwNumber H2H PAIRINGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = PlTextTertiary
                    )
                }

                items(h2hMatchups, key = { it.id }) { matchup ->
                    H2HMatchupCard(matchup = matchup, currentUserId = currentUser?.id)
                }
            } else {
                // Standings Table
                item {
                    val members = if (selectedLeague.type == "H2H") h2hStandings else classicStandings
                    StandingsTable(
                        members = members,
                        isH2H = selectedLeague.type == "H2H",
                        currentUserId = currentUser?.id
                    )
                }
            }
        }
    }

    // Create League Dialog
    if (isCreateDialogOpen) {
        CreateLeagueDialog(
            onDismiss = onCloseCreateDialog,
            onConfirm = onCreateLeague
        )
    }

    // Join League Dialog
    if (isJoinDialogOpen) {
        JoinLeagueDialog(
            onDismiss = onCloseJoinDialog,
            onConfirm = onJoinLeague
        )
    }
}

@Composable
fun StandingsTable(
    members: List<LeagueMemberEntity>,
    isH2H: Boolean,
    currentUserId: String?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, PlPlumBorder, RoundedCornerShape(14.dp)),
        color = PlPlumCard
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Table Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlTextTertiary,
                    modifier = Modifier.width(24.dp)
                )
                Text(
                    text = "MANAGER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlTextTertiary,
                    modifier = Modifier.weight(1f)
                )

                if (isH2H) {
                    Text(
                        text = "W-D-L",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlTextTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(48.dp)
                    )
                    Text(
                        text = "+/-",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlTextTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(36.dp)
                    )
                    Text(
                        text = "PTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlMint,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(36.dp)
                    )
                } else {
                    Text(
                        text = "GW",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlTextTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(44.dp)
                    )
                    Text(
                        text = "TOTAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlMint,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(48.dp)
                    )
                }
            }

            // Member Rows
            members.forEachIndexed { index, member ->
                val isMe = member.userId == currentUserId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isMe) PlPlumCardElevated else Color.Transparent)
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rank
                    Text(
                        text = "${index + 1}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (index) {
                            0 -> PlYellow
                            1 -> PlCyan
                            2 -> PlMint
                            else -> PlTextSecondary
                        },
                        modifier = Modifier.width(24.dp)
                    )

                    // Manager Name + Gameweek MVP badge
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = member.userName + if (isMe) " (You)" else "",
                            fontSize = 12.sp,
                            fontWeight = if (isMe) FontWeight.Black else FontWeight.SemiBold,
                            color = if (isMe) PlMint else PlTextPrimary,
                            maxLines = 1
                        )

                        // Gameweek MVP Badge
                        if (member.isGwMvp) {
                            Spacer(modifier = Modifier.width(6.dp))
                            ChipStatusPill(
                                type = ChipPillType.GW_MVP,
                                text = "MVP"
                            )
                        }
                    }

                    if (isH2H) {
                        // W-D-L
                        Text(
                            text = "${member.h2hWon}-${member.h2hDrawn}-${member.h2hLost}",
                            fontSize = 11.sp,
                            color = PlTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(48.dp)
                        )
                        // Goal / Point Diff
                        Text(
                            text = (if (member.pointsDiff > 0) "+${member.pointsDiff}" else "${member.pointsDiff}"),
                            fontSize = 11.sp,
                            color = if (member.pointsDiff > 0) PlMint else if (member.pointsDiff < 0) PlPink else PlTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(36.dp)
                        )
                        // H2H Points
                        Text(
                            text = "${member.h2hPoints}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = PlMint,
                            textAlign = TextAlign.End,
                            modifier = Modifier.width(36.dp)
                        )
                    } else {
                        // GW Points
                        Text(
                            text = "${member.gwPoints}",
                            fontSize = 11.sp,
                            color = PlTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(44.dp)
                        )
                        // Total Cumulative Points
                        Text(
                            text = "${member.totalPoints}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = PlMint,
                            textAlign = TextAlign.End,
                            modifier = Modifier.width(48.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun H2HMatchupCard(
    matchup: H2HMatchupEntity,
    currentUserId: String?,
    modifier: Modifier = Modifier
) {
    val isUserInvolved = matchup.user1Id == currentUserId || matchup.user2Id == currentUserId

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (isUserInvolved) PlCyan.copy(alpha = 0.6f) else PlPlumBorder,
                RoundedCornerShape(12.dp)
            ),
        color = PlPlumCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User 1
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = matchup.user1Name + if (matchup.user1Id == currentUserId) " (You)" else "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (matchup.winnerUserId == matchup.user1Id) PlMint else PlTextPrimary,
                    maxLines = 1
                )
                if (matchup.winnerUserId == matchup.user1Id) {
                    Text(text = "WINNER (+3 pts)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PlMint)
                }
            }

            // Score Duel
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PlPlumCardElevated)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${matchup.user1Score} - ${matchup.user2Score}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = PlTextPrimary
                )
            }

            // User 2
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = matchup.user2Name + if (matchup.user2Id == currentUserId) " (You)" else "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (matchup.winnerUserId == matchup.user2Id) PlMint else PlTextPrimary,
                    maxLines = 1,
                    textAlign = TextAlign.End
                )
                if (matchup.winnerUserId == matchup.user2Id) {
                    Text(text = "WINNER (+3 pts)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PlMint)
                }
            }
        }
    }
}

@Composable
fun CreateLeagueDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("CLASSIC") } // "CLASSIC" or "H2H"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PlPlumCard,
        title = {
            Text(
                text = "Create Mini-League",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = PlTextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "Name your league and select its format:",
                    fontSize = 12.sp,
                    color = PlTextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("League Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PlTextPrimary,
                        unfocusedTextColor = PlTextPrimary,
                        focusedBorderColor = PlMint,
                        unfocusedBorderColor = PlPlumBorder,
                        focusedLabelColor = PlMint,
                        unfocusedLabelColor = PlTextSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_league_name")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Select League Type:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlCyan
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Classic Option
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedType = "CLASSIC" }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedType == "CLASSIC",
                        onClick = { selectedType = "CLASSIC" },
                        colors = RadioButtonDefaults.colors(selectedColor = PlMint)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Classic League", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PlTextPrimary)
                        Text("Ranked by Total Cumulative Points", fontSize = 11.sp, color = PlTextSecondary)
                    }
                }

                // H2H Option
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedType = "H2H" }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedType == "H2H",
                        onClick = { selectedType = "H2H" },
                        colors = RadioButtonDefaults.colors(selectedColor = PlPink)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Head-to-Head (H2H)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PlTextPrimary)
                        Text("Pairwise matchups: Win=3pts, Draw=1pt, Loss=0pts", fontSize = 11.sp, color = PlTextSecondary)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, selectedType) },
                colors = ButtonDefaults.buttonColors(containerColor = PlMint, contentColor = PlPlumBackground),
                modifier = Modifier.testTag("btn_confirm_create_league")
            ) {
                Text("Create League", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PlTextSecondary)
            }
        }
    )
}

@Composable
fun JoinLeagueDialog(
    onDismiss: () -> Unit,
    onConfirm: (code: String) -> Unit
) {
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PlPlumCard,
        title = {
            Text(
                text = "Join Mini-League",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = PlTextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter the 6-character league invite code:",
                    fontSize = 12.sp,
                    color = PlTextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = { if (it.length <= 6) code = it.uppercase() },
                    label = { Text("Invite Code (e.g. PL26K8)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PlTextPrimary,
                        unfocusedTextColor = PlTextPrimary,
                        focusedBorderColor = PlCyan,
                        unfocusedBorderColor = PlPlumBorder,
                        focusedLabelColor = PlCyan,
                        unfocusedLabelColor = PlTextSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_join_code")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(code) },
                colors = ButtonDefaults.buttonColors(containerColor = PlCyan, contentColor = PlPlumBackground),
                modifier = Modifier.testTag("btn_confirm_join_league")
            ) {
                Text("Join League", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PlTextSecondary)
            }
        }
    )
}
