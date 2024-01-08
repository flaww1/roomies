package pt.ipca.roomies.data.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import pt.ipca.roomies.data.entities.Converters.*
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.Parceler


@Entity(tableName = "rooms")
@TypeConverters(
    Converters.RoomAmenitiesListConverter::class,
    Converters.LeaseDurationConverter::class,
    Converters.RoomTypeConverter::class,
    Converters.RoomStatusConverter::class,
    Converters.RoomSizeConverter::class,
    Converters.StringListConverter::class,

)

@Parcelize
data class Room(
    @PrimaryKey
    var roomId: String = "",
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.createTypedArrayList(RoomAmenities.CREATOR) ?: emptyList(),
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!,
        parcel.readLong(),
        parcel.readLong(),
        parcel.createStringArrayList()!!,
        LeaseDuration.valueOf(parcel.readString()!!),
        RoomType.valueOf(parcel.readString()!!),
        RoomStatus.valueOf(parcel.readString()!!),
        RoomSize.valueOf(parcel.readString()!!)
    )



    constructor() : this(
        "",
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

    companion object : Parceler<Room> {

        override fun Room.write(parcel: Parcel, flags: Int) {
            parcel.writeString(roomId)
            parcel.writeString(habitationId)
            parcel.writeString(description)
            parcel.writeDouble(price)
            parcel.writeStringList(likedByUsers)
            parcel.writeStringList(dislikedByUsers)
            parcel.writeStringList(matches)
            parcel.writeLong(createdAt)
            parcel.writeLong(updatedAt)
            parcel.writeStringList(roomImages)
            parcel.writeString(leaseDuration.duration)
            parcel.writeString(roomType.type)
            parcel.writeString(roomStatus.status)
            parcel.writeString(roomSize.size)
        }

        override fun create(parcel: Parcel): Room {
            return Room(parcel)

        }


    }


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

    companion object {
        val CREATOR: Parcelable.Creator<RoomAmenities> =
            object : Parcelable.Creator<RoomAmenities> {
                override fun createFromParcel(source: Parcel): RoomAmenities =
                    entries[source.readInt()]

                override fun newArray(size: Int): Array<RoomAmenities?> = arrayOfNulls(size)
            }
    }


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


