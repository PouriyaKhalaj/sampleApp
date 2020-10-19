package ir.co.repository.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ir.co.common.dto.User
import ir.co.repository.db.users.UsersDao

/**
 * Created by pooriettaw on 17,October,2020
 */
@Database(
    entities = [User::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getUsersDao(): UsersDao

    companion object {
        const val DATABASE_NAME = "basic-sample-db"

        @Volatile
        private var instance: AppDatabase? = null
        private var LOCK = AppDatabase::class

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE UserEntity (id INTEGER, first_name TEXT, last_name TEXT, PRIMARY KEY(id))")
                database.execSQL("DROP TABLE users")
            }
        }

        operator fun invoke(context: Context): AppDatabase =
            instance ?: synchronized(LOCK) {
                instance ?: Room
                    .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            }
    }
}