package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PLHeader
import com.example.ui.components.RulesHelpBottomSheet
import com.example.ui.screens.AdminSimulatorSheet
import com.example.ui.screens.LeaguesScreen
import com.example.ui.screens.PointsBreakdownScreen
import com.example.ui.screens.PredictionsScreen
import com.example.ui.screens.ProfileAuthDialog
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.PlCyan
import com.example.ui.theme.PlMint
import com.example.ui.theme.PlPlumBackground
import com.example.ui.theme.PlPlumCardElevated
import com.example.ui.theme.PlPlumSurface
import com.example.ui.theme.PlTextPrimary
import com.example.ui.theme.PlTextSecondary
import com.example.ui.viewmodel.PredictorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: PredictorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    var isRulesSheetOpen by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val adminSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Handle Snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PlPlumBackground),
        topBar = {
            if (selectedTab != 3) {
                PLHeader(
                    gameweek = uiState.gameweek,
                    currentUser = uiState.currentUser,
                    countdownText = uiState.deadlineCountdown,
                    isDeadlineLocked = uiState.isDeadlineLocked,
                    onOpenAdmin = { viewModel.setAdminSheetOpen(true) },
                    onOpenProfile = { selectedTab = 3 },
                    onOpenRules = { isRulesSheetOpen = true }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = PlPlumBackground,
                contentColor = PlTextPrimary,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.08f)
                )
            ) {
                // Tab 1: Predictions
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.SportsSoccer,
                            contentDescription = "Predictions"
                        )
                    },
                    label = {
                        Text(
                            text = "PREDICT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PlPlumBackground,
                        selectedTextColor = PlMint,
                        indicatorColor = PlMint,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.testTag("nav_tab_predictions")
                )

                // Tab 2: Points & Breakdown
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.FormatListNumbered,
                            contentDescription = "Points & Breakdown"
                        )
                    },
                    label = {
                        Text(
                            text = "POINTS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PlPlumBackground,
                        selectedTextColor = PlCyan,
                        indicatorColor = PlCyan,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.testTag("nav_tab_points")
                )

                // Tab 3: Mini-Leagues Hub
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Mini-Leagues"
                        )
                    },
                    label = {
                        Text(
                            text = "LEAGUES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PlPlumBackground,
                        selectedTextColor = PlMint,
                        indicatorColor = PlMint,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.testTag("nav_tab_leagues")
                )

                // Tab 4: Manager Profile Hub
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Manager Profile"
                        )
                    },
                    label = {
                        Text(
                            text = "PROFILE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PlPlumBackground,
                        selectedTextColor = PlMint,
                        indicatorColor = PlMint,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PlPlumBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> PredictionsScreen(
                    fixtures = uiState.fixtures,
                    predictions = uiState.predictions,
                    chips = uiState.chips,
                    isDeadlineLocked = uiState.isDeadlineLocked,
                    currentGwNumber = uiState.gameweek?.gwNumber ?: 4,
                    gameweek = uiState.gameweek,
                    isRefreshing = uiState.isFplRefreshing,
                    fplSyncState = uiState.fplSyncState,
                    targetSelectionChip = uiState.targetSelectionChip,
                    onRefresh = { viewModel.refreshFplFixtures() },
                    onScoreChanged = { fId, h, a -> viewModel.onScoreChanged(fId, h, a) },
                    onScoreChangedB = { fId, hb, ab -> viewModel.onScoreChangedB(fId, hb, ab) },
                    onCaptainToggled = { fId -> viewModel.onCaptainToggled(fId) },
                    onChipClicked = { chipType -> viewModel.onChipClicked(chipType) },
                    onFixtureSelectedForChip = { fId -> viewModel.onFixtureSelectedForChip(fId) },
                    onCancelTargetSelection = { viewModel.onCancelTargetSelection() },
                    onSubmitPredictions = { viewModel.onSubmitPredictions() },
                    onOpenRules = { isRulesSheetOpen = true }
                )
                1 -> PointsBreakdownScreen(
                    fixtures = if (uiState.breakdownGwNumber == 3) uiState.breakdownFixtures else uiState.fixtures,
                    predictions = if (uiState.breakdownGwNumber == 3) uiState.breakdownPredictions else uiState.predictions,
                    currentUser = uiState.currentUser,
                    gwNumber = uiState.breakdownGwNumber,
                    onSelectGw = { viewModel.onSelectBreakdownGw(it) },
                    onOpenRules = { isRulesSheetOpen = true }
                )
                2 -> LeaguesScreen(
                    leagues = uiState.leagues,
                    selectedLeague = uiState.selectedLeague,
                    classicStandings = uiState.classicStandings,
                    h2hStandings = uiState.h2hStandings,
                    h2hMatchups = uiState.h2hMatchups,
                    currentUser = uiState.currentUser,
                    gwNumber = uiState.gameweek?.gwNumber ?: 4,
                    isCreateDialogOpen = uiState.isCreateLeagueOpen,
                    isJoinDialogOpen = uiState.isJoinLeagueOpen,
                    onSelectLeague = { viewModel.selectLeague(it) },
                    onOpenCreateDialog = { viewModel.setCreateLeagueOpen(true) },
                    onCloseCreateDialog = { viewModel.setCreateLeagueOpen(false) },
                    onOpenJoinDialog = { viewModel.setJoinLeagueOpen(true) },
                    onCloseJoinDialog = { viewModel.setJoinLeagueOpen(false) },
                    onCreateLeague = { name, type -> viewModel.onCreateLeague(name, type) },
                    onJoinLeague = { code -> viewModel.onJoinLeague(code) }
                )
                3 -> ProfileScreen(
                    currentUser = uiState.currentUser,
                    chips = uiState.chips,
                    badges = uiState.badges,
                    activeLeaguesCount = uiState.leagues.size,
                    onLoginOrRegister = { identifier, email, displayName, avatarUri, presetCrest ->
                        viewModel.onLoginOrRegister(identifier, email, displayName, avatarUri, presetCrest)
                    },
                    onEvaluateBadges = { viewModel.triggerBadgeEvaluation() }
                )
            }
        }
    }

    // Admin Simulator Sheet
    if (uiState.isAdminSheetOpen) {
        AdminSimulatorSheet(
            sheetState = adminSheetState,
            gameweek = uiState.gameweek,
            fixtures = uiState.fixtures,
            isDeadlineLocked = uiState.isDeadlineLocked,
            onDismiss = { viewModel.setAdminSheetOpen(false) },
            onToggleDeadlineLock = { viewModel.onToggleDeadlineLock(it) },
            onSimulateAllResults = { viewModel.onSimulateAllMatchResults() },
            onRecalculateScores = { viewModel.onRecalculateScores() },
            onForceSyncSeason = { viewModel.onForceSyncToGameweek4() },
            onSwitchFixtureScenario = { viewModel.onSwitchFixtureScenario(it) },
            onUpdateScore = { fId, h, a, fin -> viewModel.onAdminUpdateScore(fId, h, a, fin) }
        )
    }

    // Profile & Authentication Dialog
    if (uiState.isAuthDialogOpen) {
        ProfileAuthDialog(
            currentUser = uiState.currentUser,
            chips = uiState.chips,
            badges = uiState.badges,
            activeLeaguesCount = uiState.leagues.size,
            onDismiss = { viewModel.setAuthDialogOpen(false) },
            onLoginOrRegister = { identifier, email, displayName, avatarUri, presetCrest ->
                viewModel.onLoginOrRegister(identifier, email, displayName, avatarUri, presetCrest)
            },
            onEvaluateBadges = { viewModel.triggerBadgeEvaluation() }
        )
    }

    // Rules Help Dialog & Scoring Breakdown Sheet
    if (isRulesSheetOpen) {
        RulesHelpBottomSheet(
            onDismiss = { isRulesSheetOpen = false }
        )
    }
}
