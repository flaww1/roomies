// AppDatabase.kt
package pt.ipca.roomies.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import pt.ipca.roomies.data.dao.*
import pt.ipca.roomies.data.entities.*


@Database(
    entities = [
        User::class,
        UserProfile::class,
        Like::class,
        Match::class,
        pt.ipca.roomies.data.entities.Room::class,
        Habitation::class,
        ProfileTags::class,
        UserTags::class,
        SelectedTag::class

    ],
    version = 2,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {


    abstract fun userDao(): UserDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun likeMatchDao(): LikeMatchDao
    abstract fun roomDao(): RoomDao

    abstract fun profileTagsDao(): ProfileTagsDao

    abstract fun habitationDao(): HabitationDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "roomies.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
        // Define the migration from version 1 to version 2

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a new table with the updated schema
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `rooms_new` (" +
                            "`roomId` TEXT NOT NULL, " +
                            "`habitationId` TEXT NOT NULL, " +
                            "`description` TEXT NOT NULL, " +
                            "`price` REAL NOT NULL, " +
                            "`roomAmenities` TEXT NOT NULL, " +
                            "`likedByUsers` TEXT NOT NULL, " +
                            "`dislikedByUsers` TEXT NOT NULL, " +
                            "`matches` TEXT NOT NULL, " +
                            "`createdAt` INTEGER NOT NULL, " +
                            "`updatedAt` INTEGER NOT NULL, " +
                            "`roomImages` TEXT NOT NULL, " +
                            "`leaseDuration` TEXT NOT NULL, " +
                            "`roomType` TEXT NOT NULL, " +
                            "`roomStatus` TEXT NOT NULL, " +
                            "`roomSize` TEXT NOT NULL, " +
                            "PRIMARY KEY(`roomId`))"
                )

                // Copy data from the old table to the new table
                database.execSQL(
                    "INSERT INTO `rooms_new` (" +
                            "`roomId`, `habitationId`, `description`, `price`, " +
                            "`roomAmenities`, `likedByUsers`, `dislikedByUsers`, `matches`, " +
                            "`createdAt`, `updatedAt`, `roomImages`, `leaseDuration`, " +
                            "`roomType`, `roomStatus`, `roomSize`) " +
                            "SELECT CAST(`roomId` AS TEXT) NOT NULL, `habitationId`, `description`, `price`, " +
                            "`roomAmenities`, `likedByUsers`, `dislikedByUsers`, `matches`, " +
                            "`createdAt`, `updatedAt`, `roomImages`, `leaseDuration`, " +
                            "`roomType`, `roomStatus`, `roomSize` FROM `rooms`"
                )

                // Remove the old table
                database.execSQL("DROP TABLE IF EXISTS `rooms`")

                // Rename the new table to the original table name
                database.execSQL("ALTER TABLE `rooms_new` RENAME TO `rooms`")
            }
        }





    }
}
