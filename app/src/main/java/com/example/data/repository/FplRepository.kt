package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.entity.FixtureEntity
import com.example.data.local.entity.GameweekEntity
import com.example.data.remote.api.FplApiService
import com.example.data.remote.dto.FplBootstrapDto
import com.example.data.remote.dto.FplFixtureDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

sealed class FplSyncState {
    object Idle : FplSyncState()
    object Loading : FplSyncState()
    data class Success(
        val gameweekNumber: Int,
        val gameweekName: String,
        val fixtureCount: Int,
        val deadlineEpochMs: Long,
        val isCached: Boolean = false,
        val lastSyncedAt: Long = System.currentTimeMillis()
    ) : FplSyncState()
    data class Error(
        val message: String,
        val hasCachedData: Boolean = false,
        val canRetry: Boolean = true
    ) : FplSyncState()
}

class FplRepository(
    context: Context,
    private val apiService: FplApiService = FplApiService.create(),
    private val database: AppDatabase = AppDatabase.getInstance(context)
) {
    private val gameweekDao = database.gameweekDao()
    private val fixtureDao = database.fixtureDao()

    private val _syncState = MutableStateFlow<FplSyncState>(FplSyncState.Idle)
    val syncState: StateFlow<FplSyncState> = _syncState.asStateFlow()

    fun getUpcomingGameweekFlow(): Flow<GameweekEntity?> = gameweekDao.getCurrentGameweekFlow()

    fun getFixturesFlow(gwNumber: Int): Flow<List<FixtureEntity>> = fixtureDao.getFixturesForGw(gwNumber)

    /**
     * Refreshes the upcoming gameweek schedule and fixtures from the official FPL public API.
     * Caches all events, deadlines, and fixture matchups into the local Room database.
     */
    suspend fun refreshUpcomingGameweek(force: Boolean = false): Result<GameweekEntity> = withContext(Dispatchers.IO) {
        _syncState.value = FplSyncState.Loading
        try {
            Log.d("FplRepository", "Connecting to FPL API: bootstrap-static...")
            val bootstrap = apiService.getBootstrapStatic()

            // 1. Identify the single "Upcoming Gameweek" where is_next = true (with resilient fallbacks)
            val upcomingEvent = bootstrap.events.find { it.isNext == true }
                ?: bootstrap.events.find { it.isCurrent == true && it.finished != true }
                ?: bootstrap.events.firstOrNull { it.finished != true }
                ?: bootstrap.events.lastOrNull()
                ?: throw IllegalStateException("No gameweek events found in FPL API response")

            Log.d("FplRepository", "Upcoming Gameweek detected: ${upcomingEvent.name} (id=${upcomingEvent.id})")

            // 2. Extract the exact deadline timestamp (deadline_time or epoch)
            val deadlineEpochMs = upcomingEvent.deadlineTimeEpoch?.let { it * 1000L }
                ?: parseIsoTimestamp(upcomingEvent.deadlineTime)
                ?: (System.currentTimeMillis() + 86400000L)

            // 3. Map team IDs to team names and short codes using the teams array
            val teamMap = bootstrap.teams.associateBy { it.id }

            // 4. Fetch all fixtures scheduled for that specific upcoming gameweek
            Log.d("FplRepository", "Connecting to FPL API: fixtures for event ${upcomingEvent.id}...")
            val fplFixtures = apiService.getFixtures(upcomingEvent.id)
            Log.d("FplRepository", "Successfully fetched ${fplFixtures.size} fixtures from FPL")

            val sortedFixtures = fplFixtures.sortedBy { dto ->
                parseIsoTimestamp(dto.kickoffTime) ?: (deadlineEpochMs + 5400000L)
            }

            // Track teams playing multiple times in this gameweek (Double Gameweek detection)
            val teamAppearanceCount = mutableMapOf<Int, Int>()
            sortedFixtures.forEach { dto ->
                teamAppearanceCount[dto.teamH] = (teamAppearanceCount[dto.teamH] ?: 0) + 1
                teamAppearanceCount[dto.teamA] = (teamAppearanceCount[dto.teamA] ?: 0) + 1
            }

            val seenTeams = mutableSetOf<Int>()
            val fixtureEntities = sortedFixtures.mapIndexed { index, dto ->
                val homeTeam = teamMap[dto.teamH]?.name ?: "Home Team (${dto.teamH})"
                val homeCode = teamMap[dto.teamH]?.shortName ?: "HOM"
                val awayTeam = teamMap[dto.teamA]?.name ?: "Away Team (${dto.teamA})"
                val awayCode = teamMap[dto.teamA]?.shortName ?: "AWA"

                val isDgwExtra = seenTeams.contains(dto.teamH) || seenTeams.contains(dto.teamA)
                seenTeams.add(dto.teamH)
                seenTeams.add(dto.teamA)

                val kickoffEpochMs = parseIsoTimestamp(dto.kickoffTime)
                    ?: (deadlineEpochMs + (index + 1) * 3600000L)

                val kickoffDisplay = formatKickoffDisplay(kickoffEpochMs, dto.finished, dto.started, dto.minutes)

                val homeStrength = teamMap[dto.teamH]?.strength ?: 3
                val awayStrength = teamMap[dto.teamA]?.strength ?: 3
                val (homeWinPct, drawPct, awayWinPct) = calculateProbabilities(homeStrength, awayStrength)

                FixtureEntity(
                    id = "fpl_fix_${dto.id}",
                    gwNumber = upcomingEvent.id,
                    homeTeam = homeTeam,
                    homeCode = homeCode,
                    awayTeam = awayTeam,
                    awayCode = awayCode,
                    kickoffEpochMs = kickoffEpochMs,
                    kickoffTimeDisplay = kickoffDisplay,
                    homeScoreActual = dto.teamHScore,
                    awayScoreActual = dto.teamAScore,
                    isFinished = dto.finished,
                    homeWinPct = homeWinPct,
                    drawPct = drawPct,
                    awayWinPct = awayWinPct,
                    isDoubleGameweekExtra = isDgwExtra
                )
            }

            val firstKickoffMs = fixtureEntities.minOfOrNull { it.kickoffEpochMs }
                ?: (deadlineEpochMs + 3600000L)

            // Strict 60-Minute Deadline: exactly 60 minutes prior to first fixture kickoff
            val strictDeadlineMs = firstKickoffMs - 3600000L

            val fixtureCountType = when {
                fixtureEntities.size > 10 -> "DOUBLE"
                fixtureEntities.size < 10 -> "BLANK"
                else -> "STANDARD"
            }

            val gameweekEntity = GameweekEntity(
                gwNumber = upcomingEvent.id,
                season = "2024/2025",
                name = upcomingEvent.name,
                deadlineEpochMs = strictDeadlineMs,
                kickoffFirstMatchMs = firstKickoffMs,
                isDeadlinePassed = System.currentTimeMillis() >= strictDeadlineMs,
                isEvaluated = upcomingEvent.finished == true,
                isNext = true,
                fixtureCountType = fixtureCountType
            )

            // Cache to local Room database
            gameweekDao.clearAllIsNext()
            gameweekDao.insertGameweek(gameweekEntity)
            fixtureDao.deleteFixturesForGw(upcomingEvent.id)
            fixtureDao.insertFixtures(fixtureEntities)

            _syncState.value = FplSyncState.Success(
                gameweekNumber = upcomingEvent.id,
                gameweekName = upcomingEvent.name,
                fixtureCount = fixtureEntities.size,
                deadlineEpochMs = deadlineEpochMs,
                isCached = false,
                lastSyncedAt = System.currentTimeMillis()
            )

            Result.success(gameweekEntity)
        } catch (e: Exception) {
            Log.e("FplRepository", "Network exception when fetching FPL data", e)

            val currentGw = gameweekDao.getCurrentGameweek()
            val cachedFixtures = if (currentGw != null) {
                fixtureDao.getFixturesForGwList(currentGw.gwNumber)
            } else emptyList()

            val hasCachedData = cachedFixtures.isNotEmpty()
            val errorMessage = if (hasCachedData) {
                "Offline: Using saved fixtures"
            } else {
                "Unable to connect to Premier League API. Check your internet connection."
            }

            _syncState.value = FplSyncState.Error(
                message = errorMessage,
                hasCachedData = hasCachedData,
                canRetry = true
            )
            Result.failure(e)
        }
    }

    private fun calculateProbabilities(homeStrength: Int, awayStrength: Int): Triple<Int, Int, Int> {
        val diff = homeStrength - awayStrength
        return when {
            diff >= 2 -> Triple(65, 20, 15)
            diff == 1 -> Triple(52, 26, 22)
            diff == 0 -> Triple(42, 28, 30)
            diff == -1 -> Triple(30, 26, 44)
            else -> Triple(18, 22, 60)
        }
    }

    companion object {
        fun parseIsoTimestamp(isoString: String?): Long? {
            if (isoString.isNullOrBlank()) return null
            return try {
                Instant.parse(isoString).toEpochMilli()
            } catch (e: Exception) {
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    sdf.parse(isoString)?.time
                } catch (e2: Exception) {
                    try {
                        val sdf2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
                        sdf2.parse(isoString)?.time
                    } catch (e3: Exception) {
                        null
                    }
                }
            }
        }

        fun formatKickoffDisplay(
            epochMs: Long,
            isFinished: Boolean = false,
            isStarted: Boolean? = false,
            minutes: Int? = 0
        ): String {
            if (isFinished) return "FT"
            if (isStarted == true) return "${minutes ?: 0}'"
            val zonedDateTime = Instant.ofEpochMilli(epochMs).atZone(ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("EEE HH:mm", Locale.getDefault())
            return zonedDateTime.format(formatter)
        }
    }
}
