package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val totalScore: Int = 0,
    val currentGwScore: Int = 0,
    val overallRank: Int = 1,
    val isCurrentUser: Boolean = false,
    val avatarUri: String? = null,
    val presetCrestCode: String? = "ARS"
)

@Entity(tableName = "gameweeks")
data class GameweekEntity(
    @PrimaryKey val gwNumber: Int,
    val season: String = "2024/2025",
    val name: String = "Gameweek $gwNumber",
    val deadlineEpochMs: Long,
    val kickoffFirstMatchMs: Long,
    val isDeadlinePassed: Boolean = false,
    val isEvaluated: Boolean = false,
    val isNext: Boolean = false,
    val fixtureCountType: String = "STANDARD" // STANDARD (10), DOUBLE (12), BLANK (8)
)

@Entity(tableName = "fixtures")
data class FixtureEntity(
    @PrimaryKey val id: String,
    val gwNumber: Int,
    val homeTeam: String,
    val homeCode: String,
    val awayTeam: String,
    val awayCode: String,
    val kickoffEpochMs: Long,
    val kickoffTimeDisplay: String,
    val homeScoreActual: Int? = null,
    val awayScoreActual: Int? = null,
    val isFinished: Boolean = false,
    val homeWinPct: Int = 45,
    val drawPct: Int = 25,
    val awayWinPct: Int = 30,
    val isDoubleGameweekExtra: Boolean = false
)

@Entity(tableName = "predictions")
data class PredictionEntity(
    @PrimaryKey val id: String, // "${userId}_${fixtureId}"
    val userId: String,
    val fixtureId: String,
    val gwNumber: Int,
    val homeScorePred: Int? = null,
    val awayScorePred: Int? = null,
    val isCaptain: Boolean = false,
    val isSuperCaptain: Boolean = false,
    val isSafetyNet: Boolean = false,
    val isDoubleShot: Boolean = false,
    val homeScorePredB: Int? = null,
    val awayScorePredB: Int? = null,
    val isAutoCaptain: Boolean = false,
    val pointsEarned: Int = 0,
    val breakdownText: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chips")
data class ChipEntity(
    @PrimaryKey val id: String, // "${userId}_${chipType}_${half}"
    val userId: String,
    val chipType: String, // "SUPER_CAPTAIN", "SAFETY_NET", "AUTO_CAPTAIN", "DOUBLE_SHOT"
    val half: Int, // 1 (GW 1-19), 2 (GW 20-38)
    val usedInGw: Int? = null,
    val isUsed: Boolean = false
)

@Entity(tableName = "leagues")
data class LeagueEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // "CLASSIC" or "H2H"
    val inviteCode: String,
    val creatorId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "league_members")
data class LeagueMemberEntity(
    @PrimaryKey val id: String, // "${leagueId}_${userId}"
    val leagueId: String,
    val userId: String,
    val userName: String,
    val totalPoints: Int = 0,
    val gwPoints: Int = 0,
    val h2hWon: Int = 0,
    val h2hDrawn: Int = 0,
    val h2hLost: Int = 0,
    val h2hPoints: Int = 0,
    val pointsDiff: Int = 0,
    val isGwMvp: Boolean = false
)

@Entity(tableName = "h2h_matchups")
data class H2HMatchupEntity(
    @PrimaryKey val id: String,
    val leagueId: String,
    val gwNumber: Int,
    val user1Id: String,
    val user1Name: String,
    val user1Score: Int = 0,
    val user2Id: String,
    val user2Name: String,
    val user2Score: Int = 0,
    val winnerUserId: String? = null,
    val isDraw: Boolean = false,
    val isFinished: Boolean = false
)

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String, // "${userId}_${badgeKey}"
    val userId: String,
    val badgeKey: String, // "THE_SNIPER", "CLEAN_SHEET_MASTER", "CAPTAIN_FANTASTIC", "UNSTOPPABLE", "LIFESAVER"
    val title: String,
    val description: String,
    val lore: String,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val unlockedDate: String? = null,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1
)
