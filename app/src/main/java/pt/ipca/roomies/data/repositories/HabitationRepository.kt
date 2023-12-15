package pt.ipca.roomies.data.repositories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.roomies.data.entities.Habitation
import pt.ipca.roomies.data.entities.Room

class HabitationRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createHabitation(
        habitation: Habitation,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val documentReference = firestore.collection("habitations")
                .add(habitation)
                .await()

            // Get the document ID
            val documentId = documentReference.id
            Log.d("Firestore", "Generated Habitation ID: $documentId")

            // Update the habitation with the document ID before calling onSuccess
            habitation.habitationId = documentId
            onSuccess(documentId)
        } catch (e: Exception) {
            onFailure(e)
        }
    }



    // In HabitationRepository or wherever you query the database...
    suspend fun getHabitations(onSuccess: (List<Habitation>) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            val habitations = firestore.collection("habitations")
                .get()
                .await()
                .toObjects(Habitation::class.java)

            // Make sure each habitation has a non-empty habitationId
            for (habitation in habitations) {
                if (habitation.habitationId.isNullOrBlank()) {
                    throw Exception("Habitation with invalid ID found: $habitation")
                }
            }

            onSuccess(habitations)
        } catch (e: Exception) {
            onFailure(e)
        }
    }



    suspend fun deleteHabitation(
            habitationId: String,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
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

        suspend fun updateHabitation(
            habitationId: String,
            updatedHabitation: Habitation,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
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

        suspend fun getHabitationsByLandlordId(
            landlordId: String,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            try {
                firestore.collection("habitations")
                    .whereEqualTo("landlordId", landlordId)
                    .get()
                    .await()
                    .toObjects(Habitation::class.java)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }

        }


    suspend fun getHabitationsByLandlordId(
        landlordId: String,
        onSuccess: (List<Habitation>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val habitations = firestore.collection("habitations")
                .whereEqualTo("landlordId", landlordId)
                .get()
                .await()
                .toObjects(Habitation::class.java)
            onSuccess(habitations)
        } catch (e: Exception) {
            onFailure(e)
        }
    }


}

