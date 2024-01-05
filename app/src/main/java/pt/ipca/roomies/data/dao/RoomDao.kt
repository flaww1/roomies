package pt.ipca.roomies.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.Update

@Dao
interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: pt.ipca.roomies.data.entities.Room)

    @Query("SELECT * FROM rooms WHERE roomId = :roomId")
    suspend fun getRoomById(roomId: String): pt.ipca.roomies.data.entities.Room?

    @Query("SELECT * FROM rooms")
    suspend fun getAllRooms(): List<pt.ipca.roomies.data.entities.Room>

    @Query("DELETE FROM rooms")
    suspend fun deleteAllRooms()

    @Query("SELECT * FROM rooms LIMIT 1")
    suspend fun getNextRoom(): pt.ipca.roomies.data.entities.Room?

    @Query("DELETE FROM rooms WHERE roomId = :roomId")
    suspend fun deleteRoomById(roomId: String)

    // update room

    @Query("UPDATE rooms SET roomImages = :roomImages WHERE roomId = :roomId")
    suspend fun updateRoomImages(roomId: String, roomImages: List<String>)

    @Query("UPDATE rooms SET roomAmenities = :roomAmenities WHERE roomId = :roomId")
    suspend fun updateRoomAmenities(roomId: String, roomAmenities: List<String>)

    @Query("UPDATE rooms SET description = :description WHERE roomId = :roomId")
    suspend fun updateRoomDescription(roomId: String, description: String)

    @Query("UPDATE rooms SET price = :price WHERE roomId = :roomId")
    suspend fun updateRoomPrice(roomId: String, price: Double)

    @Query("UPDATE rooms SET leaseDuration = :leaseDuration WHERE roomId = :roomId")
    suspend fun updateRoomLeaseDuration(roomId: String, leaseDuration: String)

    @Query("UPDATE rooms SET roomType = :roomType WHERE roomId = :roomId")
    suspend fun updateRoomType(roomId: String, roomType: String)

    @Query("UPDATE rooms SET roomStatus = :roomStatus WHERE roomId = :roomId")
    suspend fun updateRoomStatus(roomId: String, roomStatus: String)

    @Query("UPDATE rooms SET roomSize = :roomSize WHERE roomId = :roomId")
    suspend fun updateRoomSize(roomId: String, roomSize: String)


    @Update
    suspend fun updateRoom(room: pt.ipca.roomies.data.entities.Room)


    @Query("SELECT * FROM rooms WHERE roomId IN (:roomIds)")
    suspend fun getRoomsByIds(roomIds: List<String>): List<pt.ipca.roomies.data.entities.Room>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<pt.ipca.roomies.data.entities.Room>)

}
