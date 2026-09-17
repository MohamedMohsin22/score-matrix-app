package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.ChipEntity
import com.example.data.local.entity.FixtureEntity
import com.example.data.local.entity.GameweekEntity
import com.example.data.local.entity.H2HMatchupEntity
import com.example.data.local.entity.LeagueEntity
import com.example.data.local.entity.LeagueMemberEntity
import com.example.data.local.entity.PredictionEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:identifier) OR LOWER(username) = LOWER(:identifier) LIMIT 1")
    suspend fun findUser(identifier: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isCurrentUser = 0")
    suspend fun clearCurrentUser()

    @Query("UPDATE users SET isCurrentUser = 1 WHERE id = :userId")
    suspend fun setCurrentUser(userId: String)
}

@Dao
interface GameweekDao {
    @Query("SELECT * FROM gameweeks ORDER BY CASE WHEN isNext = 1 THEN 0 ELSE 1 END, gwNumber DESC LIMIT 1")
    fun getCurrentGameweekFlow(): Flow<GameweekEntity?>

    @Query("SELECT * FROM gameweeks ORDER BY CASE WHEN isNext = 1 THEN 0 ELSE 1 END, gwNumber DESC LIMIT 1")
    suspend fun getCurrentGameweek(): GameweekEntity?

    @Query("SELECT * FROM gameweeks WHERE gwNumber = :gwNumber LIMIT 1")
    suspend fun getGameweek(gwNumber: Int): GameweekEntity?

    @Query("SELECT * FROM gameweeks WHERE gwNumber = :gwNumber LIMIT 1")
    suspend fun getGameweekByNumber(gwNumber: Int): GameweekEntity?

    @Query("SELECT * FROM gameweeks ORDER BY gwNumber ASC")
    fun getAllGameweeks(): Flow<List<GameweekEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameweek(gameweek: GameweekEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameweeks(gameweeks: List<GameweekEntity>)

    @Update
    suspend fun updateGameweek(gameweek: GameweekEntity)

    @Query("UPDATE gameweeks SET isNext = 0")
    suspend fun clearAllIsNext()
}

@Dao
interface FixtureDao {
    @Query("SELECT * FROM fixtures WHERE gwNumber = :gwNumber ORDER BY kickoffEpochMs ASC")
    fun getFixturesForGw(gwNumber: Int): Flow<List<FixtureEntity>>

    @Query("SELECT * FROM fixtures WHERE gwNumber = :gwNumber ORDER BY kickoffEpochMs ASC")
    suspend fun getFixturesForGwList(gwNumber: Int): List<FixtureEntity>

    @Query("SELECT * FROM fixtures WHERE id = :id LIMIT 1")
    suspend fun getFixtureById(id: String): FixtureEntity?

    @Query("SELECT * FROM fixtures")
    suspend fun getAllFixturesList(): List<FixtureEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFixtures(fixtures: List<FixtureEntity>)

    @Update
    suspend fun updateFixture(fixture: FixtureEntity)

    @Query("DELETE FROM fixtures WHERE gwNumber = :gwNumber")
    suspend fun deleteFixturesForGw(gwNumber: Int)
}

@Dao
interface PredictionDao {
    @Query("SELECT * FROM predictions WHERE userId = :userId AND gwNumber = :gwNumber")
    fun getPredictionsForUserAndGw(userId: String, gwNumber: Int): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions WHERE userId = :userId AND gwNumber = :gwNumber")
    suspend fun getPredictionsList(userId: String, gwNumber: Int): List<PredictionEntity>

    @Query("SELECT * FROM predictions WHERE userId = :userId")
    suspend fun getAllPredictionsForUserList(userId: String): List<PredictionEntity>

    @Query("SELECT * FROM predictions WHERE userId = :userId AND fixtureId = :fixtureId LIMIT 1")
    suspend fun getPrediction(userId: String, fixtureId: String): PredictionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrediction(prediction: PredictionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPredictions(predictions: List<PredictionEntity>)

    @Query("UPDATE predictions SET isCaptain = 0, isSuperCaptain = 0 WHERE userId = :userId AND gwNumber = :gwNumber")
    suspend fun clearCaptainForGw(userId: String, gwNumber: Int)

    @Query("UPDATE predictions SET isSafetyNet = 0 WHERE userId = :userId AND gwNumber = :gwNumber")
    suspend fun clearSafetyNetForGw(userId: String, gwNumber: Int)

    @Query("UPDATE predictions SET isDoubleShot = 0, homeScorePredB = NULL, awayScorePredB = NULL WHERE userId = :userId AND gwNumber = :gwNumber")
    suspend fun clearDoubleShotForGw(userId: String, gwNumber: Int)

    @Query("UPDATE predictions SET isAutoCaptain = 0 WHERE userId = :userId AND gwNumber = :gwNumber")
    suspend fun clearAutoCaptainForGw(userId: String, gwNumber: Int)
}

@Dao
interface ChipDao {
    @Query("SELECT * FROM chips WHERE userId = :userId ORDER BY half ASC, chipType ASC")
    fun getChipsForUser(userId: String): Flow<List<ChipEntity>>

    @Query("SELECT * FROM chips WHERE userId = :userId")
    suspend fun getChipsList(userId: String): List<ChipEntity>

    @Query("SELECT * FROM chips WHERE userId = :userId AND chipType = :chipType AND half = :half LIMIT 1")
    suspend fun getChip(userId: String, chipType: String, half: Int): ChipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChips(chips: List<ChipEntity>)

    @Update
    suspend fun updateChip(chip: ChipEntity)
}

@Dao
interface LeagueDao {
    @Query("SELECT l.* FROM leagues l INNER JOIN league_members m ON l.id = m.leagueId WHERE m.userId = :userId ORDER BY l.createdAt DESC")
    fun getLeaguesForUser(userId: String): Flow<List<LeagueEntity>>

    @Query("SELECT * FROM leagues ORDER BY createdAt DESC")
    fun getAllLeagues(): Flow<List<LeagueEntity>>

    @Query("SELECT * FROM leagues WHERE id = :id LIMIT 1")
    fun getLeagueById(id: String): Flow<LeagueEntity?>

    @Query("SELECT * FROM leagues WHERE UPPER(inviteCode) = UPPER(:code) LIMIT 1")
    suspend fun getLeagueByInviteCode(code: String): LeagueEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeague(league: LeagueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeagueMember(member: LeagueMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeagueMembers(members: List<LeagueMemberEntity>)

    @Query("SELECT * FROM league_members WHERE leagueId = :leagueId ORDER BY totalPoints DESC, gwPoints DESC")
    fun getClassicStandings(leagueId: String): Flow<List<LeagueMemberEntity>>

    @Query("SELECT * FROM league_members WHERE leagueId = :leagueId ORDER BY h2hPoints DESC, pointsDiff DESC, totalPoints DESC")
    fun getH2HStandings(leagueId: String): Flow<List<LeagueMemberEntity>>

    @Query("SELECT * FROM league_members WHERE leagueId = :leagueId")
    suspend fun getLeagueMembersList(leagueId: String): List<LeagueMemberEntity>

    @Update
    suspend fun updateLeagueMember(member: LeagueMemberEntity)

    @Query("SELECT * FROM h2h_matchups WHERE leagueId = :leagueId AND gwNumber = :gwNumber")
    fun getH2HMatchups(leagueId: String, gwNumber: Int): Flow<List<H2HMatchupEntity>>

    @Query("SELECT * FROM h2h_matchups WHERE leagueId = :leagueId AND gwNumber = :gwNumber")
    suspend fun getH2HMatchupsList(leagueId: String, gwNumber: Int): List<H2HMatchupEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertH2HMatchups(matchups: List<H2HMatchupEntity>)

    @Query("SELECT * FROM h2h_matchups WHERE user1Id = :userId OR user2Id = :userId")
    suspend fun getAllH2HMatchupsForUserList(userId: String): List<H2HMatchupEntity>

    @Query("SELECT MAX(h2hWon) FROM league_members WHERE userId = :userId")
    suspend fun getMaxH2HWinsForUser(userId: String): Int?

    @Update
    suspend fun updateH2HMatchup(matchup: H2HMatchupEntity)
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges WHERE userId = :userId ORDER BY isUnlocked DESC, currentProgress DESC")
    fun getBadgesForUser(userId: String): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badges WHERE userId = :userId")
    suspend fun getBadgesForUserList(userId: String): List<BadgeEntity>

    @Query("SELECT * FROM badges WHERE userId = :userId AND badgeKey = :badgeKey LIMIT 1")
    suspend fun getBadge(userId: String, badgeKey: String): BadgeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<BadgeEntity>)

    @Update
    suspend fun updateBadge(badge: BadgeEntity)
}
