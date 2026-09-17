package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.ChipEntity
import com.example.data.local.entity.UserEntity
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
import com.example.ui.theme.PlWarning
import com.example.ui.theme.PlYellow
import com.example.ui.theme.getClubColor
import com.example.ui.theme.getClubTextColor

/**
 * Manager Profile full-screen destination (used as a main bottom navigation tab).
 */
@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    chips: List<ChipEntity>,
    badges: List<BadgeEntity>,
    activeLeaguesCount: Int,
    onLoginOrRegister: (identifier: String, email: String?, displayName: String?, avatarUri: String?, presetCrestCode: String?) -> Unit,
    onEvaluateBadges: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ProfileScreenContent(
        currentUser = currentUser,
        chips = chips,
        badges = badges,
        activeLeaguesCount = activeLeaguesCount,
        onLoginOrRegister = onLoginOrRegister,
        onEvaluateBadges = onEvaluateBadges,
        isDialog = false,
        onDismiss = null,
        modifier = modifier
    )
}

@Composable
fun ProfileAuthDialog(
    currentUser: UserEntity?,
    chips: List<ChipEntity>,
    badges: List<BadgeEntity>,
    activeLeaguesCount: Int,
    onDismiss: () -> Unit,
    onLoginOrRegister: (identifier: String, email: String?, displayName: String?, avatarUri: String?, presetCrestCode: String?) -> Unit,
    onEvaluateBadges: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = PlPlumCard,
            border = BorderStroke(1.5.dp, PlPlumBorder)
        ) {
            ProfileScreenContent(
                currentUser = currentUser,
                chips = chips,
                badges = badges,
                activeLeaguesCount = activeLeaguesCount,
                onLoginOrRegister = { id, em, disp, av, cr ->
                    onLoginOrRegister(id, em, disp, av, cr)
                    onDismiss()
                },
                onEvaluateBadges = onEvaluateBadges,
                isDialog = true,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
fun ProfileScreenContent(
    currentUser: UserEntity?,
    chips: List<ChipEntity>,
    badges: List<BadgeEntity>,
    activeLeaguesCount: Int,
    onLoginOrRegister: (identifier: String, email: String?, displayName: String?, avatarUri: String?, presetCrestCode: String?) -> Unit,
    onEvaluateBadges: () -> Unit = {},
    isDialog: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Badges Cabinet, 1 = Manager Stats & Chips, 2 = Switch Account
    var identifier by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var selectedBadgeForLore by remember { mutableStateOf<BadgeEntity?>(null) }

    val unlockedBadgesCount = badges.count { it.isUnlocked }

    Column(
        modifier = modifier
            .fillMaxSize()
            .then(if (!isDialog) Modifier.statusBarsPadding() else Modifier)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDialog) {
                        listOf(
                            PlPlumCard,
                            Color(0xFF1E0A24)
                        )
                    } else {
                        listOf(
                            PlPlumBackground,
                            Color(0xFF1B0022)
                        )
                    }
                )
            )
    ) {
        // Compact Profile Header: Avatar and Name
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = if (isDialog) 12.dp else 10.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isDialog) 36.dp else 40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, PlMint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!currentUser?.avatarUri.isNullOrBlank()) {
                        AsyncImage(
                            model = currentUser?.avatarUri,
                            contentDescription = "Manager Avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else if (currentUser?.presetCrestCode != null) {
                        val bg = getClubColor(currentUser.presetCrestCode)
                        val txt = getClubTextColor(currentUser.presetCrestCode)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(bg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser.presetCrestCode,
                                fontSize = if (isDialog) 11.sp else 12.sp,
                                fontWeight = FontWeight.Black,
                                color = txt
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(PlMint, PlCyan))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Profile",
                                tint = PlPlumBackground,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = currentUser?.username ?: "Manager",
                        fontSize = if (isDialog) 16.sp else 18.sp,
                        fontWeight = FontWeight.Black,
                        color = PlTextPrimary
                    )
                    Text(
                        text = "FPL Manager",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PlMint
                    )
                }
            }

            if (isDialog && onDismiss != null) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .testTag("btn_close_profile_dialog")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

                // Manager Quick Stats Strip
                if (currentUser != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = PlPlumCardElevated,
                        border = BorderStroke(1.dp, PlPlumBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("TOTAL POINTS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PlTextTertiary)
                                Text("${currentUser.totalScore} PTS", fontSize = 15.sp, fontWeight = FontWeight.Black, color = PlTextPrimary)
                            }
                            Column {
                                Text("GW POINTS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PlTextTertiary)
                                Text("${currentUser.currentGwScore} PTS", fontSize = 15.sp, fontWeight = FontWeight.Black, color = PlCyan)
                            }
                            Column {
                                Text("LEAGUES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PlTextTertiary)
                                Text("$activeLeaguesCount Active", fontSize = 14.sp, fontWeight = FontWeight.Black, color = PlYellow)
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (unlockedBadgesCount > 0) PlMint.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, if (unlockedBadgesCount > 0) PlMint else PlPlumBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = if (unlockedBadgesCount > 0) PlMint else PlTextTertiary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$unlockedBadgesCount/${badges.size} BADGES",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (unlockedBadgesCount > 0) PlMint else PlTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = PlPlumCardElevated,
                    contentColor = PlMint,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = PlMint
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .height(38.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("Badges", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_profile_badges")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("Chips", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_profile_chips")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("Switch / Auth", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_profile_auth")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            // ACHIEVEMENTS & BADGES CABINET (LazyVerticalGrid)
                            BadgesCabinetSection(
                                badges = badges,
                                onBadgeClick = { selectedBadgeForLore = it },
                                onEvaluateBadges = onEvaluateBadges
                            )
                        }
                        1 -> {
                            // SEASON CHIPS & STATS
                            ChipsTrackerSection(
                                currentUser = currentUser,
                                chips = chips
                            )
                        }
                        2 -> {
                            // SWITCH ACCOUNT / 6-DIGIT EMAIL OTP / PROFILE SETUP
                            AuthSwitchSection(
                                currentUser = currentUser,
                                onLoginOrRegister = { id, em, dispName, avatar, crest ->
                                    onLoginOrRegister(id, em, dispName, avatar, crest)
                                    if (isDialog) {
                                        onDismiss?.invoke()
                                    } else {
                                        selectedTab = 0
                                    }
                                }
                            )
                        }
                    }
                }

                // Footer Actions (Only displayed when shown as a modal dialog)
                if (isDialog && onDismiss != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PlMint,
                                contentColor = PlPlumBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_close_profile")
                        ) {
                            Text("Close", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

    // Lore and Criteria Dialog for Selected Badge
    selectedBadgeForLore?.let { badge ->
        BadgeLoreDetailDialog(
            badge = badge,
            onDismiss = { selectedBadgeForLore = null }
        )
    }
}

/**
 * The Badge Cabinet / Showcase grid implementation.
 */
@Composable
private fun BadgesCabinetSection(
    badges: List<BadgeEntity>,
    onBadgeClick: (BadgeEntity) -> Unit,
    onEvaluateBadges: () -> Unit
) {
    val unlockedCount = badges.count { it.isUnlocked }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("grid_badge_cabinet")
    ) {
        item(span = { GridItemSpan(2) }) {
            // Editorial Header Banner with Premier League Neon Accents
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF280A30),
                border = BorderStroke(1.dp, PlPlumBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ACHIEVEMENTS & BADGES",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = PlMint,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "($unlockedCount/${badges.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PlCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Milestones awarded automatically upon match finalization. Tap any badge to inspect criteria & lore.",
                            fontSize = 10.sp,
                            color = PlTextSecondary,
                            lineHeight = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Quick evaluation button for instant recalculation testing
                    Surface(
                        onClick = onEvaluateBadges,
                        shape = RoundedCornerShape(8.dp),
                        color = PlPlumCardElevated,
                        border = BorderStroke(1.dp, PlPlumBorder),
                        modifier = Modifier.testTag("btn_eval_badges")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Evaluate Badges",
                                tint = PlCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Check", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        items(badges, key = { it.id }) { badge ->
            BadgeCabinetCard(
                badge = badge,
                onClick = { onBadgeClick(badge) }
            )
        }
    }
}

/**
 * Individual Badge Card inside the cabinet.
 * Unlocked badges display in vibrant neon styling with their unlock date.
 * Locked badges appear dim/grayscale with a progress indicator showing how close the user is to earning them.
 */
@Composable
private fun BadgeCabinetCard(
    badge: BadgeEntity,
    onClick: () -> Unit
) {
    val isUnlocked = badge.isUnlocked
    val icon = getBadgeIcon(badge.iconName, badge.badgeKey)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isUnlocked) Color(0xFF2E0935) else Color(0xFF1B0721).copy(alpha = 0.85f),
        border = if (isUnlocked) {
            BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(PlMint, PlCyan)))
        } else {
            BorderStroke(1.dp, PlPlumBorder.copy(alpha = 0.4f))
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("badge_card_${badge.badgeKey.lowercase()}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge Crest Icon Container
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) {
                            Brush.radialGradient(
                                colors = listOf(
                                    PlMint.copy(alpha = 0.25f),
                                    Color(0xFF38003C)
                                )
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.04f),
                                    Color(0xFF18051E)
                                )
                            )
                        }
                    )
                    .border(
                        width = 1.dp,
                        brush = if (isUnlocked) Brush.linearGradient(listOf(PlMint, PlYellow)) else Brush.linearGradient(listOf(PlPlumBorder.copy(alpha = 0.5f), Color.Transparent)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = badge.title,
                    tint = if (isUnlocked) PlMint else PlTextTertiary,
                    modifier = Modifier.size(26.dp)
                )

                // Small badge overlay
                if (isUnlocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(PlYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Unlocked",
                            tint = PlPlumBackground,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2C1434)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = PlTextTertiary,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badge Title
            Text(
                text = badge.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = if (isUnlocked) Color.White else PlTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Short Description
            Text(
                text = badge.description,
                fontSize = 10.sp,
                color = if (isUnlocked) PlTextSecondary else PlTextTertiary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status Indicator: Unlocked with Date vs Locked with Progress Bar
            if (isUnlocked) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PlMint.copy(alpha = 0.16f),
                    border = BorderStroke(0.5.dp, PlMint.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✓ UNLOCKED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = PlMint
                        )
                    }
                }

                badge.unlockedDate?.let { date ->
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = date,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PlCyan
                    )
                }
            } else {
                // Locked Progress
                val progressFraction = (badge.currentProgress.toFloat() / badge.targetProgress.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PlCyan,
                        trackColor = PlPlumBorder.copy(alpha = 0.4f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${badge.currentProgress} / ${badge.targetProgress}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlCyan
                    )
                }
            }
        }
    }
}

/**
 * Lore & Full Criteria Modal Dialog.
 */
@Composable
private fun BadgeLoreDetailDialog(
    badge: BadgeEntity,
    onDismiss: () -> Unit
) {
    val isUnlocked = badge.isUnlocked
    val icon = getBadgeIcon(badge.iconName, badge.badgeKey)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PlPlumCard,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked) Brush.linearGradient(listOf(PlMint, PlCyan)) else Brush.linearGradient(listOf(Color(0xFF451950), Color(0xFF2E0935)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = badge.title,
                        tint = if (isUnlocked) PlPlumBackground else PlTextTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = badge.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = PlTextPrimary
                    )
                    Text(
                        text = if (isUnlocked) "UNLOCKED • ${badge.unlockedDate ?: "Gameweek 3"}" else "LOCKED • ${badge.currentProgress}/${badge.targetProgress}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) PlMint else PlWarning
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Criteria Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PlPlumCardElevated,
                    border = BorderStroke(1.dp, PlPlumBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "OBJECTIVE & TARGET",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = PlCyan,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = badge.description,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Lore Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF280A30),
                    border = BorderStroke(1.dp, PlPlumBorder.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "PREMIER LEAGUE LORE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = PlYellow,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "\"${badge.lore}\"",
                            fontSize = 11.sp,
                            color = PlTextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Progress status if locked
                if (!isUnlocked) {
                    val progressFraction = (badge.currentProgress.toFloat() / badge.targetProgress.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PlPlumCardElevated,
                        border = BorderStroke(1.dp, PlPlumBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Current Progress", fontSize = 10.sp, color = PlTextSecondary)
                                Text("${badge.currentProgress} of ${badge.targetProgress}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = PlCyan)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = PlCyan,
                                trackColor = PlPlumBorder
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PlMint, contentColor = PlPlumBackground)
            ) {
                Text("Got It", fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * Chip Tracker & Manager Season Status
 */
@Composable
private fun ChipsTrackerSection(
    currentUser: UserEntity?,
    chips: List<ChipEntity>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "SEASON CHIPS AVAILABILITY (TWICE PER SEASON)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PlCyan,
            letterSpacing = 0.5.sp
        )

        ChipSeasonTrackerCard(
            halfTitle = "Half 1 (GW 1 - 19) • Active Now",
            superCapChip = chips.find { it.chipType == "SUPER_CAPTAIN" && it.half == 1 },
            safetyNetChip = chips.find { it.chipType == "SAFETY_NET" && it.half == 1 },
            autoCaptainChip = chips.find { it.chipType == "AUTO_CAPTAIN" && it.half == 1 },
            doubleShotChip = chips.find { it.chipType == "DOUBLE_SHOT" && it.half == 1 }
        )

        ChipSeasonTrackerCard(
            halfTitle = "Half 2 (GW 20 - 38) • Unlocks at GW 20",
            superCapChip = chips.find { it.chipType == "SUPER_CAPTAIN" && it.half == 2 },
            safetyNetChip = chips.find { it.chipType == "SAFETY_NET" && it.half == 2 },
            autoCaptainChip = chips.find { it.chipType == "AUTO_CAPTAIN" && it.half == 2 },
            doubleShotChip = chips.find { it.chipType == "DOUBLE_SHOT" && it.half == 2 }
        )

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = PlPlumCardElevated,
            border = BorderStroke(1.dp, PlPlumBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "CHIP RULES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = PlYellow
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Super Captain: Multiplies match score by 3x (instead of regular 2x).\n" +
                            "• Safety Net: Rescues zero-point predictions by guaranteeing 3 points if your predicted outcome was entirely wrong.\n" +
                            "• Auto Captain: Automatically awards 2x multiplier to your highest scoring match of the Gameweek.\n" +
                            "• Double Shot: Allows submitting 2 predictions (A & B) for one match; your highest score counts.\n" +
                            "• You receive one of each chip in Half 1, and an automatic refresh in Half 2. Only 1 chip can be played per Gameweek.",
                    fontSize = 11.sp,
                    color = PlTextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun ChipSeasonTrackerCard(
    halfTitle: String,
    superCapChip: ChipEntity?,
    safetyNetChip: ChipEntity?,
    autoCaptainChip: ChipEntity? = null,
    doubleShotChip: ChipEntity? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = PlPlumCardElevated,
        border = BorderStroke(1.dp, PlPlumBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = halfTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PlTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Super Captain
                ChipMiniStatus(
                    title = "Super Cap",
                    icon = Icons.Default.Bolt,
                    color = PlPink,
                    isUsed = superCapChip?.isUsed == true,
                    usedInGw = superCapChip?.usedInGw
                )

                // Safety Net
                ChipMiniStatus(
                    title = "Safety Net",
                    icon = Icons.Default.Security,
                    color = PlCyan,
                    isUsed = safetyNetChip?.isUsed == true,
                    usedInGw = safetyNetChip?.usedInGw
                )

                // Auto Captain
                ChipMiniStatus(
                    title = "Auto Cap",
                    icon = Icons.Default.Star,
                    color = PlYellow,
                    isUsed = autoCaptainChip?.isUsed == true,
                    usedInGw = autoCaptainChip?.usedInGw
                )

                // Double Shot
                ChipMiniStatus(
                    title = "Double Shot",
                    icon = Icons.Default.MilitaryTech,
                    color = PlMint,
                    isUsed = doubleShotChip?.isUsed == true,
                    usedInGw = doubleShotChip?.usedInGw
                )
            }
        }
    }
}

@Composable
private fun ChipMiniStatus(
    title: String,
    icon: ImageVector,
    color: Color,
    isUsed: Boolean,
    usedInGw: Int?
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isUsed) Color.White.copy(alpha = 0.08f) else color.copy(alpha = 0.2f))
                .border(1.dp, if (isUsed) PlPlumBorder else color.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isUsed) PlTextTertiary else color,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (isUsed) PlTextTertiary else Color.White
        )
        Text(
            text = if (isUsed) "GW $usedInGw" else "Ready",
            fontSize = 8.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isUsed) PlTextTertiary else PlMint
        )
    }
}

/**
 * Authentication, 6-Digit Email OTP Verification & Profile Setup Section
 */
@Composable
private fun AuthSwitchSection(
    currentUser: UserEntity?,
    onLoginOrRegister: (identifier: String, email: String?, displayName: String?, avatarUri: String?, presetCrestCode: String?) -> Unit
) {
    // 0 = Email Entry & OTP request
    // 1 = Enter 6-Digit OTP Verification PIN
    // 2 = First-Login Profile Setup (Display Name, Custom Avatar Upload, Club Crests)
    var authStep by remember { mutableIntStateOf(0) }

    var emailInput by remember { mutableStateOf(currentUser?.email ?: "") }
    var identifierInput by remember { mutableStateOf(currentUser?.username ?: "") }
    var generatedOtp by remember { mutableStateOf<String?>(null) }
    var enteredOtp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }
    var showNotificationBanner by remember { mutableStateOf(false) }

    // Profile Setup State
    var displayNameInput by remember { mutableStateOf(currentUser?.username ?: "") }
    var selectedAvatarUri by remember { mutableStateOf<String?>(currentUser?.avatarUri) }
    var selectedPresetCrest by remember { mutableStateOf(currentUser?.presetCrestCode ?: "ARS") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedAvatarUri = uri.toString()
        }
    }

    val premierLeagueClubs = listOf(
        "ARS" to "Arsenal", "AVL" to "Aston Villa", "BOU" to "Bournemouth", "BRE" to "Brentford",
        "BHA" to "Brighton", "CHE" to "Chelsea", "CRY" to "Crystal Palace", "EVE" to "Everton",
        "FUL" to "Fulham", "IPS" to "Ipswich", "LEI" to "Leicester", "LIV" to "Liverpool",
        "MCI" to "Man City", "MUN" to "Man United", "NEW" to "Newcastle", "NFO" to "Nott'm Forest",
        "SOU" to "Southampton", "TOT" to "Spurs", "WHU" to "West Ham", "WOL" to "Wolves"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Simulated Push Notification Toast (when OTP is generated)
        AnimatedVisibility(
            visible = showNotificationBanner && generatedOtp != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notification_toast_otp"),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2A103D),
                border = BorderStroke(1.5.dp, PlMint)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PlMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Notification",
                            tint = PlPlumBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PREMIER LEAGUE AUTH ALERT",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = PlMint,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text("Now", fontSize = 9.sp, color = PlTextTertiary)
                        }
                        Text(
                            text = "Your verification PIN is: $generatedOtp",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlTextPrimary
                        )
                        Text(
                            text = "Tap 'Auto-Fill' to insert your secure 6-digit code.",
                            fontSize = 10.sp,
                            color = PlTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            enteredOtp = generatedOtp ?: ""
                            otpError = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PlCyan, contentColor = PlPlumBackground),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_autofill_otp")
                    ) {
                        Text("Auto-Fill", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        when (authStep) {
            0 -> {
                // STEP 0: Email Entry & Trigger 6-Digit OTP
                Text(
                    text = "STEP 1: EMAIL VERIFICATION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = PlMint,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Enter your manager email. We will generate and send a secure 6-digit verification code to authenticate your account.",
                    fontSize = 12.sp,
                    color = PlTextSecondary,
                    lineHeight = 16.sp
                )

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = {
                        emailInput = it
                        if (identifierInput.isBlank() || identifierInput.contains("@")) {
                            identifierInput = it.substringBefore("@")
                        }
                    },
                    label = { Text("Manager Email Address") },
                    placeholder = { Text("manager@premierleague.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                        .testTag("input_auth_email")
                )

                Button(
                    onClick = {
                        val email = emailInput.trim()
                        if (email.isBlank() || !email.contains("@")) {
                            otpError = "Please enter a valid email address."
                            return@Button
                        }
                        otpError = null
                        val code = (100000..999999).random().toString()
                        generatedOtp = code
                        showNotificationBanner = true
                        enteredOtp = ""
                        authStep = 1
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlMint, contentColor = PlPlumBackground),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_send_otp")
                ) {
                    Icon(Icons.Default.MarkEmailRead, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send 6-Digit Verification PIN", fontWeight = FontWeight.Bold)
                }

                if (otpError != null) {
                    Text(
                        text = otpError ?: "",
                        color = Color(0xFFFF5252),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Quick Demo Accounts
                Text(
                    text = "Quick Demo Accounts:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlTextTertiary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("PremierMaster", "DeclanSpecial", "KloppGegenpress").forEach { demoName ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PlPlumCardElevated)
                                .border(1.dp, PlPlumBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    emailInput = "$demoName@premierleague.com"
                                    identifierInput = demoName
                                    displayNameInput = demoName
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(demoName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PlCyan)
                        }
                    }
                }
            }

            1 -> {
                // STEP 1: Enter 6-Digit PIN & Exact Match Validation
                Text(
                    text = "STEP 2: ENTER 6-DIGIT CODE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = PlCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "A 6-digit verification code was sent to $emailInput. Please enter the exact code to verify and unlock access.",
                    fontSize = 12.sp,
                    color = PlTextSecondary,
                    lineHeight = 16.sp
                )

                OutlinedTextField(
                    value = enteredOtp,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            enteredOtp = it
                            otpError = null
                        }
                    },
                    label = { Text("6-Digit Verification PIN") },
                    placeholder = { Text("••••••") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PlTextPrimary,
                        unfocusedTextColor = PlTextPrimary,
                        focusedBorderColor = if (otpError != null) Color(0xFFFF5252) else PlCyan,
                        unfocusedBorderColor = if (otpError != null) Color(0xFFFF5252) else PlPlumBorder,
                        focusedLabelColor = PlCyan,
                        unfocusedLabelColor = PlTextSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_auth_otp")
                )

                if (otpError != null) {
                    Text(
                        text = otpError ?: "",
                        color = Color(0xFFFF5252),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            authStep = 0
                            otpError = null
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, PlPlumBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back", color = PlTextSecondary)
                    }

                    Button(
                        onClick = {
                            if (enteredOtp.trim() != generatedOtp) {
                                otpError = "Invalid verification code. Please enter the exact 6-digit PIN ($generatedOtp) to unlock."
                                return@Button
                            }
                            otpError = null
                            if (displayNameInput.isBlank()) {
                                displayNameInput = identifierInput.ifBlank { "Manager" }
                            }
                            authStep = 2
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PlCyan, contentColor = PlPlumBackground),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(2f)
                            .testTag("btn_verify_otp")
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verify & Continue", fontWeight = FontWeight.Bold)
                    }
                }
            }

            2 -> {
                // STEP 2: Profile Setup (Display Name, Custom Avatar Upload, Preset Crests)
                Text(
                    text = "STEP 3: MANAGER PROFILE SETUP",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = PlMint,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Verification successful! Choose your Manager Display Name and avatar for leaderboards and mini-leagues.",
                    fontSize = 12.sp,
                    color = PlTextSecondary,
                    lineHeight = 16.sp
                )

                // Current Avatar / Crest Preview
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PlPlumCardElevated)
                        .border(1.dp, PlPlumBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .border(2.dp, PlMint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!selectedAvatarUri.isNullOrBlank()) {
                            AsyncImage(
                                model = selectedAvatarUri,
                                contentDescription = "Custom Avatar",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            val bg = getClubColor(selectedPresetCrest)
                            val txt = getClubTextColor(selectedPresetCrest)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(bg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = selectedPresetCrest,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = txt
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayNameInput.ifBlank { "New Manager" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlTextPrimary
                        )
                        Text(
                            text = if (!selectedAvatarUri.isNullOrBlank()) "Custom Device Photo" else "$selectedPresetCrest Club Badge",
                            fontSize = 11.sp,
                            color = PlMint
                        )
                    }

                    // Gallery photo picker button
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PlPlumBackground),
                        border = BorderStroke(1.dp, PlPlumBorder),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_upload_avatar")
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = PlCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gallery", fontSize = 11.sp, color = PlCyan)
                    }
                }

                // Display Name Input
                OutlinedTextField(
                    value = displayNameInput,
                    onValueChange = { displayNameInput = it },
                    label = { Text("Display Name") },
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
                        .testTag("input_profile_display_name")
                )

                // Preset Club Crests Row
                Text(
                    text = "Or Choose Club Crest Preset:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlTextSecondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    premierLeagueClubs.forEach { (code, name) ->
                        val isSelected = selectedPresetCrest == code && selectedAvatarUri == null
                        val clubBg = getClubColor(code)
                        val clubTxt = getClubTextColor(code)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PlMint.copy(alpha = 0.15f) else Color.Transparent)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) PlMint else PlPlumBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedPresetCrest = code
                                    selectedAvatarUri = null
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(clubBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = code,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = clubTxt
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = code,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PlMint else PlTextSecondary
                            )
                        }
                    }
                }

                // Final Submit Button
                Button(
                    onClick = {
                        val finalName = displayNameInput.ifBlank { identifierInput.ifBlank { "Manager" } }
                        onLoginOrRegister(
                            finalName,
                            emailInput,
                            finalName,
                            selectedAvatarUri,
                            selectedPresetCrest
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlMint, contentColor = PlPlumBackground),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_save_profile")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Profile & Enter Predictor", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

private fun getBadgeIcon(iconName: String, badgeKey: String): ImageVector {
    return when (badgeKey) {
        "THE_SNIPER" -> Icons.Default.CrisisAlert
        "CLEAN_SHEET_MASTER" -> Icons.Default.Shield
        "CAPTAIN_FANTASTIC" -> Icons.Default.MilitaryTech
        "UNSTOPPABLE" -> Icons.Default.LocalFireDepartment
        "LIFESAVER" -> Icons.Default.HealthAndSafety
        else -> when (iconName) {
            "Crosshair", "CrisisAlert" -> Icons.Default.CrisisAlert
            "Shield" -> Icons.Default.Shield
            "MilitaryTech" -> Icons.Default.MilitaryTech
            "Fire", "LocalFireDepartment" -> Icons.Default.LocalFireDepartment
            "HealthAndSafety" -> Icons.Default.HealthAndSafety
            else -> Icons.Default.EmojiEvents
        }
    }
}
