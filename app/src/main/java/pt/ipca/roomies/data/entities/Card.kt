package pt.ipca.roomies.data.entities

data class CardModel(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
)

data class RoomCardModel(
    val  roomId : String,
    val roomName: String
)
