package pt.ipca.roomies.data.entities

data class CardItem(val title: String, val description: String)

sealed class Card {
    data class RoomCard(val room: Room) : Card()
    data class UserCard(val user: User) : Card()
}

