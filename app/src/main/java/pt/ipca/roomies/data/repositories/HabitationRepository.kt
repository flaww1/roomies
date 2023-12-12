package pt.ipca.roomies.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.Habitation

class HabitationRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createHabitation(habitation: Habitation, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            val documentReference = firestore.collection("habitations")
                .add(habitation)
                .await()

            // Get the document ID
            val documentId = documentReference.id
            onSuccess(documentId)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    suspend fun getHabitations(onSuccess: (List<Habitation>) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            val habitations = firestore.collection("habitations")
                .get()
                .await()
                .toObjects(Habitation::class.java)
            onSuccess(habitations)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    suspend fun deleteHabitation(habitationId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        try {
            firestore.collection("habitations")
                .document(habitationId)
                .delete()
                .await()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    suspend fun updateHabitation(habitationId: String, updatedHabitation: Habitation, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        try {
            firestore.collection("habitations")
                .document(habitationId)
                .set(updatedHabitation)
                .await()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    // Add more methods as needed for habitation-related operations
}
