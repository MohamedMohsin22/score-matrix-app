package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.ChipEntity
import com.example.data.local.entity.FixtureEntity
import com.example.data.local.entity.GameweekEntity
import com.example.data.local.entity.H2HMatchupEntity
import com.example.data.local.entity.LeagueEntity
import com.example.data.local.entity.LeagueMemberEntity
import com.example.data.local.entity.PredictionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.BadgeEvaluationEngine
import com.example.data.model.MatchScoringEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import kotlin.random.Random

class AppRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val userDao = db.userDao()
    private val gameweekDao = db.gameweekDao()
    private val fixtureDao = db.fixtureDao()
    private val predictionDao = db.predictionDao()
    private val chipDao = db.chipDao()
    private val leagueDao = db.leagueDao()
    private val badgeDao = db.badgeDao()

    val fplRepository = FplRepository(context)
    val fplSyncState = fplRepository.syncState

    val currentUserFlow: Flow<UserEntity?> = userDao.getCurrentUserFlow()
    val gameweekFlow: Flow<GameweekEntity?> = gameweekDao.getCurrentGameweekFlow()
    val allLeaguesFlow: Flow<List<LeagueEntity>> = leagueDao.getAllLeagues()

    fun getBadgesFlow(userId: String): Flow<List<BadgeEntity>> =
        badgeDao.getBadgesForUser(userId)

    suspend fun refreshFplFixtures(force: Boolean = false): Result<GameweekEntity> {
        return fplRepository.refreshUpcomingGameweek(force)
    }

    fun getFixturesFlow(gwNumber: Int): Flow<List<FixtureEntity>> =
        fixtureDao.getFixturesForGw(gwNumber)

    fun getPredictionsFlow(userId: String, gwNumber: Int): Flow<List<PredictionEntity>> =
        predictionDao.getPredictionsForUserAndGw(userId, gwNumber)

    fun getChipsFlow(userId: String): Flow<List<ChipEntity>> =
        chipDao.getChipsForUser(userId)

    fun getLeaguesForUser(userId: String): Flow<List<LeagueEntity>> =
        leagueDao.getLeaguesForUser(userId)

    fun getClassicStandings(leagueId: String): Flow<List<LeagueMemberEntity>> =
        leagueDao.getClassicStandings(leagueId)

    fun getH2HStandings(leagueId: String): Flow<List<LeagueMemberEntity>> =
        leagueDao.getH2HStandings(leagueId)

    fun getH2HMatchups(leagueId: String, gwNumber: Int): Flow<List<H2HMatchupEntity>> =
        leagueDao.getH2HMatchups(leagueId, gwNumber)

    suspend fun initializeSeedDataIfNeeded(forceReset: Boolean = false) {
        val currentGw = gameweekDao.getCurrentGameweek()
        val currentGw4Fixtures = fixtureDao.getFixturesForGwList(4)
        val hasWrongFixtures = currentGw4Fixtures.isEmpty() || currentGw4Fixtures.any { it.homeTeam == "Southampton" || it.awayTeam == "Nottingham Forest" }
        if (!forceReset && currentGw != null && currentGw.gwNumber == 4 && !hasWrongFixtures) {
            ensureBadgesInitialized("user_primary")
            return
        }

        if (hasWrongFixtures || forceReset) {
            fixtureDao.deleteFixturesForGw(4)
        }

        val now = System.currentTimeMillis()
        val kickoffFirstMatch = now + (26 * 3600 * 1000L) // upcoming round, first kickoff
        val deadline = kickoffFirstMatch - (60 * 60 * 1000L) // strictly 60 mins before first match kickoff

        // Active Gameweek 4 (Upcoming round to predict)
        val gw4 = GameweekEntity(
            gwNumber = 4,
            season = "2026/2027",
            deadlineEpochMs = deadline,
            kickoffFirstMatchMs = kickoffFirstMatch,
            isDeadlinePassed = false,
            isEvaluated = false,
            fixtureCountType = "STANDARD"
        )

        // Gameweek 3 (Just finished/evaluated round)
        val gw3 = GameweekEntity(
            gwNumber = 3,
            season = "2026/2027",
            deadlineEpochMs = now - (48 * 3600 * 1000L),
            kickoffFirstMatchMs = now - (47 * 3600 * 1000L),
            isDeadlinePassed = true,
            isEvaluated = true,
            fixtureCountType = "STANDARD"
        )
        gameweekDao.insertGameweeks(listOf(gw3, gw4))

        // Seed Users with early-season totals (after GW 1, 2, and 3)
        val primaryUser = UserEntity(
            id = "user_primary",
            username = "PremierMaster",
            email = "premier.fan@pl2026.com",
            totalScore = 68,
            currentGwScore = 22,
            overallRank = 4,
            isCurrentUser = true
        )

        val demoOpponents = listOf(
            UserEntity("user_opp1", "DeclanSpecial", "declan@arsenal.com", 76, 26, 1, false),
            UserEntity("user_opp2", "KloppGegenpress", "klopp@anfield.com", 73, 23, 2, false),
            UserEntity("user_opp3", "HaalandBorg", "erling@mancity.com", 70, 21, 3, false),
            UserEntity("user_opp4", "ArtetaVisions", "mikel@london.com", 65, 18, 5, false),
            UserEntity("user_opp5", "ColePalmerIce", "cole@chelsea.com", 62, 19, 6, false),
            UserEntity("user_opp6", "VillaEmeryMagic", "unai@villa.com", 59, 17, 7, false),
            UserEntity("user_opp7", "ToonArmyHowe", "eddie@newcastle.com", 55, 15, 8, false)
        )

        userDao.insertUsers(listOf(primaryUser) + demoOpponents)

        // Seed Chips: 4 Chips available for Season Half 1 (GW 1-19) and Half 2 (GW 20-38)
        val chips = listOf(
            // Half 1 (Available for active upcoming GW 4!)
            ChipEntity("${primaryUser.id}_SUPER_CAPTAIN_1", primaryUser.id, "SUPER_CAPTAIN", half = 1, usedInGw = null, isUsed = false),
            ChipEntity("${primaryUser.id}_SAFETY_NET_1", primaryUser.id, "SAFETY_NET", half = 1, usedInGw = null, isUsed = false),
            ChipEntity("${primaryUser.id}_AUTO_CAPTAIN_1", primaryUser.id, "AUTO_CAPTAIN", half = 1, usedInGw = null, isUsed = false),
            ChipEntity("${primaryUser.id}_DOUBLE_SHOT_1", primaryUser.id, "DOUBLE_SHOT", half = 1, usedInGw = null, isUsed = false),
            // Half 2 (Reserved for GW 20-38)
            ChipEntity("${primaryUser.id}_SUPER_CAPTAIN_2", primaryUser.id, "SUPER_CAPTAIN", half = 2, usedInGw = null, isUsed = false),
            ChipEntity("${primaryUser.id}_SAFETY_NET_2", primaryUser.id, "SAFETY_NET", half = 2, usedInGw = null, isUsed = false),
            ChipEntity("${primaryUser.id}_AUTO_CAPTAIN_2", primaryUser.id, "AUTO_CAPTAIN", half = 2, usedInGw = null, isUsed = false),
            ChipEntity("${primaryUser.id}_DOUBLE_SHOT_2", primaryUser.id, "DOUBLE_SHOT", half = 2, usedInGw = null, isUsed = false)
        )
        chipDao.insertChips(chips)

        // Seed Standard 10 Fixtures for GW 4 (Upcoming - predictions active)
        val fixturesGw4 = createStandardFixtures(4, kickoffFirstMatch)
        fixtureDao.insertFixtures(fixturesGw4)

        // Seed Gameweek 3 Fixtures & User Predictions (Completed - points breakdown available)
        val fixturesGw3 = createGameweek3Fixtures(kickoffFirstMatch)
        fixtureDao.insertFixtures(fixturesGw3)
        val predictionsGw3 = createGameweek3Predictions(primaryUser.id)
        predictionDao.insertPredictions(predictionsGw3)

        // Seed Default Leagues (One Classic, One H2H)
        val classicLeague = LeagueEntity(
            id = "league_classic_1",
            name = "Premier League Global Top 100",
            type = "CLASSIC",
            inviteCode = "PL26K8",
            creatorId = "user_opp1",
            createdAt = now - 86400000L * 14
        )

        val h2hLeague = LeagueEntity(
            id = "league_h2h_1",
            name = "Super 8 Championship H2H",
            type = "H2H",
            inviteCode = "H2H7X4",
            creatorId = "user_primary",
            createdAt = now - 86400000L * 14
        )

        leagueDao.insertLeague(classicLeague)
        leagueDao.insertLeague(h2hLeague)

        // Seed League Members
        val allUsers = listOf(primaryUser) + demoOpponents

        val classicMembers = allUsers.mapIndexed { index, user ->
            LeagueMemberEntity(
                id = "${classicLeague.id}_${user.id}",
                leagueId = classicLeague.id,
                userId = user.id,
                userName = user.username,
                totalPoints = user.totalScore,
                gwPoints = user.currentGwScore,
                isGwMvp = (index == 0) // Opp1 was GW 3 MVP
            )
        }
        leagueDao.insertLeagueMembers(classicMembers)

        // Seed H2H Standings
        val h2hMembers = allUsers.mapIndexed { index, user ->
            val played = 3
            val won = when (index) {
                0 -> 3
                1 -> 2
                2 -> 2
                3 -> 2
                4 -> 1
                5 -> 1
                6 -> 1
                else -> 0
            }
            val drawn = if (index == 4 || index == 5) 1 else 0
            val lost = (played - won - drawn).coerceAtLeast(0)
            val pts = (won * 3) + drawn
            LeagueMemberEntity(
                id = "${h2hLeague.id}_${user.id}",
                leagueId = h2hLeague.id,
                userId = user.id,
                userName = user.username,
                totalPoints = user.totalScore,
                gwPoints = user.currentGwScore,
                h2hWon = won,
                h2hDrawn = drawn,
                h2hLost = lost,
                h2hPoints = pts,
                pointsDiff = (won - lost) * 4,
                isGwMvp = (index == 0)
            )
        }
        leagueDao.insertLeagueMembers(h2hMembers)

        // Seed H2H pairings for GW 4
        val matchups = listOf(
            H2HMatchupEntity("h2h_gw4_1", h2hLeague.id, 4, primaryUser.id, primaryUser.username, 0, "user_opp1", "DeclanSpecial", 0),
            H2HMatchupEntity("h2h_gw4_2", h2hLeague.id, 4, "user_opp2", "KloppGegenpress", 0, "user_opp3", "HaalandBorg", 0),
            H2HMatchupEntity("h2h_gw4_3", h2hLeague.id, 4, "user_opp4", "ArtetaVisions", 0, "user_opp5", "ColePalmerIce", 0),
            H2HMatchupEntity("h2h_gw4_4", h2hLeague.id, 4, "user_opp6", "VillaEmeryMagic", 0, "user_opp7", "ToonArmyHowe", 0)
        )
        leagueDao.insertH2HMatchups(matchups)

        // Seed Initial Badges for Primary User
        val primaryBadges = BadgeEvaluationEngine.createInitialBadgesForUser(primaryUser.id)
        badgeDao.insertBadges(primaryBadges)
    }

    private fun createGameweek3Fixtures(kickoffBase: Long): List<FixtureEntity> {
        val hour = 3600 * 1000L
        return listOf(
            FixtureEntity("fix_gw3_1", 3, "Arsenal", "ARS", "Brighton", "BHA", kickoffBase - 48 * hour, "FT", 1, 1, true, 58, 24, 18),
            FixtureEntity("fix_gw3_2", 3, "Brentford", "BRE", "Southampton", "SOU", kickoffBase - 46 * hour, "FT", 3, 1, true, 52, 26, 22),
            FixtureEntity("fix_gw3_3", 3, "Everton", "EVE", "Bournemouth", "BOU", kickoffBase - 46 * hour, "FT", 2, 3, true, 40, 30, 30),
            FixtureEntity("fix_gw3_4", 3, "Ipswich Town", "IPS", "Fulham", "FUL", kickoffBase - 46 * hour, "FT", 1, 1, true, 35, 33, 32),
            FixtureEntity("fix_gw3_5", 3, "Leicester City", "LEI", "Aston Villa", "AVL", kickoffBase - 46 * hour, "FT", 1, 2, true, 28, 28, 44),
            FixtureEntity("fix_gw3_6", 3, "Nottingham Forest", "NFO", "Wolverhampton", "WOL", kickoffBase - 46 * hour, "FT", 1, 1, true, 42, 30, 28),
            FixtureEntity("fix_gw3_7", 3, "West Ham", "WHU", "Manchester City", "MCI", kickoffBase - 44 * hour, "FT", 1, 3, true, 18, 22, 60),
            FixtureEntity("fix_gw3_8", 3, "Chelsea", "CHE", "Crystal Palace", "CRY", kickoffBase - 24 * hour, "FT", 1, 1, true, 55, 25, 20),
            FixtureEntity("fix_gw3_9", 3, "Newcastle United", "NEW", "Tottenham Hotspur", "TOT", kickoffBase - 24 * hour, "FT", 2, 1, true, 45, 25, 30),
            FixtureEntity("fix_gw3_10", 3, "Manchester United", "MUN", "Liverpool", "LIV", kickoffBase - 22 * hour, "FT", 0, 3, true, 30, 25, 45)
        )
    }

    private fun createGameweek3Predictions(userId: String): List<PredictionEntity> {
        return listOf(
            PredictionEntity("${userId}_fix_gw3_1", userId, "fix_gw3_1", 3, 2, 1, isCaptain = false, pointsEarned = 0, breakdownText = "Incorrect outcome (0 pts)"),
            PredictionEntity("${userId}_fix_gw3_2", userId, "fix_gw3_2", 3, 2, 0, isCaptain = false, pointsEarned = 1, breakdownText = "Correct outcome Win (+1 pt)"),
            PredictionEntity("${userId}_fix_gw3_3", userId, "fix_gw3_3", 3, 1, 1, isCaptain = false, pointsEarned = 0, breakdownText = "Incorrect outcome (0 pts)"),
            PredictionEntity("${userId}_fix_gw3_4", userId, "fix_gw3_4", 3, 1, 1, isCaptain = false, pointsEarned = 3, breakdownText = "Exact Score (+3 pts)"),
            PredictionEntity("${userId}_fix_gw3_5", userId, "fix_gw3_5", 3, 1, 2, isCaptain = false, pointsEarned = 3, breakdownText = "Exact Score (+3 pts)"),
            PredictionEntity("${userId}_fix_gw3_6", userId, "fix_gw3_6", 3, 1, 0, isCaptain = false, pointsEarned = 0, breakdownText = "Incorrect outcome (0 pts)"),
            PredictionEntity("${userId}_fix_gw3_7", userId, "fix_gw3_7", 3, 1, 3, isCaptain = true, pointsEarned = 6, breakdownText = "Captain 2x! Exact Score (+3 x 2 = 6 pts)"),
            PredictionEntity("${userId}_fix_gw3_8", userId, "fix_gw3_8", 3, 2, 1, isCaptain = false, pointsEarned = 0, breakdownText = "Incorrect outcome (0 pts)"),
            PredictionEntity("${userId}_fix_gw3_9", userId, "fix_gw3_9", 3, 2, 1, isCaptain = false, pointsEarned = 3, breakdownText = "Exact Score (+3 pts)"),
            PredictionEntity("${userId}_fix_gw3_10", userId, "fix_gw3_10", 3, 0, 3, isCaptain = false, pointsEarned = 4, breakdownText = "Exact Score (+3 pts) + Clean Sheet Bonus (+1 pt) = 4 pts")
        )
    }

    private fun createStandardFixtures(gwNumber: Int, kickoffBase: Long): List<FixtureEntity> {
        val hour = 3600 * 1000L
        return listOf(
            FixtureEntity("fix_gw4_1", gwNumber, "Aston Villa", "AVL", "Nott'm Forest", "NFO", kickoffBase, "Sat 17:00", null, null, false, 58, 24, 18),
            FixtureEntity("fix_gw4_2", gwNumber, "Bournemouth", "BOU", "Brentford", "BRE", kickoffBase, "Sat 17:00", null, null, false, 42, 28, 30),
            FixtureEntity("fix_gw4_3", gwNumber, "Chelsea", "CHE", "Hull City", "HUL", kickoffBase, "Sat 17:00", null, null, false, 75, 16, 9),
            FixtureEntity("fix_gw4_4", gwNumber, "Crystal Palace", "CRY", "Ipswich Town", "IPS", kickoffBase, "Sat 17:00", null, null, false, 52, 26, 22),
            FixtureEntity("fix_gw4_5", gwNumber, "Liverpool", "LIV", "Fulham", "FUL", kickoffBase, "Sat 17:00", null, null, false, 78, 14, 8),
            FixtureEntity("fix_gw4_6", gwNumber, "Spurs", "TOT", "Everton", "EVE", kickoffBase + 2 * hour + 30 * 60000L, "Sat 19:30", null, null, false, 62, 22, 16),
            FixtureEntity("fix_gw4_7", gwNumber, "Sunderland", "SUN", "Arsenal", "ARS", kickoffBase + 5 * hour, "Sat 22:00", null, null, false, 15, 22, 63),
            FixtureEntity("fix_gw4_8", gwNumber, "Coventry City", "COV", "Brighton", "BHA", kickoffBase + 23 * hour, "Sun 16:00", null, null, false, 22, 28, 50),
            FixtureEntity("fix_gw4_9", gwNumber, "Man Utd", "MUN", "Man City", "MCI", kickoffBase + 25 * hour + 30 * 60000L, "Sun 18:30", null, null, false, 34, 26, 40),
            FixtureEntity("fix_gw4_10", gwNumber, "Leeds", "LEE", "Newcastle", "NEW", kickoffBase + 53 * hour, "Mon 22:00", null, null, false, 28, 26, 46)
        )
    }

    suspend fun savePrediction(
        userId: String,
        fixtureId: String,
        gwNumber: Int,
        homePred: Int?,
        awayPred: Int?
    ) {
        val existing = predictionDao.getPrediction(userId, fixtureId)
        val id = "${userId}_${fixtureId}"
        val updated = existing?.copy(
            homeScorePred = homePred,
            awayScorePred = awayPred,
            updatedAt = System.currentTimeMillis()
        ) ?: PredictionEntity(
            id = id,
            userId = userId,
            fixtureId = fixtureId,
            gwNumber = gwNumber,
            homeScorePred = homePred,
            awayScorePred = awayPred,
            updatedAt = System.currentTimeMillis()
        )
        predictionDao.insertOrUpdatePrediction(updated)
    }

    suspend fun savePredictionB(
        userId: String,
        fixtureId: String,
        gwNumber: Int,
        homePredB: Int?,
        awayPredB: Int?
    ) {
        val existing = predictionDao.getPrediction(userId, fixtureId)
        val id = "${userId}_${fixtureId}"
        val updated = (existing ?: PredictionEntity(
            id = id,
            userId = userId,
            fixtureId = fixtureId,
            gwNumber = gwNumber
        )).copy(
            isDoubleShot = true,
            homeScorePredB = homePredB,
            awayScorePredB = awayPredB,
            updatedAt = System.currentTimeMillis()
        )
        predictionDao.insertOrUpdatePrediction(updated)
    }

    suspend fun setCaptain(userId: String, fixtureId: String, gwNumber: Int, isSuper: Boolean) {
        // Clear previous captain for this gameweek (Single Strict Captain Engine)
        predictionDao.clearCaptainForGw(userId, gwNumber)
        // Clear Auto Captain if active
        predictionDao.clearAutoCaptainForGw(userId, gwNumber)

        val existing = predictionDao.getPrediction(userId, fixtureId)
        val updated = (existing ?: PredictionEntity(
            id = "${userId}_${fixtureId}",
            userId = userId,
            fixtureId = fixtureId,
            gwNumber = gwNumber
        )).copy(
            isCaptain = !isSuper,
            isSuperCaptain = isSuper
        )
        predictionDao.insertOrUpdatePrediction(updated)
    }

    suspend fun removeCaptain(userId: String, gwNumber: Int) {
        predictionDao.clearCaptainForGw(userId, gwNumber)
    }

    suspend fun setSafetyNet(userId: String, fixtureId: String, gwNumber: Int) {
        // One Chip per Gameweek Rule: Clear any other active chips
        predictionDao.clearSafetyNetForGw(userId, gwNumber)
        predictionDao.clearDoubleShotForGw(userId, gwNumber)
        predictionDao.clearAutoCaptainForGw(userId, gwNumber)
        // If super captain was set, revert to standard captain
        val currentCaptain = predictionDao.getPredictionsList(userId, gwNumber).find { it.isSuperCaptain }
        if (currentCaptain != null) {
            predictionDao.insertOrUpdatePrediction(currentCaptain.copy(isCaptain = true, isSuperCaptain = false))
        }

        val existing = predictionDao.getPrediction(userId, fixtureId)
        val updated = (existing ?: PredictionEntity(
            id = "${userId}_${fixtureId}",
            userId = userId,
            fixtureId = fixtureId,
            gwNumber = gwNumber
        )).copy(
            isSafetyNet = true
        )
        predictionDao.insertOrUpdatePrediction(updated)
    }

    suspend fun removeSafetyNet(userId: String, gwNumber: Int) {
        predictionDao.clearSafetyNetForGw(userId, gwNumber)
    }

    suspend fun setDoubleShot(userId: String, fixtureId: String, gwNumber: Int) {
        // One Chip per Gameweek Rule: Clear any other active chips
        predictionDao.clearSafetyNetForGw(userId, gwNumber)
        predictionDao.clearDoubleShotForGw(userId, gwNumber)
        predictionDao.clearAutoCaptainForGw(userId, gwNumber)
        // If super captain was set, revert to standard captain
        val currentCaptain = predictionDao.getPredictionsList(userId, gwNumber).find { it.isSuperCaptain }
        if (currentCaptain != null) {
            predictionDao.insertOrUpdatePrediction(currentCaptain.copy(isCaptain = true, isSuperCaptain = false))
        }

        val existing = predictionDao.getPrediction(userId, fixtureId)
        val defHomeB = if (existing?.homeScorePred != null) existing.homeScorePred else 1
        val defAwayB = if (existing?.awayScorePred != null) {
            if (existing.awayScorePred == 0) 1 else 0
        } else 0

        val updated = (existing ?: PredictionEntity(
            id = "${userId}_${fixtureId}",
            userId = userId,
            fixtureId = fixtureId,
            gwNumber = gwNumber
        )).copy(
            isDoubleShot = true,
            homeScorePredB = existing?.homeScorePredB ?: defHomeB,
            awayScorePredB = existing?.awayScorePredB ?: defAwayB
        )
        predictionDao.insertOrUpdatePrediction(updated)
    }

    suspend fun removeDoubleShot(userId: String, gwNumber: Int) {
        predictionDao.clearDoubleShotForGw(userId, gwNumber)
    }

    suspend fun setAutoCaptain(userId: String, gwNumber: Int) {
        // One Chip per Gameweek Rule & Auto Captain rules:
        // Disables manual captain pick, clears previous captain & other chips
        predictionDao.clearCaptainForGw(userId, gwNumber)
        predictionDao.clearSafetyNetForGw(userId, gwNumber)
        predictionDao.clearDoubleShotForGw(userId, gwNumber)

        val preds = predictionDao.getPredictionsList(userId, gwNumber)
        if (preds.isNotEmpty()) {
            val updated = preds.map { it.copy(isAutoCaptain = true, isCaptain = false, isSuperCaptain = false) }
            predictionDao.insertPredictions(updated)
        } else {
            // Seed marker prediction
            val marker = PredictionEntity(
                id = "${userId}_autocaptain_marker",
                userId = userId,
                fixtureId = "marker",
                gwNumber = gwNumber,
                isAutoCaptain = true
            )
            predictionDao.insertOrUpdatePrediction(marker)
        }
    }

    suspend fun removeAutoCaptain(userId: String, gwNumber: Int) {
        predictionDao.clearAutoCaptainForGw(userId, gwNumber)
    }

    suspend fun deactivateAllChipsForGw(userId: String, gwNumber: Int) {
        predictionDao.clearSafetyNetForGw(userId, gwNumber)
        predictionDao.clearDoubleShotForGw(userId, gwNumber)
        predictionDao.clearAutoCaptainForGw(userId, gwNumber)
        val currentCaptain = predictionDao.getPredictionsList(userId, gwNumber).find { it.isSuperCaptain }
        if (currentCaptain != null) {
            predictionDao.insertOrUpdatePrediction(currentCaptain.copy(isCaptain = true, isSuperCaptain = false))
        }
    }

    suspend fun toggleDeadlineLocked(isLocked: Boolean) {
        val gw = gameweekDao.getCurrentGameweek() ?: return
        gameweekDao.updateGameweek(gw.copy(isDeadlinePassed = isLocked))
    }

    suspend fun updateFixtureActualScore(
        fixtureId: String,
        homeScore: Int?,
        awayScore: Int?,
        isFinished: Boolean
    ) {
        val fix = fixtureDao.getFixtureById(fixtureId) ?: return
        val updated = fix.copy(
            homeScoreActual = homeScore,
            awayScoreActual = awayScore,
            isFinished = isFinished
        )
        fixtureDao.updateFixture(updated)
    }

    suspend fun recalculateGameweekPoints(gwNumber: Int) {
        val fixtures = fixtureDao.getFixturesForGwList(gwNumber)
        val users = listOfNotNull(userDao.getCurrentUser()) + listOf(
            UserEntity("user_opp1", "DeclanSpecial", "declan@arsenal.com", 76, 0, 1, false),
            UserEntity("user_opp2", "KloppGegenpress", "klopp@anfield.com", 73, 0, 2, false),
            UserEntity("user_opp3", "HaalandBorg", "erling@mancity.com", 70, 0, 3, false),
            UserEntity("user_opp4", "ArtetaVisions", "mikel@london.com", 65, 0, 5, false),
            UserEntity("user_opp5", "ColePalmerIce", "cole@chelsea.com", 62, 0, 6, false),
            UserEntity("user_opp6", "VillaEmeryMagic", "unai@villa.com", 59, 0, 7, false),
            UserEntity("user_opp7", "ToonArmyHowe", "eddie@newcastle.com", 55, 0, 8, false)
        )

        val userScores = mutableMapOf<String, Int>()

        // 1. Calculate Primary User predictions
        val currentUser = userDao.getCurrentUser()
        if (currentUser != null) {
            val userPreds = predictionDao.getPredictionsList(currentUser.id, gwNumber).filter { it.fixtureId != "marker" }
            var totalGwPts = 0

            val isAutoCaptainActive = userPreds.any { it.isAutoCaptain }

            // Auto Captain Engine: If active, dynamically find the fixture that scores the highest natural points
            var autoCaptainFixtureId: String? = null
            if (isAutoCaptainActive && userPreds.isNotEmpty()) {
                var maxBasePts = -1
                for (fix in fixtures) {
                    val pred = userPreds.find { it.fixtureId == fix.id }
                    if (pred != null && fix.homeScoreActual != null && fix.awayScoreActual != null) {
                        val baseScoring = MatchScoringEngine.calculatePoints(
                            predHome = pred.homeScorePred,
                            predAway = pred.awayScorePred,
                            actualHome = fix.homeScoreActual,
                            actualAway = fix.awayScoreActual,
                            isCaptain = false,
                            isSuperCaptain = false,
                            isSafetyNet = pred.isSafetyNet,
                            isDoubleShot = pred.isDoubleShot,
                            predHomeB = pred.homeScorePredB,
                            predAwayB = pred.awayScorePredB
                        )
                        if (baseScoring.totalPoints > maxBasePts) {
                            maxBasePts = baseScoring.totalPoints
                            autoCaptainFixtureId = fix.id
                        }
                    }
                }
                if (autoCaptainFixtureId == null) {
                    autoCaptainFixtureId = userPreds.firstOrNull()?.fixtureId
                }
            }

            val updatedPreds = mutableListOf<PredictionEntity>()
            for (fix in fixtures) {
                val pred = userPreds.find { it.fixtureId == fix.id }
                if (pred != null) {
                    val isThisCaptain = if (isAutoCaptainActive) {
                        fix.id == autoCaptainFixtureId
                    } else {
                        pred.isCaptain
                    }

                    val scoring = MatchScoringEngine.calculatePoints(
                        predHome = pred.homeScorePred,
                        predAway = pred.awayScorePred,
                        actualHome = fix.homeScoreActual,
                        actualAway = fix.awayScoreActual,
                        isCaptain = isThisCaptain,
                        isSuperCaptain = pred.isSuperCaptain,
                        isSafetyNet = pred.isSafetyNet,
                        isDoubleShot = pred.isDoubleShot,
                        predHomeB = pred.homeScorePredB,
                        predAwayB = pred.awayScorePredB
                    )
                    totalGwPts += scoring.totalPoints

                    val breakdownFinal = if (isAutoCaptainActive && isThisCaptain) {
                        "${scoring.breakdownText} • Auto Captain 2x Bonus"
                    } else {
                        scoring.breakdownText
                    }

                    updatedPreds.add(
                        pred.copy(
                            isCaptain = isThisCaptain,
                            pointsEarned = scoring.totalPoints,
                            breakdownText = breakdownFinal
                        )
                    )
                }
            }
            predictionDao.insertPredictions(updatedPreds)
            val updatedUser = currentUser.copy(
                currentGwScore = totalGwPts,
                totalScore = currentUser.totalScore + totalGwPts
            )
            userDao.updateUser(updatedUser)
            userScores[currentUser.id] = totalGwPts

            // Mark used chips in seasonal quota
            val half = if (gwNumber <= 19) 1 else 2
            val hasSuperCaptain = updatedPreds.any { it.isSuperCaptain }
            val hasSafetyNet = updatedPreds.any { it.isSafetyNet }
            val hasDoubleShot = updatedPreds.any { it.isDoubleShot }

            if (hasSuperCaptain) {
                chipDao.getChip(currentUser.id, "SUPER_CAPTAIN", half)?.let { chip ->
                    chipDao.updateChip(chip.copy(isUsed = true, usedInGw = gwNumber))
                }
            }
            if (hasSafetyNet) {
                chipDao.getChip(currentUser.id, "SAFETY_NET", half)?.let { chip ->
                    chipDao.updateChip(chip.copy(isUsed = true, usedInGw = gwNumber))
                }
            }
            if (isAutoCaptainActive) {
                chipDao.getChip(currentUser.id, "AUTO_CAPTAIN", half)?.let { chip ->
                    chipDao.updateChip(chip.copy(isUsed = true, usedInGw = gwNumber))
                }
            }
            if (hasDoubleShot) {
                chipDao.getChip(currentUser.id, "DOUBLE_SHOT", half)?.let { chip ->
                    chipDao.updateChip(chip.copy(isUsed = true, usedInGw = gwNumber))
                }
            }
        }

        // 2. Simulate AI/Opponent scores realistically based on actual scores
        val random = Random(gwNumber * 42)
        for (opp in users.filter { !it.isCurrentUser }) {
            var oppGwPts = 0
            for (fix in fixtures) {
                if (fix.homeScoreActual != null && fix.awayScoreActual != null) {
                    val roll = random.nextInt(100)
                    val pts = when {
                        roll < 30 -> 3 // exact score
                        roll < 65 -> 1 // correct outcome
                        else -> 0
                    }
                    oppGwPts += pts
                }
            }
            // Add slight captain bonus
            oppGwPts += random.nextInt(2, 6)
            userScores[opp.id] = oppGwPts
        }

        // 3. Update League Members & MVP Badge
        val leagues = leagueDao.getAllLeagues().firstOrNull() ?: emptyList()
        for (league in leagues) {
            val members = leagueDao.getLeagueMembersList(league.id)
            if (members.isNotEmpty()) {
                val highestScore = members.maxOfOrNull { userScores[it.userId] ?: 0 } ?: 0
                val updatedMembers = members.map { member ->
                    val gwScore = userScores[member.userId] ?: 0
                    member.copy(
                        gwPoints = gwScore,
                        totalPoints = member.totalPoints + gwScore,
                        isGwMvp = (gwScore > 0 && gwScore == highestScore)
                    )
                }
                leagueDao.insertLeagueMembers(updatedMembers)
            }

            // If H2H League, evaluate matchups
            if (league.type == "H2H") {
                val matchups = leagueDao.getH2HMatchupsList(league.id, gwNumber)
                val updatedMatchups = matchups.map { match ->
                    val score1 = userScores[match.user1Id] ?: 0
                    val score2 = userScores[match.user2Id] ?: 0
                    val isFinished = fixtures.all { it.isFinished }
                    val winner = when {
                        score1 > score2 -> match.user1Id
                        score2 > score1 -> match.user2Id
                        else -> null
                    }
                    val isDraw = (score1 == score2)
                    match.copy(
                        user1Score = score1,
                        user2Score = score2,
                        winnerUserId = winner,
                        isDraw = isDraw,
                        isFinished = isFinished
                    )
                }
                leagueDao.insertH2HMatchups(updatedMatchups)

                // Update H2H standings table
                val membersMap = leagueDao.getLeagueMembersList(league.id).associateBy { it.userId }.toMutableMap()
                for (m in updatedMatchups) {
                    val mem1 = membersMap[m.user1Id]
                    val mem2 = membersMap[m.user2Id]
                    if (mem1 != null && mem2 != null) {
                        when {
                            m.user1Score > m.user2Score -> {
                                membersMap[m.user1Id] = mem1.copy(
                                    h2hWon = mem1.h2hWon + 1,
                                    h2hPoints = mem1.h2hPoints + 3,
                                    pointsDiff = mem1.pointsDiff + (m.user1Score - m.user2Score)
                                )
                                membersMap[m.user2Id] = mem2.copy(
                                    h2hLost = mem2.h2hLost + 1,
                                    pointsDiff = mem2.pointsDiff - (m.user1Score - m.user2Score)
                                )
                            }
                            m.user2Score > m.user1Score -> {
                                membersMap[m.user2Id] = mem2.copy(
                                    h2hWon = mem2.h2hWon + 1,
                                    h2hPoints = mem2.h2hPoints + 3,
                                    pointsDiff = mem2.pointsDiff + (m.user2Score - m.user1Score)
                                )
                                membersMap[m.user1Id] = mem1.copy(
                                    h2hLost = mem1.h2hLost + 1,
                                    pointsDiff = mem1.pointsDiff - (m.user2Score - m.user1Score)
                                )
                            }
                            else -> {
                                membersMap[m.user1Id] = mem1.copy(
                                    h2hDrawn = mem1.h2hDrawn + 1,
                                    h2hPoints = mem1.h2hPoints + 1
                                )
                                membersMap[m.user2Id] = mem2.copy(
                                    h2hDrawn = mem2.h2hDrawn + 1,
                                    h2hPoints = mem2.h2hPoints + 1
                                )
                            }
                        }
                    }
                }
                leagueDao.insertLeagueMembers(membersMap.values.toList())
            }
        }

        val gw = gameweekDao.getCurrentGameweek()
        if (gw != null) {
            gameweekDao.updateGameweek(gw.copy(isEvaluated = true))
        }

        // Automated Badge Evaluation Engine: Evaluate & award badges to user profile state
        val activeUser = userDao.getCurrentUser()
        if (activeUser != null) {
            evaluateBadgesForUser(activeUser.id, gwNumber)
        }
    }

    suspend fun ensureBadgesInitialized(userId: String) {
        val existing = badgeDao.getBadgesForUserList(userId)
        if (existing.isEmpty()) {
            badgeDao.insertBadges(BadgeEvaluationEngine.createInitialBadgesForUser(userId))
        }
    }

    suspend fun evaluateBadgesForUser(userId: String, gwNumber: Int) {
        ensureBadgesInitialized(userId)
        val currentBadges = badgeDao.getBadgesForUserList(userId)
        val allFixtures = fixtureDao.getAllFixturesList()
        val allUserPreds = predictionDao.getAllPredictionsForUserList(userId)
        val userH2H = leagueDao.getAllH2HMatchupsForUserList(userId)
        val h2hWonCount = leagueDao.getMaxH2HWinsForUser(userId) ?: 0

        val evaluatedBadges = BadgeEvaluationEngine.evaluateBadges(
            userId = userId,
            currentBadges = currentBadges,
            allFixtures = allFixtures,
            allUserPredictions = allUserPreds,
            userH2HMatchups = userH2H,
            h2hWinsCount = h2hWonCount,
            activeGwNumber = gwNumber
        )
        badgeDao.insertBadges(evaluatedBadges)
    }

    suspend fun switchFixtureScenario(scenario: String) { // "STANDARD", "DOUBLE", "BLANK"
        val gw = gameweekDao.getCurrentGameweek() ?: return
        val baseKickoff = gw.kickoffFirstMatchMs
        val hour = 3600 * 1000L

        fixtureDao.deleteFixturesForGw(gw.gwNumber)

        val fixtures = when (scenario) {
            "DOUBLE" -> {
                // 12 fixtures
                createStandardFixtures(gw.gwNumber, baseKickoff) + listOf(
                    FixtureEntity("fix_dgw_1", gw.gwNumber, "Arsenal", "ARS", "Aston Villa", "AVL", baseKickoff + 72 * hour, "Tue 20:00", null, null, false, 56, 24, 20, isDoubleGameweekExtra = true),
                    FixtureEntity("fix_dgw_2", gw.gwNumber, "Chelsea", "CHE", "Liverpool", "LIV", baseKickoff + 73 * hour, "Wed 20:15", null, null, false, 38, 30, 32, isDoubleGameweekExtra = true)
                )
            }
            "BLANK" -> {
                // 8 fixtures (2 postponed due to Cup final)
                createStandardFixtures(gw.gwNumber, baseKickoff).take(8)
            }
            else -> {
                createStandardFixtures(gw.gwNumber, baseKickoff)
            }
        }

        fixtureDao.insertFixtures(fixtures)
        gameweekDao.updateGameweek(gw.copy(fixtureCountType = scenario))
    }

    /**
     * Rolling Transition Engine:
     * When Gameweek N kicks off, Gameweek N enters live read-only mode, and
     * Gameweek N+1 automatically opens and unlocks for user predictions.
     */
    suspend fun transitionToNextGameweek(currentGwNumber: Int) {
        val currentGw = gameweekDao.getGameweekByNumber(currentGwNumber) ?: return
        // Lock and mark current GW as in-play
        gameweekDao.updateGameweek(currentGw.copy(isDeadlinePassed = true, isNext = false))

        val nextGwNumber = currentGwNumber + 1
        val existingNextGw = gameweekDao.getGameweekByNumber(nextGwNumber)
        if (existingNextGw == null) {
            val now = System.currentTimeMillis()
            val nextKickoff = now + (7 * 24 * 3600 * 1000L)
            val nextDeadline = nextKickoff - (60 * 60 * 1000L)
            val nextGw = GameweekEntity(
                gwNumber = nextGwNumber,
                season = currentGw.season,
                name = "Gameweek $nextGwNumber",
                deadlineEpochMs = nextDeadline,
                kickoffFirstMatchMs = nextKickoff,
                isDeadlinePassed = false,
                isEvaluated = false,
                isNext = true,
                fixtureCountType = "STANDARD"
            )
            gameweekDao.clearAllIsNext()
            gameweekDao.insertGameweek(nextGw)
            val nextFixtures = createStandardFixtures(nextGwNumber, nextKickoff)
            fixtureDao.insertFixtures(nextFixtures)
        } else {
            gameweekDao.clearAllIsNext()
            gameweekDao.updateGameweek(existingNextGw.copy(isNext = true, isDeadlinePassed = false))
        }
    }

// Firebase Firestore Instance
    private val firestore by lazy { com.google.firebase.firestore.FirebaseFirestore.getInstance() }

    suspend fun createLeague(name: String, type: String): LeagueEntity {
        val user = userDao.getCurrentUser()
        val userId = user?.id ?: "user_primary"
        val userName = user?.username ?: "Manager"
        val code = generateInviteCode()
        val leagueId = "league_${UUID.randomUUID()}"
        val now = System.currentTimeMillis()

        val league = LeagueEntity(
            id = leagueId,
            name = name,
            type = type,
            inviteCode = code,
            creatorId = userId,
            createdAt = now
        )

        val member = LeagueMemberEntity(
            id = "${league.id}_$userId",
            leagueId = league.id,
            userId = userId,
            userName = userName,
            totalPoints = user?.totalScore ?: 0,
            gwPoints = 0
        )

        // 1. الحفظ السحابي في Firestore ليصبح متاحاً لكل المستخدمين
        try {
            val leagueData = hashMapOf(
                "id" to league.id,
                "name" to league.name,
                "type" to league.type,
                "inviteCode" to league.inviteCode,
                "creatorId" to league.creatorId,
                "createdAt" to league.createdAt
            )
            val memberData = hashMapOf(
                "id" to member.id,
                "leagueId" to member.leagueId,
                "userId" to member.userId,
                "userName" to member.userName,
                "totalPoints" to member.totalPoints,
                "gwPoints" to member.gwPoints
            )

            val db = firestore
            val leagueRef = db.collection("leagues").document(league.id)
            kotlinx.coroutines.tasks.await(leagueRef.set(leagueData))
            kotlinx.coroutines.tasks.await(leagueRef.collection("members").document(member.id).set(memberData))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. الحفظ في قاعدة البيانات المحلية Room
        leagueDao.insertLeague(league)
        leagueDao.insertLeagueMember(member)

        return league
    }

    suspend fun joinLeagueByCode(code: String): Result<LeagueEntity> {
        val cleanCode = code.trim().uppercase()
        val user = userDao.getCurrentUser()
        val userId = user?.id ?: "user_primary"
        val userName = user?.username ?: "Manager"

        return try {
            // 1. البحث السحابي في Firestore أولاً
            val querySnapshot = kotlinx.coroutines.tasks.await(
                firestore.collection("leagues")
                    .whereEqualTo("inviteCode", cleanCode)
                    .get()
            )

            if (!querySnapshot.isEmpty) {
                val doc = querySnapshot.documents[0]
                val remoteLeague = LeagueEntity(
                    id = doc.getString("id") ?: doc.id,
                    name = doc.getString("name") ?: "League",
                    type = doc.getString("type") ?: "CLASSIC",
                    inviteCode = doc.getString("inviteCode") ?: cleanCode,
                    creatorId = doc.getString("creatorId") ?: "",
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                )

                val newMember = LeagueMemberEntity(
                    id = "${remoteLeague.id}_$userId",
                    leagueId = remoteLeague.id,
                    userId = userId,
                    userName = userName,
                    totalPoints = user?.totalScore ?: 0,
                    gwPoints = 0
                )

                // إضافة العضو سحابياً في Firestore
                val memberData = hashMapOf(
                    "id" to newMember.id,
                    "leagueId" to newMember.leagueId,
                    "userId" to newMember.userId,
                    "userName" to newMember.userName,
                    "totalPoints" to newMember.totalPoints,
                    "gwPoints" to newMember.gwPoints
                )
                kotlinx.coroutines.tasks.await(
                    firestore.collection("leagues")
                        .document(remoteLeague.id)
                        .collection("members")
                        .document(newMember.id)
                        .set(memberData)
                )

                // مزامنة الدوري والعضو محلياً في جهاز الصديق
                leagueDao.insertLeague(remoteLeague)
                leagueDao.insertLeagueMember(newMember)

                Result.success(remoteLeague)
            } else {
                // محاولة البحث محلياً كخيار احتياطي
                val localLeague = leagueDao.getLeagueByInviteCode(cleanCode)
                if (localLeague != null) {
                    val member = LeagueMemberEntity(
                        id = "${localLeague.id}_$userId",
                        leagueId = localLeague.id,
                        userId = userId,
                        userName = userName,
                        totalPoints = user?.totalScore ?: 0,
                        gwPoints = 0
                    )
                    leagueDao.insertLeagueMember(member)
                    Result.success(localLeague)
                } else {
                    Result.failure(IllegalArgumentException("League with code '$cleanCode' not found."))
                }
            }
        } catch (e: Exception) {
            // في حالة فشل الاتصال بالسيرفر، البحث محلياً
            val localLeague = leagueDao.getLeagueByInviteCode(cleanCode)
            if (localLeague != null) {
                val member = LeagueMemberEntity(
                    id = "${localLeague.id}_$userId",
                    leagueId = localLeague.id,
                    userId = userId,
                    userName = userName,
                    totalPoints = user?.totalScore ?: 0,
                    gwPoints = 0
                )
                leagueDao.insertLeagueMember(member)
                Result.success(localLeague)
            } else {
                Result.failure(IllegalArgumentException(e.localizedMessage ?: "Failed to join league"))
            }
        }
    }
    }

    suspend fun loginOrRegister(
        identifier: String,
        email: String?,
        displayName: String? = null,
        avatarUri: String? = null,
        presetCrestCode: String? = null
    ): UserEntity {
        val existing = userDao.findUser(identifier)
        if (existing != null) {
            userDao.clearCurrentUser()
            val updated = existing.copy(
                isCurrentUser = true,
                avatarUri = avatarUri ?: existing.avatarUri,
                presetCrestCode = presetCrestCode ?: existing.presetCrestCode,
                username = if (!displayName.isNullOrBlank()) displayName else existing.username
            )
            userDao.updateUser(updated)
            ensureBadgesInitialized(existing.id)
            return updated
        } else {
            userDao.clearCurrentUser()
            val finalName = if (!displayName.isNullOrBlank()) displayName else identifier
            val newUser = UserEntity(
                id = "user_${UUID.randomUUID().toString().take(8)}",
                username = finalName,
                email = email ?: "$identifier@premierleague.com",
                totalScore = 0,
                currentGwScore = 0,
                overallRank = 42,
                isCurrentUser = true,
                avatarUri = avatarUri,
                presetCrestCode = presetCrestCode ?: "ARS"
            )
            userDao.insertUser(newUser)

            // Initialize all 4 chips for new user across Half 1 & Half 2
            val chips = listOf(
                ChipEntity("${newUser.id}_SUPER_CAPTAIN_1", newUser.id, "SUPER_CAPTAIN", half = 1, usedInGw = null, isUsed = false),
                ChipEntity("${newUser.id}_SAFETY_NET_1", newUser.id, "SAFETY_NET", half = 1, usedInGw = null, isUsed = false),
                ChipEntity("${newUser.id}_AUTO_CAPTAIN_1", newUser.id, "AUTO_CAPTAIN", half = 1, usedInGw = null, isUsed = false),
                ChipEntity("${newUser.id}_DOUBLE_SHOT_1", newUser.id, "DOUBLE_SHOT", half = 1, usedInGw = null, isUsed = false),
                ChipEntity("${newUser.id}_SUPER_CAPTAIN_2", newUser.id, "SUPER_CAPTAIN", half = 2, usedInGw = null, isUsed = false),
                ChipEntity("${newUser.id}_SAFETY_NET_2", newUser.id, "SAFETY_NET", half = 2, usedInGw = null, isUsed = false),
                ChipEntity("${newUser.id}_AUTO_CAPTAIN_2", newUser.id, "AUTO_CAPTAIN", half = 2, usedInGw = null, isUsed = false),
                ChipEntity("${newUser.id}_DOUBLE_SHOT_2", newUser.id, "DOUBLE_SHOT", half = 2, usedInGw = null, isUsed = false)
            )
            chipDao.insertChips(chips)

            // Initialize badges for new user
            val badges = BadgeEvaluationEngine.createInitialBadgesForUser(newUser.id)
            badgeDao.insertBadges(badges)

            return newUser
        }
    }

    suspend fun updateProfile(displayName: String, avatarUri: String?, presetCrestCode: String?) {
        val user = userDao.getCurrentUser() ?: return
        val updated = user.copy(
            username = if (displayName.isNotBlank()) displayName else user.username,
            avatarUri = avatarUri ?: user.avatarUri,
            presetCrestCode = presetCrestCode ?: user.presetCrestCode
        )
        userDao.updateUser(updated)
    }

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars.random() }.joinToString("")
    }
}
