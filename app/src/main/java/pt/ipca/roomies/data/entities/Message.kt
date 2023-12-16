package pt.ipca.roomies.data.entities


    data class Message(
        val messageId: String = "", // Unique ID for the message
        val senderUserId: String = "", // ID of the user sending the message
        val receiverUserId: String = "", // ID of the user receiving the message
        val content: String = "", // Content of the message
        val timestamp: Long = 0,// Timestamp indicating when the message was sent
        val matchId: String = "" // ID of the match associated with the message
    )
