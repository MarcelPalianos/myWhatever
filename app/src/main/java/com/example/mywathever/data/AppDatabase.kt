package com.example.mywathever.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [GoalEntity::class, UserProgress::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun userProgressDao(): UserProgressDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val MIGRATION_1_2 = object : Migration(1, 2) {
                    override fun migrate(db: SupportSQLiteDatabase) {
                        // Create user_progress table
                        db.execSQL(
                            """
            CREATE TABLE IF NOT EXISTS user_progress (
                id INTEGER NOT NULL,
                xp INTEGER NOT NULL,
                level INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
                        )
                    }
                }
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "goals_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
