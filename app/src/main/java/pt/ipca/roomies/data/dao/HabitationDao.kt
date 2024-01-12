package pt.ipca.roomies.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import pt.ipca.roomies.data.entities.Habitation

@Dao //consultas personalizadas
interface HabitationDao { //operacoes CRUD que usam DAO(Data Access Object) um padrão estrutural, isola a camada de aplicação/negócio da camada de persistência, usando uma API abstrata, não é necessario às camadas conhecerem-se.

    @Insert(onConflict = OnConflictStrategy.REPLACE) //caso de conflito, substitui por novas
    suspend fun insertHabitation(habitation: Habitation) //preciso requer evitar bloqueios thread principa  main
//operacoes mais demoradas pela questão de I/O e procura de registos, convem nao bloquear outras
    @Query("SELECT * FROM habitations WHERE habitationId = :habitationId")
    suspend fun getHabitationById(habitationId: String): Habitation?

    @Query("SELECT * FROM habitations")
    suspend fun getAllHabitations(): List<Habitation>  //buscar tudo na lista 

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
