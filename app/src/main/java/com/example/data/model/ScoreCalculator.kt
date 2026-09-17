package com.example.data.model

/**
 * ScoreCalculator: Official scoring calculation engine for Score Matrix Premier League Predictor.
 *
 * Scoring Rules:
 * - Exact Score: Award 3 base points.
 * - Correct Outcome - Draw: Award 2 points (e.g., predicted 1-1, ended 2-2).
 * - Correct Outcome - Win: Award 1 point (e.g., predicted 2-1, ended 1-0).
 * - Incorrect Outcome: Award 0 points.
 *
 * Decoupled Clean Sheet Bonus:
 * - Add +1 pt per team whenever the predicted opponent score is 0 AND the actual opponent score is 0
 *   (applies to both exact score and correct win outcome).
 * - Goalless Draw (0-0 Exact): Awards 3 base (Exact) + 2 points (Both Clean Sheets) = 5 points total.
 *
 * Tactical Multipliers & Floor:
 * - Captain: 2x multiplier
 * - Super Captain: 3x multiplier
 * - Safety Net: 3-point minimum floor
 * - Double Shot: Evaluates Prediction A & B, awarding the higher scoring prediction
 */
object ScoreCalculator {
    fun calculatePoints(
        predHome: Int?,
        predAway: Int?,
        actualHome: Int?,
        actualAway: Int?,
        isCaptain: Boolean = false,
        isSuperCaptain: Boolean = false,
        isSafetyNet: Boolean = false,
        isDoubleShot: Boolean = false,
        predHomeB: Int? = null,
        predAwayB: Int? = null
    ): ScoringResult {
        return MatchScoringEngine.calculatePoints(
            predHome = predHome,
            predAway = predAway,
            actualHome = actualHome,
            actualAway = actualAway,
            isCaptain = isCaptain,
            isSuperCaptain = isSuperCaptain,
            isSafetyNet = isSafetyNet,
            isDoubleShot = isDoubleShot,
            predHomeB = predHomeB,
            predAwayB = predAwayB
        )
    }
}
