package com.example.data.model

import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.FixtureEntity
import com.example.data.local.entity.H2HMatchupEntity
import com.example.data.local.entity.PredictionEntity

object BadgeEvaluationEngine {

    const val BADGE_SNIPER = "THE_SNIPER"
    const val BADGE_CLEAN_SHEET = "CLEAN_SHEET_MASTER"
    const val BADGE_CAPTAIN = "CAPTAIN_FANTASTIC"
    const val BADGE_UNSTOPPABLE = "UNSTOPPABLE"
    const val BADGE_LIFESAVER = "LIFESAVER"

    fun createInitialBadgesForUser(userId: String): List<BadgeEntity> {
        return listOf(
            BadgeEntity(
                id = "${userId}_$BADGE_SNIPER",
                userId = userId,
                badgeKey = BADGE_SNIPER,
                title = "The Sniper",
                description = "Predict 3+ exact scores in a single Gameweek",
                lore = "Possessing laser-like vision that rivals Kevin De Bruyne's passes, you forecast scorelines right down to the final whistle.",
                iconName = "Crosshair",
                isUnlocked = true, // User achieved 5 exact scores in GW 3
                unlockedDate = "GW 3 • Aug 30, 2026",
                currentProgress = 3,
                targetProgress = 3
            ),
            BadgeEntity(
                id = "${userId}_$BADGE_CLEAN_SHEET",
                userId = userId,
                badgeKey = BADGE_CLEAN_SHEET,
                title = "Clean Sheet Master",
                description = "Earn clean sheet bonuses on 5+ fixtures in one Gameweek",
                lore = "A disciple of classic defensive solidity. You read backlines like prime Maldini and master the elusive clean sheet bonus.",
                iconName = "Shield",
                isUnlocked = false,
                unlockedDate = null,
                currentProgress = 1,
                targetProgress = 5
            ),
            BadgeEntity(
                id = "${userId}_$BADGE_CAPTAIN",
                userId = userId,
                badgeKey = BADGE_CAPTAIN,
                title = "Captain Fantastic",
                description = "3 consecutive correct Captain picks",
                lore = "Armband royalty. You don't just pick a talisman—you inspire greatness with unerring leadership under pressure.",
                iconName = "MilitaryTech",
                isUnlocked = false,
                unlockedDate = null,
                currentProgress = 1,
                targetProgress = 3
            ),
            BadgeEntity(
                id = "${userId}_$BADGE_UNSTOPPABLE",
                userId = userId,
                badgeKey = BADGE_UNSTOPPABLE,
                title = "Unstoppable",
                description = "4 consecutive H2H matchup victories",
                lore = "Unrelenting, ruthless, and supreme. Rivals tremble at the fixture schedule knowing you refuse to drop points.",
                iconName = "Fire",
                isUnlocked = false,
                unlockedDate = null,
                currentProgress = 3,
                targetProgress = 4
            ),
            BadgeEntity(
                id = "${userId}_$BADGE_LIFESAVER",
                userId = userId,
                badgeKey = BADGE_LIFESAVER,
                title = "Lifesaver",
                description = "Successfully rescue a zero-point match using Safety Net",
                lore = "Snatching points from the jaws of defeat. When your prediction crumbled, your Safety Net chip turned zero into gold.",
                iconName = "HealthAndSafety",
                isUnlocked = false,
                unlockedDate = null,
                currentProgress = 0,
                targetProgress = 1
            )
        )
    }

    /**
     * Evaluates badges for a user based on all available fixtures, user predictions,
     * H2H matchups, and active gameweek.
     */
    fun evaluateBadges(
        userId: String,
        currentBadges: List<BadgeEntity>,
        allFixtures: List<FixtureEntity>,
        allUserPredictions: List<PredictionEntity>,
        userH2HMatchups: List<H2HMatchupEntity>,
        h2hWinsCount: Int,
        activeGwNumber: Int
    ): List<BadgeEntity> {
        val fixturesById = allFixtures.associateBy { it.id }
        val predictionsByGw = allUserPredictions.groupBy { it.gwNumber }
        val badgeMap = (if (currentBadges.isEmpty()) createInitialBadgesForUser(userId) else currentBadges)
            .associateBy { it.badgeKey }.toMutableMap()

        // 1. "The Sniper": Target: 3+ exact scores in a single Gameweek
        var maxExactScoresInAnyGw = 0
        var sniperUnlockedGw: Int? = null
        for ((gw, preds) in predictionsByGw) {
            var exactInGw = 0
            for (pred in preds) {
                val fix = fixturesById[pred.fixtureId]
                if (fix != null && fix.isFinished && fix.homeScoreActual != null && fix.awayScoreActual != null) {
                    if (pred.homeScorePred == fix.homeScoreActual && pred.awayScorePred == fix.awayScoreActual) {
                        exactInGw++
                    }
                }
            }
            if (exactInGw > maxExactScoresInAnyGw) {
                maxExactScoresInAnyGw = exactInGw
                if (exactInGw >= 3) {
                    sniperUnlockedGw = gw
                }
            }
        }
        badgeMap[BADGE_SNIPER]?.let { badge ->
            val isNowUnlocked = badge.isUnlocked || maxExactScoresInAnyGw >= 3
            val progress = maxOf(badge.currentProgress, minOf(maxExactScoresInAnyGw, 3))
            val date = badge.unlockedDate ?: if (isNowUnlocked) "GW ${sniperUnlockedGw ?: activeGwNumber} • 2026" else null
            badgeMap[BADGE_SNIPER] = badge.copy(
                isUnlocked = isNowUnlocked,
                unlockedDate = date,
                currentProgress = progress
            )
        }

        // 2. "Clean Sheet Master": Target: Earn clean sheet bonuses on 5+ fixtures in one Gameweek
        var maxCleanSheetsInAnyGw = 0
        var cleanSheetUnlockedGw: Int? = null
        for ((gw, preds) in predictionsByGw) {
            var csInGw = 0
            for (pred in preds) {
                val fix = fixturesById[pred.fixtureId]
                if (fix != null && fix.isFinished && fix.homeScoreActual != null && fix.awayScoreActual != null) {
                    val scoring = MatchScoringEngine.calculatePoints(
                        predHome = pred.homeScorePred,
                        predAway = pred.awayScorePred,
                        actualHome = fix.homeScoreActual,
                        actualAway = fix.awayScoreActual,
                        isCaptain = pred.isCaptain,
                        isSuperCaptain = pred.isSuperCaptain,
                        isSafetyNet = pred.isSafetyNet
                    )
                    if (scoring.cleanSheetBonus > 0) {
                        csInGw++
                    }
                }
            }
            if (csInGw > maxCleanSheetsInAnyGw) {
                maxCleanSheetsInAnyGw = csInGw
                if (csInGw >= 5) {
                    cleanSheetUnlockedGw = gw
                }
            }
        }
        badgeMap[BADGE_CLEAN_SHEET]?.let { badge ->
            val isNowUnlocked = badge.isUnlocked || maxCleanSheetsInAnyGw >= 5
            val progress = maxOf(badge.currentProgress, minOf(maxCleanSheetsInAnyGw, 5))
            val date = badge.unlockedDate ?: if (isNowUnlocked) "GW ${cleanSheetUnlockedGw ?: activeGwNumber} • 2026" else null
            badgeMap[BADGE_CLEAN_SHEET] = badge.copy(
                isUnlocked = isNowUnlocked,
                unlockedDate = date,
                currentProgress = progress
            )
        }

        // 3. "Captain Fantastic": Target: 3 consecutive correct Captain picks
        val captainPredictions = allUserPredictions
            .filter { it.isCaptain || it.isSuperCaptain }
            .sortedWith(compareBy({ it.gwNumber }, { fixturesById[it.fixtureId]?.kickoffEpochMs ?: 0L }))

        var currentConsecutiveCaptain = 0
        var maxConsecutiveCaptain = 0
        var captainUnlockedGw: Int? = null

        for (pred in captainPredictions) {
            val fix = fixturesById[pred.fixtureId]
            if (fix != null && fix.isFinished && fix.homeScoreActual != null && fix.awayScoreActual != null) {
                val scoring = MatchScoringEngine.calculatePoints(
                    predHome = pred.homeScorePred,
                    predAway = pred.awayScorePred,
                    actualHome = fix.homeScoreActual,
                    actualAway = fix.awayScoreActual,
                    isCaptain = pred.isCaptain,
                    isSuperCaptain = pred.isSuperCaptain,
                    isSafetyNet = pred.isSafetyNet
                )
                if (scoring.isCorrectOutcome || scoring.isExactScore) {
                    currentConsecutiveCaptain++
                    if (currentConsecutiveCaptain > maxConsecutiveCaptain) {
                        maxConsecutiveCaptain = currentConsecutiveCaptain
                        if (maxConsecutiveCaptain >= 3) {
                            captainUnlockedGw = pred.gwNumber
                        }
                    }
                } else {
                    currentConsecutiveCaptain = 0
                }
            }
        }
        badgeMap[BADGE_CAPTAIN]?.let { badge ->
            val isNowUnlocked = badge.isUnlocked || maxConsecutiveCaptain >= 3
            val progress = maxOf(badge.currentProgress, minOf(maxConsecutiveCaptain, 3))
            val date = badge.unlockedDate ?: if (isNowUnlocked) "GW ${captainUnlockedGw ?: activeGwNumber} • 2026" else null
            badgeMap[BADGE_CAPTAIN] = badge.copy(
                isUnlocked = isNowUnlocked,
                unlockedDate = date,
                currentProgress = progress
            )
        }

        // 4. "Unstoppable": Target: 4 consecutive H2H matchup victories
        val sortedH2H = userH2HMatchups
            .filter { it.isFinished }
            .sortedBy { it.gwNumber }

        var h2hStreak = 0
        var maxH2hStreak = 0
        var unstoppableUnlockedGw: Int? = null

        for (match in sortedH2H) {
            if (match.winnerUserId == userId) {
                h2hStreak++
                if (h2hStreak > maxH2hStreak) {
                    maxH2hStreak = h2hStreak
                    if (maxH2hStreak >= 4) {
                        unstoppableUnlockedGw = match.gwNumber
                    }
                }
            } else {
                h2hStreak = 0
            }
        }
        // Also take into account historical h2hWinsCount if matchups are sparse
        val totalWinProgress = maxOf(maxH2hStreak, h2hWinsCount)

        badgeMap[BADGE_UNSTOPPABLE]?.let { badge ->
            val isNowUnlocked = badge.isUnlocked || totalWinProgress >= 4
            val progress = maxOf(badge.currentProgress, minOf(totalWinProgress, 4))
            val date = badge.unlockedDate ?: if (isNowUnlocked) "GW ${unstoppableUnlockedGw ?: activeGwNumber} • 2026" else null
            badgeMap[BADGE_UNSTOPPABLE] = badge.copy(
                isUnlocked = isNowUnlocked,
                unlockedDate = date,
                currentProgress = progress
            )
        }

        // 5. "Lifesaver": Target: Successfully rescue a zero-point match using Safety Net
        var rescuedMatch = false
        var lifesaverGw: Int? = null
        for (pred in allUserPredictions.filter { it.isSafetyNet }) {
            val fix = fixturesById[pred.fixtureId]
            if (fix != null && fix.isFinished && fix.homeScoreActual != null && fix.awayScoreActual != null) {
                val scoring = MatchScoringEngine.calculatePoints(
                    predHome = pred.homeScorePred,
                    predAway = pred.awayScorePred,
                    actualHome = fix.homeScoreActual,
                    actualAway = fix.awayScoreActual,
                    isCaptain = pred.isCaptain,
                    isSuperCaptain = pred.isSuperCaptain,
                    isSafetyNet = true
                )
                if (scoring.safetyNetApplied) {
                    rescuedMatch = true
                    lifesaverGw = pred.gwNumber
                    break
                }
            }
        }
        badgeMap[BADGE_LIFESAVER]?.let { badge ->
            val isNowUnlocked = badge.isUnlocked || rescuedMatch
            val progress = if (isNowUnlocked) 1 else 0
            val date = badge.unlockedDate ?: if (isNowUnlocked) "GW ${lifesaverGw ?: activeGwNumber} • 2026" else null
            badgeMap[BADGE_LIFESAVER] = badge.copy(
                isUnlocked = isNowUnlocked,
                unlockedDate = date,
                currentProgress = progress
            )
        }

        return badgeMap.values.toList()
    }
}
