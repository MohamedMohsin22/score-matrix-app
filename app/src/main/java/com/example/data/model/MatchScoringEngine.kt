package com.example.data.model

data class ScoringResult(
    val basePoints: Int,
    val cleanSheetBonus: Int,
    val isExactScore: Boolean,
    val isCorrectOutcome: Boolean,
    val multiplier: Int,
    val safetyNetApplied: Boolean,
    val isDoubleShotApplied: Boolean = false,
    val doubleShotBestPrediction: String? = null,
    val totalPoints: Int,
    val breakdownText: String
)

object MatchScoringEngine {

    /**
     * Calculates points for a prediction according to Score Matrix official rules:
     * - Exact Score Base: 3 points.
     * - Clean Sheet Bonus (on exact score only):
     *   * Exact win to nil (e.g. 1-0, 2-0, 0-1): +1 bonus point (Total 4 points).
     *   * Exact 0-0 draw: +2 bonus points (Total 5 points).
     *   * Other draws (1-1, 2-2): 0 bonus (Total 3 points).
     * - Correct Outcome Only:
     *   * Correct Draw (e.g. predicted 1-1, actual 2-2): 2 points.
     *   * Correct Win (predicted home/away win, wrong exact score): 1 point.
     * - Incorrect Outcome: 0 points.
     * - Multipliers:
     *   * Standard Captain: 2x multiplier.
     *   * Super Captain: 3x multiplier.
     * - Safety Net:
     *   * Guarantees a minimum 3-point floor (0, 1, or 2 points elevate to 3; natural 3, 4, or 5 remain untouched).
     * - Double Shot:
     *   * Evaluates Prediction A and Prediction B against actual score, awarding the maximum points of the two.
     */
    fun calculatePoints(
        predHome: Int?,
        predAway: Int?,
        actualHome: Int?,
        actualAway: Int?,
        isCaptain: Boolean,
        isSuperCaptain: Boolean,
        isSafetyNet: Boolean,
        isDoubleShot: Boolean = false,
        predHomeB: Int? = null,
        predAwayB: Int? = null
    ): ScoringResult {
        if (actualHome == null || actualAway == null || (predHome == null && predHomeB == null)) {
            return ScoringResult(
                basePoints = 0,
                cleanSheetBonus = 0,
                isExactScore = false,
                isCorrectOutcome = false,
                multiplier = 1,
                safetyNetApplied = false,
                totalPoints = 0,
                breakdownText = "No prediction or match incomplete"
            )
        }

        // If Double Shot is active and both predictions are present, evaluate both and pick best
        if (isDoubleShot && predHomeB != null && predAwayB != null) {
            val resultA = evaluateSingleScore(
                predHome = predHome ?: 0,
                predAway = predAway ?: 0,
                actualHome = actualHome,
                actualAway = actualAway,
                isCaptain = isCaptain,
                isSuperCaptain = isSuperCaptain,
                isSafetyNet = isSafetyNet
            )
            val resultB = evaluateSingleScore(
                predHome = predHomeB,
                predAway = predAwayB,
                actualHome = actualHome,
                actualAway = actualAway,
                isCaptain = isCaptain,
                isSuperCaptain = isSuperCaptain,
                isSafetyNet = isSafetyNet
            )

            val bestResult = if (resultB.totalPoints > resultA.totalPoints) resultB else resultA
            val bestTag = if (resultB.totalPoints > resultA.totalPoints) "Prediction B (${predHomeB}-${predAwayB})" else "Prediction A (${predHome ?: 0}-${predAway ?: 0})"
            val otherTag = if (resultB.totalPoints > resultA.totalPoints) "Pred A (${predHome ?: 0}-${predAway ?: 0}: ${resultA.totalPoints} pts)" else "Pred B (${predHomeB}-${predAwayB}: ${resultB.totalPoints} pts)"

            return bestResult.copy(
                isDoubleShotApplied = true,
                doubleShotBestPrediction = bestTag,
                breakdownText = "Double Shot [$bestTag awarded ${bestResult.totalPoints} pts • $otherTag] • ${bestResult.breakdownText}"
            )
        }

        // Standard Single Prediction evaluation
        return evaluateSingleScore(
            predHome = predHome ?: 0,
            predAway = predAway ?: 0,
            actualHome = actualHome,
            actualAway = actualAway,
            isCaptain = isCaptain,
            isSuperCaptain = isSuperCaptain,
            isSafetyNet = isSafetyNet
        )
    }

    private fun evaluateSingleScore(
        predHome: Int,
        predAway: Int,
        actualHome: Int,
        actualAway: Int,
        isCaptain: Boolean,
        isSuperCaptain: Boolean,
        isSafetyNet: Boolean
    ): ScoringResult {
        val exactScore = (predHome == actualHome && predAway == actualAway)

        val predOutcome = when {
            predHome > predAway -> 1
            predHome < predAway -> -1
            else -> 0
        }

        val actualOutcome = when {
            actualHome > actualAway -> 1
            actualHome < actualAway -> -1
            else -> 0
        }

        val correctOutcome = (predOutcome == actualOutcome)
        val isDrawOutcome = (actualOutcome == 0 && correctOutcome && !exactScore)

        var basePts = 0
        var cleanSheetBonus = 0
        val breakdownParts = mutableListOf<String>()

        if (exactScore) {
            basePts = 3
            breakdownParts.add("Exact Score (+3)")
        } else if (correctOutcome) {
            if (isDrawOutcome) {
                // Correct draw outcome (non-exact): 2 points
                basePts = 2
                breakdownParts.add("Correct Draw Outcome (+2)")
            } else {
                // Correct win outcome (non-exact): 1 point
                basePts = 1
                breakdownParts.add("Correct Outcome (+1)")
            }
        } else {
            basePts = 0
            breakdownParts.add("Incorrect Outcome (+0)")
        }

        // Decoupled Clean Sheet Bonus:
        // +1 pt per team whenever the predicted opponent score is 0 AND the actual opponent score is 0
        // (Applies to both exact score and correct win outcome)
        val eligibleForCleanSheet = exactScore || (correctOutcome && actualOutcome != 0)
        if (eligibleForCleanSheet) {
            if (predAway == 0 && actualAway == 0) {
                cleanSheetBonus += 1
                breakdownParts.add("Home Clean Sheet (+1)")
            }
            if (predHome == 0 && actualHome == 0) {
                cleanSheetBonus += 1
                breakdownParts.add("Away Clean Sheet (+1)")
            }
        }

        val preMultiplierPoints = basePts + cleanSheetBonus
        val multiplier = when {
            isSuperCaptain -> 3
            isCaptain -> 2
            else -> 1
        }

        var total = preMultiplierPoints * multiplier

        if (multiplier > 1) {
            if (isSuperCaptain) {
                breakdownParts.add("Super Captain (3x)")
            } else {
                breakdownParts.add("Captain (2x)")
            }
        }

        var safetyNetApplied = false
        // Safety Net guarantees minimum 3-point floor:
        // (0, 1, or 2 base points elevate to 3; natural 3, 4, or 5 remain untouched)
        if (isSafetyNet && total < 3) {
            safetyNetApplied = true
            total = 3
            breakdownParts.add("Safety Net Floor Applied (Elevated to 3 pts)")
        }

        val summary = breakdownParts.joinToString(" • ") + " = $total pts"

        return ScoringResult(
            basePoints = basePts,
            cleanSheetBonus = cleanSheetBonus,
            isExactScore = exactScore,
            isCorrectOutcome = correctOutcome,
            multiplier = multiplier,
            safetyNetApplied = safetyNetApplied,
            totalPoints = total,
            breakdownText = summary
        )
    }
}
