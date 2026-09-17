package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.BadgeDao
import com.example.data.local.dao.ChipDao
import com.example.data.local.dao.FixtureDao
import com.example.data.local.dao.GameweekDao
import com.example.data.local.dao.LeagueDao
import com.example.data.local.dao.PredictionDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.ChipEntity
import com.example.data.local.entity.FixtureEntity
import com.example.data.local.entity.GameweekEntity
import com.example.data.local.entity.H2HMatchupEntity
import com.example.data.local.entity.LeagueEntity
import com.example.data.local.entity.LeagueMemberEntity
import com.example.data.local.entity.PredictionEntity
import com.example.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        GameweekEntity::class,
        FixtureEntity::class,
        PredictionEntity::class,
        ChipEntity::class,
        LeagueEntity::class,
        LeagueMemberEntity::class,
        H2HMatchupEntity::class,
        BadgeEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun gameweekDao(): GameweekDao
    abstract fun fixtureDao(): FixtureDao
    abstract fun predictionDao(): PredictionDao
    abstract fun chipDao(): ChipDao
    abstract fun leagueDao(): LeagueDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pl_predictor_2026_gw4_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
