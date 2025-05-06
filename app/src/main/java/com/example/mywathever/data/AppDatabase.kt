package com.example.mywathever.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [GoalEntity::class, UserProgress::class, ShoppingItemEntity::class],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun shoppingItemDao(): ShoppingItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                // Migration from version 7 to 8: Add xpAwarded and xpDeductible columns to goal_table.
                val MIGRATION_7_8 = object : Migration(7, 8) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        // Add xpAwarded if not already present.
                        if (!columnExists(database, "goal_table", "xpAwarded")) {
                            database.execSQL("ALTER TABLE goal_table ADD COLUMN xpAwarded INTEGER NOT NULL DEFAULT 0")
                        }
                        // Add xpDeductible if not already present.
                        if (!columnExists(database, "goal_table", "xpDeductible")) {
                            database.execSQL("ALTER TABLE goal_table ADD COLUMN xpDeductible INTEGER NOT NULL DEFAULT 0")
                        }
                    }

                    // Helper function to check if a column exists in a table.
                    @SuppressLint("Range")
                    private fun columnExists(database: SupportSQLiteDatabase, tableName: String, columnName: String): Boolean {
                        val cursor = database.query("PRAGMA table_info($tableName)")
                        var exists = false
                        while (cursor.moveToNext()) {
                            val currentColumn = cursor.getString(cursor.getColumnIndex("name"))
                            if (currentColumn == columnName) {
                                exists = true
                                break
                            }
                        }
                        cursor.close()
                        return exists
                    }
                }

                // Migration from version 8 to 9: Create shopping_item_table.
                val MIGRATION_8_9 = object : Migration(8, 9) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS shopping_item_table (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                goalId INTEGER NOT NULL,
                                itemName TEXT NOT NULL,
                                amount INTEGER NOT NULL,
                                price REAL NOT NULL
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
                    .addMigrations(MIGRATION_7_8, MIGRATION_8_9)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
