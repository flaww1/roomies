package pt.ipca.roomies

import android.app.Application
import com.facebook.stetho.Stetho
import pt.ipca.roomies.data.local.AppDatabase

class MyApplication : Application() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize Stetho for database inspection
        Stetho.initializeWithDefaults(this)

        // Initialize the Room database
        database = AppDatabase.getDatabase(this)
    }
}
