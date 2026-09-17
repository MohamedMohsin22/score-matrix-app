package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.ChipEntity
import com.example.data.local.entity.FixtureEntity
import com.example.data.local.entity.GameweekEntity
import com.example.data.local.entity.H2HMatchupEntity
import com.example.data.local.entity.LeagueEntity
import com.example.data.local.entity.LeagueMemberEntity
import com.example.data.local.entity.PredictionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.AppRepository
import com.example.data.repository.FplSyncState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class PredictorUiState(
    val currentUser: UserEntity? = null,
    val gameweek: GameweekEntity? = null,
    val fixtures: List<FixtureEntity> = emptyList(),
    val predictions: Map<String, PredictionEntity> = emptyMap(),
    val chips: List<ChipEntity> = emptyList(),
    val badges: List<BadgeEntity> = emptyList(),
    val deadlineCountdown: String = "--:--:--",
    val isDeadlineLocked: Boolean = false,
    val leagues: List<LeagueEntity> = emptyList(),
    val selectedLeague: LeagueEntity? = null,
    val classicStandings: List<LeagueMemberEntity> = emptyList(),
    val h2hStandings: List<LeagueMemberEntity> = emptyList(),
    val h2hMatchups: List<H2HMatchupEntity> = emptyList(),
    val isCreateLeagueOpen: Boolean = false,
    val isJoinLeagueOpen: Boolean = false,
    val isAuthDialogOpen: Boolean = false,
    val isAdminSheetOpen: Boolean = false,
    val targetSelectionChip: String? = null, // "SAFETY_NET" or "DOUBLE_SHOT"
    val snackbarMessage: String? = null,
    val isLoading: Boolean = false,
    val isFplRefreshing: Boolean = false,
    val fplSyncState: FplSyncState = FplSyncState.Idle,
    val breakdownGwNumber: Int = 3,
    val breakdownFixtures: List<FixtureEntity> = emptyList(),
    val breakdownPredictions: Map<String, PredictionEntity> = emptyMap()
) {
    val activeChip: String?
        get() = when {
            predictions.values.any { it.isSuperCaptain } -> "SUPER_CAPTAIN"
            predictions.values.any { it.isSafetyNet } -> "SAFETY_NET"
            predictions.values.any { it.isAutoCaptain } -> "AUTO_CAPTAIN"
            predictions.values.any { it.isDoubleShot } -> "DOUBLE_SHOT"
            else -> null
        }

    val isAutoCaptainActive: Boolean
        get() = predictions.values.any { it.isAutoCaptain }

    val captainFixtureId: String?
        get() = predictions.values.find { it.isCaptain || it.isSuperCaptain }?.fixtureId
}

class PredictorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    private val _uiState = MutableStateFlow(PredictorUiState())
    val uiState: StateFlow<PredictorUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null
    private var leagueStandingsJob: Job? = null
    private var breakdownJob: Job? = null
    private var badgesJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
            observeCoreData()
            observeBreakdownData(3)
            startCountdownTimer()
            // Auto-fetch real upcoming fixtures from official FPL API
            repository.refreshFplFixtures(force = false)
        }
    }

    fun refreshFplFixtures() {
        viewModelScope.launch {
            repository.refreshFplFixtures(force = true)
        }
    }

    private fun observeCoreData() {
        viewModelScope.launch {
            repository.fplSyncState.collectLatest { syncState ->
                _uiState.update {
                    it.copy(
                        fplSyncState = syncState,
                        isFplRefreshing = syncState is FplSyncState.Loading
                    )
                }
            }
        }
        viewModelScope.launch {
            repository.currentUserFlow.collectLatest { user ->
                _uiState.update { it.copy(currentUser = user) }
                if (user != null) {
                    val gw = _uiState.value.gameweek?.gwNumber ?: 4
                    observeUserPredictions(user.id, gw)
                    observeUserChips(user.id)
                    observeUserBadges(user.id)
                    observeUserLeagues(user.id)
                    observeBreakdownData(_uiState.value.breakdownGwNumber)
                }
            }
        }

        viewModelScope.launch {
            repository.gameweekFlow.collectLatest { gw ->
                _uiState.update {
                    it.copy(
                        gameweek = gw,
                        isDeadlineLocked = gw?.isDeadlinePassed == true
                    )
                }
                if (gw != null) {
                    observeFixtures(gw.gwNumber)
                    val user = _uiState.value.currentUser
                    if (user != null) {
                        observeUserPredictions(user.id, gw.gwNumber)
                    }
                }
            }
        }
    }

    fun onSelectBreakdownGw(gwNumber: Int) {
        _uiState.update { it.copy(breakdownGwNumber = gwNumber) }
        observeBreakdownData(gwNumber)
    }

    private fun observeBreakdownData(gwNumber: Int) {
        breakdownJob?.cancel()
        breakdownJob = viewModelScope.launch {
            val user = _uiState.value.currentUser
            launch {
                repository.getFixturesFlow(gwNumber).collectLatest { fixes ->
                    _uiState.update { it.copy(breakdownFixtures = fixes) }
                }
            }
            if (user != null) {
                launch {
                    repository.getPredictionsFlow(user.id, gwNumber).collectLatest { preds ->
                        val map = preds.associateBy { it.fixtureId }
                        _uiState.update { it.copy(breakdownPredictions = map) }
                    }
                }
            }
        }
    }

    private fun observeFixtures(gwNumber: Int) {
        viewModelScope.launch {
            repository.getFixturesFlow(gwNumber).collectLatest { fixtures ->
                _uiState.update { it.copy(fixtures = fixtures) }
            }
        }
    }

    private fun observeUserPredictions(userId: String, gwNumber: Int) {
        viewModelScope.launch {
            repository.getPredictionsFlow(userId, gwNumber).collectLatest { preds ->
                val map = preds.associateBy { it.fixtureId }
                _uiState.update { it.copy(predictions = map) }
            }
        }
    }

    private fun observeUserChips(userId: String) {
        viewModelScope.launch {
            repository.getChipsFlow(userId).collectLatest { chips ->
                _uiState.update { it.copy(chips = chips) }
            }
        }
    }

    private fun observeUserBadges(userId: String) {
        badgesJob?.cancel()
        badgesJob = viewModelScope.launch {
            repository.ensureBadgesInitialized(userId)
            repository.getBadgesFlow(userId).collectLatest { badges ->
                _uiState.update { it.copy(badges = badges) }
            }
        }
    }

    fun triggerBadgeEvaluation() {
        val user = _uiState.value.currentUser ?: return
        val gw = _uiState.value.gameweek?.gwNumber ?: 4
        viewModelScope.launch {
            repository.evaluateBadgesForUser(user.id, gw)
            showMessage("Achievements & Badges updated!")
        }
    }

    private fun observeUserLeagues(userId: String) {
        viewModelScope.launch {
            repository.getLeaguesForUser(userId).collectLatest { leagues ->
                _uiState.update { state ->
                    val selected = state.selectedLeague?.let { sel ->
                        leagues.find { it.id == sel.id }
                    } ?: leagues.firstOrNull()
                    state.copy(
                        leagues = leagues,
                        selectedLeague = selected
                    )
                }
                _uiState.value.selectedLeague?.let { observeLeagueDetails(it) }
            }
        }
    }

    private fun observeLeagueDetails(league: LeagueEntity) {
        leagueStandingsJob?.cancel()
        leagueStandingsJob = viewModelScope.launch {
            launch {
                repository.getClassicStandings(league.id).collectLatest { standings ->
                    _uiState.update { it.copy(classicStandings = standings) }
                }
            }
            if (league.type == "H2H") {
                launch {
                    repository.getH2HStandings(league.id).collectLatest { standings ->
                        _uiState.update { it.copy(h2hStandings = standings) }
                    }
                }
                launch {
                    val gw = _uiState.value.gameweek?.gwNumber ?: 28
                    repository.getH2HMatchups(league.id, gw).collectLatest { matchups ->
                        _uiState.update { it.copy(h2hMatchups = matchups) }
                    }
                }
            }
        }
    }

    private fun startCountdownTimer() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                val gw = _uiState.value.gameweek
                val fixtures = _uiState.value.fixtures
                if (gw != null) {
                    val now = System.currentTimeMillis()
                    val firstKickoff = fixtures.minOfOrNull { it.kickoffEpochMs } ?: gw.kickoffFirstMatchMs
                    val strictDeadline = firstKickoff - 3600000L // strictly 60 mins prior to first match kickoff

                    when {
                        // The exact second Gameweek N kicks off:
                        // GW N switches to live read-only mode, and GW N+1 automatically opens and unlocks
                        now >= firstKickoff -> {
                            _uiState.update {
                                it.copy(
                                    deadlineCountdown = "ROUND LIVE (IN PLAY)",
                                    isDeadlineLocked = true
                                )
                            }
                            if (!gw.isDeadlinePassed) {
                                repository.toggleDeadlineLocked(true)
                            }
                            if (gw.isNext) {
                                repository.transitionToNextGameweek(gw.gwNumber)
                            }
                        }
                        // When Gameweek N hits its 60-minute deadline:
                        // Lock predictions, chips, and captain selection
                        now >= strictDeadline || gw.isDeadlinePassed -> {
                            val timeToKickoff = firstKickoff - now
                            val mins = (timeToKickoff % (3600 * 1000)) / (60 * 1000)
                            val secs = (timeToKickoff % (60 * 1000)) / 1000
                            val countdownText = if (timeToKickoff > 0) {
                                String.format("DEADLINE LOCKED • Kickoff in %02dm %02ds", mins, secs)
                            } else {
                                "DEADLINE LOCKED"
                            }
                            _uiState.update {
                                it.copy(
                                    deadlineCountdown = countdownText,
                                    isDeadlineLocked = true
                                )
                            }
                            if (!gw.isDeadlinePassed) {
                                repository.toggleDeadlineLocked(true)
                            }
                        }
                        else -> {
                            val diff = strictDeadline - now
                            val hours = diff / (3600 * 1000)
                            val mins = (diff % (3600 * 1000)) / (60 * 1000)
                            val secs = (diff % (60 * 1000)) / 1000
                            val formatted = String.format("%02dh %02dm %02ds", hours, mins, secs)
                            _uiState.update {
                                it.copy(
                                    deadlineCountdown = formatted,
                                    isDeadlineLocked = false
                                )
                            }
                        }
                    }
                }
                delay(1000L)
            }
        }
    }

    fun selectLeague(league: LeagueEntity) {
        _uiState.update { it.copy(selectedLeague = league) }
        observeLeagueDetails(league)
    }

    fun onScoreChanged(fixtureId: String, homeScore: Int?, awayScore: Int?) {
        val user = _uiState.value.currentUser ?: return
        val gw = _uiState.value.gameweek?.gwNumber ?: 4
        if (_uiState.value.isDeadlineLocked) {
            showMessage("Predictions are locked for this Gameweek!")
            return
        }

        viewModelScope.launch {
            repository.savePrediction(user.id, fixtureId, gw, homeScore, awayScore)
        }
    }

    fun onScoreChangedB(fixtureId: String, homeScoreB: Int?, awayScoreB: Int?) {
        val user = _uiState.value.currentUser ?: return
        val gw = _uiState.value.gameweek?.gwNumber ?: 4
        if (_uiState.value.isDeadlineLocked) {
            showMessage("Predictions are locked for this Gameweek!")
            return
        }
        viewModelScope.launch {
            repository.savePredictionB(user.id, fixtureId, gw, homeScoreB, awayScoreB)
        }
    }

    /**
     * Dedicated Captain Engine:
     * - Strict Mutual Exclusivity: Tapping Captain on any match assigns captaincy to that fixture
     *   and clears it from all others.
     * - If Auto Captain is active, manual selection is locked.
     * - Tapping the already assigned Captain removes it.
     */
    fun onCaptainToggled(fixtureId: String) {
        val user = _uiState.value.currentUser ?: return
        val gw = _uiState.value.gameweek?.gwNumber ?: 4
        if (_uiState.value.isDeadlineLocked) {
            showMessage("Predictions are locked!")
            return
        }

        if (_uiState.value.isAutoCaptainActive) {
            showMessage("Auto Captain is active! Manual captain selection is locked.")
            return
        }

        viewModelScope.launch {
            val currentPred = _uiState.value.predictions[fixtureId]
            if (currentPred?.isCaptain == true || currentPred?.isSuperCaptain == true) {
                // Remove captain
                repository.removeCaptain(user.id, gw)
                showMessage("Captain unassigned")
            } else {
                // Check if Super Captain chip is currently active in the Gameweek
                val isSuperActive = _uiState.value.activeChip == "SUPER_CAPTAIN"
                repository.setCaptain(user.id, fixtureId, gw, isSuper = isSuperActive)
                val mult = if (isSuperActive) "3x (Super Captain)" else "2x"
                showMessage("Captain assigned ($mult Multiplier)")
            }
        }
    }

    /**
     * Interactive 4-Chip Top Hub Handler:
     * Enforces Seasonal Quota (once in GW 1-19, once in GW 20-38)
     * Enforces Strict "One Chip per Gameweek" Rule
     */
    fun onChipClicked(chipType: String) {
        val user = _uiState.value.currentUser ?: return
        val gw = _uiState.value.gameweek?.gwNumber ?: 4
        if (_uiState.value.isDeadlineLocked) {
            showMessage("Predictions are locked! Chips cannot be modified.")
            return
        }

        val half = if (gw <= 19) 1 else 2
        val chip = _uiState.value.chips.find { it.chipType == chipType && it.half == half }
        val chipDisplayName = when (chipType) {
            "SUPER_CAPTAIN" -> "Super Captain"
            "SAFETY_NET" -> "Safety Net"
            "AUTO_CAPTAIN" -> "Auto Captain"
            "DOUBLE_SHOT" -> "Double Shot"
            else -> chipType
        }

        // 1. If this chip is already active, clicking it deactivates it
        if (_uiState.value.activeChip == chipType) {
            viewModelScope.launch {
                when (chipType) {
                    "SUPER_CAPTAIN" -> {
                        // Revert Captain to standard 2x
                        _uiState.value.captainFixtureId?.let { fixId ->
                            repository.setCaptain(user.id, fixId, gw, isSuper = false)
                        }
                        showMessage("Super Captain deactivated (reverted to standard 2x Captain)")
                    }
                    "SAFETY_NET" -> {
                        repository.removeSafetyNet(user.id, gw)
                        showMessage("Safety Net deactivated")
                    }
                    "AUTO_CAPTAIN" -> {
                        repository.removeAutoCaptain(user.id, gw)
                        showMessage("Auto Captain deactivated")
                    }
                    "DOUBLE_SHOT" -> {
                        repository.removeDoubleShot(user.id, gw)
                        showMessage("Double Shot deactivated")
                    }
                }
            }
            return
        }

        // 2. Check Seasonal Quota: has it been used already this half?
        if (chip?.isUsed == true) {
            showMessage("$chipDisplayName has already been used in Half $half (GW ${if (half == 1) "1-19" else "20-38"})!")
            return
        }

        // 3. Activate the chip
        viewModelScope.launch {
            when (chipType) {
                "SUPER_CAPTAIN" -> {
                    val capId = _uiState.value.captainFixtureId
                    if (capId == null) {
                        showMessage("Select a match as Captain first (tap 'C' badge), then tap Super Captain (3x)!")
                    } else {
                        // Clear any other chip & upgrade captain to 3x
                        repository.setCaptain(user.id, capId, gw, isSuper = true)
                        showMessage("SUPER CAPTAIN ACTIVE! 3x points on your Captain match.")
                    }
                }
                "SAFETY_NET" -> {
                    // Enter interactive target fixture selection mode
                    _uiState.update { it.copy(targetSelectionChip = "SAFETY_NET") }
                    showMessage("Select a fixture to apply Safety Net (+3 pts minimum floor)")
                }
                "AUTO_CAPTAIN" -> {
                    repository.setAutoCaptain(user.id, gw)
                    showMessage("AUTO CAPTAIN ACTIVE! Highest-scoring match will automatically receive 2x Captain multiplier.")
                }
                "DOUBLE_SHOT" -> {
                    // Enter interactive target fixture selection mode
                    _uiState.update { it.copy(targetSelectionChip = "DOUBLE_SHOT") }
                    showMessage("Select a fixture to apply Double Shot (submit 2 score predictions)")
                }
            }
        }
    }

    /**
     * Called when a fixture card is clicked while in target selection mode (Safety Net or Double Shot)
     */
    fun onFixtureSelectedForChip(fixtureId: String) {
        val user = _uiState.value.currentUser ?: return
        val gw = _uiState.value.gameweek?.gwNumber ?: 4
        val pendingChip = _uiState.value.targetSelectionChip ?: return

        viewModelScope.launch {
            when (pendingChip) {
                "SAFETY_NET" -> {
                    repository.setSafetyNet(user.id, fixtureId, gw)
                    _uiState.update { it.copy(targetSelectionChip = null) }
                    showMessage("Safety Net applied! Minimum 3 points guaranteed for this match.")
                }
                "DOUBLE_SHOT" -> {
                    repository.setDoubleShot(user.id, fixtureId, gw)
                    _uiState.update { it.copy(targetSelectionChip = null) }
                    showMessage("Double Shot applied! Enter Prediction A and Prediction B for this match.")
                }
            }
        }
    }

    fun onCancelTargetSelection() {
        _uiState.update { it.copy(targetSelectionChip = null) }
        showMessage("Target selection cancelled")
    }

    /**
     * Submission Validation:
     * - Validates that at least one score is entered.
     * - Validates mandatory Captain selection (unless Auto Captain is active).
     */
    fun onSubmitPredictions() {
        val state = _uiState.value
        val preds = state.predictions.values.filter { it.homeScorePred != null && it.awayScorePred != null }
        if (preds.isEmpty()) {
            showMessage("Please enter at least one score prediction before submitting!")
            return
        }

        if (!state.isAutoCaptainActive && state.captainFixtureId == null) {
            showMessage("Mandatory: Please select ONE Captain (tap the 'C' badge on a match card)!")
            return
        }

        showMessage("Predictions submitted successfully for Gameweek ${state.gameweek?.gwNumber}!")
    }

    fun onCreateLeague(name: String, type: String) {
        if (name.isBlank()) {
            showMessage("Please enter a valid league name")
            return
        }
        viewModelScope.launch {
            val created = repository.createLeague(name.trim(), type)
            showMessage("League '${created.name}' created! Code: ${created.inviteCode}")
            _uiState.update { it.copy(isCreateLeagueOpen = false, selectedLeague = created) }
            observeLeagueDetails(created)
        }
    }

    fun onJoinLeague(code: String) {
        if (code.isBlank()) {
            showMessage("Please enter a 6-character invite code")
            return
        }
        viewModelScope.launch {
            val result = repository.joinLeagueByCode(code.trim().uppercase())
            result.onSuccess { league ->
                showMessage("Joined league '${league.name}' successfully!")
                _uiState.update { it.copy(isJoinLeagueOpen = false, selectedLeague = league) }
                observeLeagueDetails(league)
            }.onFailure { err ->
                showMessage(err.message ?: "Failed to join league")
            }
        }
    }

    fun onToggleDeadlineLock(locked: Boolean) {
        viewModelScope.launch {
            repository.toggleDeadlineLocked(locked)
            showMessage(if (locked) "Gameweek locked by Admin!" else "Gameweek unlocked by Admin!")
        }
    }

    fun onAdminUpdateScore(fixtureId: String, home: Int?, away: Int?, isFinished: Boolean) {
        viewModelScope.launch {
            repository.updateFixtureActualScore(fixtureId, home, away, isFinished)
        }
    }

    fun onSimulateAllMatchResults() {
        viewModelScope.launch {
            val fixtures = _uiState.value.fixtures
            val realisticScores = listOf(
                Pair(2, 1), Pair(1, 0), Pair(0, 0), Pair(3, 1),
                Pair(1, 1), Pair(2, 0), Pair(0, 2), Pair(2, 2),
                Pair(1, 2), Pair(3, 0), Pair(1, 3), Pair(0, 1)
            )

            fixtures.forEachIndexed { index, fix ->
                val score = realisticScores[index % realisticScores.size]
                repository.updateFixtureActualScore(fix.id, score.first, score.second, isFinished = true)
            }
            repository.toggleDeadlineLocked(true)
            repository.recalculateGameweekPoints(_uiState.value.gameweek?.gwNumber ?: 4)
            observeBreakdownData(_uiState.value.breakdownGwNumber)
            showMessage("Simulated all 2026/27 fixture results & recalculated scores!")
        }
    }

    fun onRecalculateScores() {
        viewModelScope.launch {
            repository.recalculateGameweekPoints(_uiState.value.gameweek?.gwNumber ?: 4)
            observeBreakdownData(_uiState.value.breakdownGwNumber)
            showMessage("Points & Standings recalculated successfully!")
        }
    }

    fun onForceSyncToGameweek4() {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded(forceReset = true)
            observeBreakdownData(3)
            showMessage("Synced to 2026/2027 Season: Gameweek 4 Predictions Open!")
        }
    }

    fun onSwitchFixtureScenario(scenario: String) {
        viewModelScope.launch {
            repository.switchFixtureScenario(scenario)
            showMessage("Scenario updated to $scenario Fixtures!")
        }
    }

    fun onLoginOrRegister(
        identifier: String,
        email: String?,
        displayName: String? = null,
        avatarUri: String? = null,
        presetCrestCode: String? = null
    ) {
        val finalId = identifier.ifBlank { displayName ?: "Manager" }.trim()
        if (finalId.isBlank()) {
            showMessage("Please enter a valid username or email")
            return
        }
        viewModelScope.launch {
            val user = repository.loginOrRegister(
                identifier = finalId,
                email = email?.trim(),
                displayName = displayName?.trim(),
                avatarUri = avatarUri,
                presetCrestCode = presetCrestCode
            )
            showMessage("Welcome, ${user.username}! Profile verified & ready.")
            _uiState.update { it.copy(isAuthDialogOpen = false) }
        }
    }

    fun onUpdateProfile(displayName: String, avatarUri: String?, presetCrestCode: String?) {
        viewModelScope.launch {
            repository.updateProfile(displayName.trim(), avatarUri, presetCrestCode)
            showMessage("Manager profile updated successfully!")
        }
    }

    fun setCreateLeagueOpen(open: Boolean) {
        _uiState.update { it.copy(isCreateLeagueOpen = open) }
    }

    fun setJoinLeagueOpen(open: Boolean) {
        _uiState.update { it.copy(isJoinLeagueOpen = open) }
    }

    fun setAuthDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isAuthDialogOpen = open) }
    }

    fun setAdminSheetOpen(open: Boolean) {
        _uiState.update { it.copy(isAdminSheetOpen = open) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private fun showMessage(msg: String) {
        _uiState.update { it.copy(snackbarMessage = msg) }
    }
}
