package pt.ipca.roomies.data.repositories

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.Habitation

class HabitationRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getHabitations(): List<Habitation> {
        // Assuming you have a "habitations" collection in Firestore
        return firestore.collection("habitations")
            .get()
            .await()
            .toObjects(Habitation::class.java)
    }

    suspend fun createHabitation(habitation: Habitation): DocumentReference {
        // Assuming you have a "habitations" collection in Firestore
        return firestore.collection("habitations")
            .add(habitation)
            .await()
    }

    // Add more methods as needed for habitation-related operations

}
