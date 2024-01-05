package pt.ipca.roomies.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import pt.ipca.roomies.data.entities.Converters.*





@Entity(tableName = "rooms")
@TypeConverters(
    Converters.RoomAmenitiesListConverter::class,
    Converters.LeaseDurationConverter::class,
    Converters.RoomTypeConverter::class,
    Converters.RoomStatusConverter::class,
    Converters.RoomSizeConverter::class,
    Converters.StringListConverter::class,

)

data class Room(
    @PrimaryKey(autoGenerate = true)
    var roomId: Long = 0,
    val habitationId: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val roomAmenities: List<RoomAmenities>,
    val likedByUsers: List<String> = emptyList(),
    val dislikedByUsers: List<String> = emptyList(),
    val matches: List<String> = emptyList(),
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    var roomImages: List<String> = emptyList(),
    val leaseDuration: LeaseDuration,
    val roomType: RoomType,
    val roomStatus: RoomStatus,
    val roomSize: RoomSize
) {
    constructor() : this(
        0,
        "",
        "",
        0.0,
        listOf(),
        listOf(),
        listOf(),
        listOf(),
        0,
        0,
        emptyList(),
        LeaseDuration.ONE_MONTH,
        RoomType.SINGLE,
        RoomStatus.AVAILABLE,
        RoomSize.SMALL
    )
}




@TypeConverters(RoomTypeConverter::class)
enum class RoomType {
    SINGLE,
    DOUBLE,
    TRIPLE,
    QUADRUPLE;

    val type: String
        get() = name
}

@TypeConverters(RoomAmenitiesConverter::class)
enum class RoomAmenities {
    AIR_CONDITIONING,
    HEATING,
    BALCONY,
    PRIVATE_BATHROOM;

    val amenity: String
        get() = name


}

@TypeConverters(RoomStatusConverter::class)
enum class RoomStatus {
    AVAILABLE,
    UNAVAILABLE,
    PENDING,
    RENTED;

    val status: String
        get() = name

}
@TypeConverters(RoomSizeConverter::class)
enum class RoomSize {
    SMALL,
    MEDIUM,
    LARGE;

    val size: String
        get() = name

}

@TypeConverters(LeaseDurationConverter::class)
enum class LeaseDuration {
    ONE_MONTH,
    THREE_MONTHS,
    SIX_MONTHS,
    ONE_YEAR,
    TWO_YEARS,
    THREE_YEARS;

    val duration: String
        get() = name

}


