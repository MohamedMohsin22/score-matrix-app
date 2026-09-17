package com.example

import com.example.data.model.MatchScoringEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchScoringEngineTest {

    @Test
    fun testExactScoreWinToNilWithCleanSheet() {
        // Predicted 2-0, Actual 2-0: Win to nil clean sheet bonus (+1) -> 4 base points
        val res = MatchScoringEngine.calculatePoints(
            predHome = 2,
            predAway = 0,
            actualHome = 2,
            actualAway = 0,
            isCaptain = false,
            isSuperCaptain = false,
            isSafetyNet = false
        )
        assertTrue(res.isExactScore)
        assertEquals(3, res.basePoints)
        assertEquals(1, res.cleanSheetBonus)
        assertEquals(4, res.totalPoints)
    }

    @Test
    fun testExactZeroZeroDrawWithBothCleanSheets() {
        // Predicted 0-0, Actual 0-0: +2 bonus clean sheet points -> 5 base points
        val res = MatchScoringEngine.calculatePoints(
            predHome = 0,
            predAway = 0,
            actualHome = 0,
            actualAway = 0,
            isCaptain = false,
            isSuperCaptain = false,
            isSafetyNet = false
        )
        assertTrue(res.isExactScore)
        assertEquals(3, res.basePoints)
        assertEquals(2, res.cleanSheetBonus)
        assertEquals(5, res.totalPoints)
    }

    @Test
    fun testExactScoreWithGoalsConceded() {
        // Predicted 2-1, Actual 2-1: Exact score (+3), no clean sheet bonus -> 3 points
        val res = MatchScoringEngine.calculatePoints(
            predHome = 2,
            predAway = 1,
            actualHome = 2,
            actualAway = 1,
            isCaptain = false,
            isSuperCaptain = false,
            isSafetyNet = false
        )
        assertTrue(res.isExactScore)
        assertEquals(3, res.basePoints)
        assertEquals(0, res.cleanSheetBonus)
        assertEquals(3, res.totalPoints)
    }

    @Test
    fun testExactScoreDrawWithGoalsConceded() {
        // Predicted 1-1, Actual 1-1: Exact score (+3), no clean sheet bonus -> 3 points
        val res = MatchScoringEngine.calculatePoints(
            predHome = 1,
            predAway = 1,
            actualHome = 1,
            actualAway = 1,
            isCaptain = false,
            isSuperCaptain = false,
            isSafetyNet = false
        )
        assertTrue(res.isExactScore)
        assertEquals(3, res.basePoints)
        assertEquals(0, res.cleanSheetBonus)
        assertEquals(3, res.totalPoints)
    }

    @Test
    fun testCorrectOutcomeOnly() {
        // Predicted 2-0 (Home Win), Actual 3-1 (Home Win, not exact): 1 point
        val res = MatchScoringEngine.calculatePoints(
            predHome = 2,
            predAway = 0,
            actualHome = 3,
            actualAway = 1,
            isCaptain = false,
            isSuperCaptain = false,
            isSafetyNet = false
        )
        assertFalse(res.isExactScore)
        assertTrue(res.isCorrectOutcome)
        assertEquals(1, res.basePoints)
        assertEquals(1, res.totalPoints)
    }

    @Test
    fun testCorrectOutcomeDraw() {
        // Predicted 1-1 (Draw), Actual 2-2 (Draw, not exact): 2 points
        val res = MatchScoringEngine.calculatePoints(
            predHome = 1,
            predAway = 1,
            actualHome = 2,
            actualAway = 2,
            isCaptain = false,
            isSuperCaptain = false,
            isSafetyNet = false
        )
        assertFalse(res.isExactScore)
        assertTrue(res.isCorrectOutcome)
        assertEquals(2, res.basePoints)
        assertEquals(2, res.totalPoints)
    }

    @Test
    fun testIncorrectOutcome() {
        // Predicted 2-0 (Home Win), Actual 0-1 (Away Win): 0 points
        val res = MatchScoringEngine.calculatePoints(
            predHome = 2,
            predAway = 0,
            actualHome = 0,
            actualAway = 1,
            isCaptain = false,
            isSuperCaptain = false,
            isSafetyNet = false
        )
        assertFalse(res.isExactScore)
        assertFalse(res.isCorrectOutcome)
        assertEquals(0, res.totalPoints)
    }

    @Test
    fun testCaptainMultiplier() {
        // Predicted 2-0, Actual 2-0: 4 pts base * 2 captain = 8 pts
        val res = MatchScoringEngine.calculatePoints(
            predHome = 2,
            predAway = 0,
            actualHome = 2,
            actualAway = 0,
            isCaptain = true,
            isSuperCaptain = false,
            isSafetyNet = false
        )
        assertEquals(8, res.totalPoints)
        assertEquals(2, res.multiplier)
    }

    @Test
    fun testSuperCaptainMultiplier() {
        // Predicted 0-0, Actual 0-0: 5 pts base * 3 super captain = 15 pts
        val res = MatchScoringEngine.calculatePoints(
            predHome = 0,
            predAway = 0,
            actualHome = 0,
            actualAway = 0,
            isCaptain = true,
            isSuperCaptain = true,
            isSafetyNet = false
        )
        assertEquals(15, res.totalPoints)
        assertEquals(3, res.multiplier)
    }

    @Test
    fun testSafetyNetActivationOnZeroPoints() {
        // Predicted 1-0, Actual 0-2 (0 pts earned) -> Safety Net elevates to 3 floor points
        val res = MatchScoringEngine.calculatePoints(
            predHome = 1,
            predAway = 0,
            actualHome = 0,
            actualAway = 2,
            isCaptain = false,
            isSuperCaptain = false,
            isSafetyNet = true
        )
        assertTrue(res.safetyNetApplied)
        assertEquals(3, res.totalPoints)
    }

    @Test
    fun testSafetyNetActivationOnLowPoints() {
        // Predicted 1-0, Actual 2-0 (1 pt earned) -> Safety Net elevates to 3 floor points
        val res = MatchScoringEngine.calculatePoints(
            predHome = 1,
            predAway = 0,
            actualHome = 2,
            actualAway = 0,
            isCaptain = false,
            isSuperCaptain = false,
            isSafetyNet = true
        )
        assertTrue(res.safetyNetApplied)
        assertEquals(3, res.totalPoints)
    }

    @Test
    fun testSafetyNetDoesNotActivateWhenThreeOrMorePointsEarned() {
        // Predicted 2-1, Actual 2-1 (3 pts earned) -> Safety Net does not activate
        val res = MatchScoringEngine.calculatePoints(
            predHome = 2,
            predAway = 1,
            actualHome = 2,
            actualAway = 1,
            isCaptain = false,
            isSuperCaptain = false,
            isSafetyNet = true
        )
        assertFalse(res.safetyNetApplied)
        assertEquals(3, res.totalPoints)
    }

    @Test
    fun testDoubleShotPicksHigherScoringPrediction() {
        // Prediction A: 1-0 (wrong outcome -> 0 pts)
        // Prediction B: 2-0 (exact score with clean sheet -> 4 pts)
        val res = MatchScoringEngine.calculatePoints(
            predHome = 1,
            predAway = 0,
            actualHome = 2,
            actualAway = 0,
            isCaptain = false,
            isSuperCaptain = false,
            isSafetyNet = false,
            isDoubleShot = true,
            predHomeB = 2,
            predAwayB = 0
        )
        assertTrue(res.isExactScore)
        assertEquals(4, res.totalPoints)
        assertEquals("Prediction B (2-0)", res.doubleShotBestPrediction)
    }
}
