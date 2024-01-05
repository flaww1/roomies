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
    version = 1,
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
        // Define the migration from version 1 to version 2
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create the new table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `habitations_temp` (`habitationId` TEXT NOT NULL, `landlordId` TEXT NOT NULL, `address` TEXT NOT NULL, `city` TEXT NOT NULL, `numberOfRooms` INTEGER NOT NULL, `numberOfBathrooms` INTEGER NOT NULL, `habitationType` TEXT NOT NULL, `description` TEXT NOT NULL, `habitationAmenities` TEXT NOT NULL, `securityMeasures` TEXT NOT NULL, `petsAllowed` INTEGER NOT NULL, `smokingPolicy` TEXT NOT NULL, `noiseLevel` TEXT NOT NULL, `guestPolicy` TEXT NOT NULL, PRIMARY KEY(`habitationId`))"
                )

                // Copy data from the old table to the new table, specifying the columns
                db.execSQL(
                    "INSERT INTO `habitations_temp` (`habitationId`, `landlordId`, `address`, `city`, `numberOfRooms`, `numberOfBathrooms`, `habitationType`, `description`, `habitationAmenities`, `securityMeasures`, `petsAllowed`, `smokingPolicy`, `noiseLevel`, `guestPolicy`) SELECT `habitationId`, `landlordId`, `address`, `city`, `numberOfRooms`, `numberOfBathrooms`, `habitationType`, `description`, `habitationAmenities`, `securityMeasures`, `petsAllowed`, `smokingPolicy`, `noiseLevel`, `guestPolicy` FROM `habitations`"
                )

                // Remove the old table
                db.execSQL("DROP TABLE IF EXISTS `habitations`")

                // Rename the new table to the original table name
                db.execSQL("ALTER TABLE `habitations_temp` RENAME TO `habitations`")
            }
        }

    }
}
