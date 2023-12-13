package pt.ipca.roomies.data.entities

data class Room(
    val roomId: String = "",
    val habitationId: String = "",
    val landlordId: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val roomAmenities: MutableList<RoomAmenities>,
    val likedByUsers: List<String> = emptyList(),
    val dislikedByUsers: List<String> = emptyList(),
    val matches: List<String> = emptyList(),
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    var roomImages: List<String> = emptyList(),
    val leaseDuration: LeaseDuration,
    val roomType: RoomType,
    val roomStatus: RoomStatus,
    val roomSize: RoomSize,


    )

enum class RoomType(s: String) {
    SINGLE("Single"),
    DOUBLE("Double"),
    TRIPLE("Triple"),
    QUADRUPLE("Quadruple"),
}

enum class RoomAmenities(s: String) {
    AIR_CONDITIONING("Air Conditioning"),
    HEATING("Heating"),
    BALCONY("Balcony"),
    PRIVATE_BATHROOM("Private Bathroom"),

    // Add other amenities as needed
}

enum class RoomStatus(s: String) {
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable"),
    PENDING("Pending"),
    RENTED("Rented"),

}

enum class RoomSize(s: String) {
    SMALL("Small"),
    MEDIUM("Medium"),
    LARGE("Large"),
    // Add other sizes as needed
}

enum class LeaseDuration(s: String){
    ONE_MONTH("1 Month"),
    THREE_MONTHS("3 Months"),
    SIX_MONTHS("6 Months"),
    ONE_YEAR("1 Year"),
    TWO_YEARS("2 Years"),
    THREE_YEARS("3 Years"),
}


