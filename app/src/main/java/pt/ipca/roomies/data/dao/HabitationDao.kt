package pt.ipca.roomies.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import pt.ipca.roomies.data.entities.Habitation

@Dao
interface HabitationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitation(habitation: Habitation)

    @Query("SELECT * FROM habitations WHERE habitationId = :habitationId")
    suspend fun getHabitationById(habitationId: String): Habitation?

    @Query("SELECT * FROM habitations")
    suspend fun getAllHabitations(): List<Habitation>

    @Query("DELETE FROM habitations")
    suspend fun deleteAllHabitations()

    @Query("DELETE FROM habitations WHERE habitationId = :habitationId")
    suspend fun deleteHabitationById(habitationId: String)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHabitations(habitations: List<Habitation>)

    @Update
    suspend fun updateHabitation(habitation: Habitation)

    @Query("SELECT * FROM habitations WHERE landlordId = :landlordId")
    suspend fun getHabitationsByLandlordId(landlordId: String): List<Habitation>



}
