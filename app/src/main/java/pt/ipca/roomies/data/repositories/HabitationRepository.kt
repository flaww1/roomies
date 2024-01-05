package pt.ipca.roomies.data.repositories


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import pt.ipca.roomies.data.dao.HabitationDao

import pt.ipca.roomies.data.entities.Habitation

class HabitationRepository(private val habitationDao: HabitationDao) {
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

            // Save habitation to local Room database
            withContext(Dispatchers.IO) {
                habitationDao.insertHabitation(habitation)
            }

            onSuccess(documentId)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    // In pt.ipca.roomies.data.repositories.HabitationRepository or wherever you query the database...
    suspend fun getHabitations(onSuccess: (List<Habitation>) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            // Attempt to fetch from Firestore
            val habitations = try {
                firestore.collection("habitations")
                    .get()
                    .await()
                    .toObjects(Habitation::class.java)
            } catch (e: Exception) {
                onFailure(e)
                return
            }

            // Save fetched habitations to local Room database
            withContext(Dispatchers.IO) {
                habitationDao.insertAllHabitations(habitations)
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
            // Delete from Firestore
            firestore.collection("habitations")
                .document(habitationId)
                .delete()
                .await()

            // Delete from local Room database
            withContext(Dispatchers.IO) {
                habitationDao.deleteHabitationById(habitationId)
            }

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
            // Update in Firestore
            firestore.collection("habitations")
                .document(habitationId)
                .set(updatedHabitation)
                .await()

            // Update in local Room database
            withContext(Dispatchers.IO) {
                habitationDao.updateHabitation(updatedHabitation)
            }

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
            // Attempt to fetch from Firestore
            val habitations = try {
                firestore.collection("habitations")
                    .whereEqualTo("landlordId", landlordId)
                    .get()
                    .await()
                    .toObjects(Habitation::class.java)
            } catch (e: Exception) {
                onFailure(e)
                return
            }

            // Save fetched habitations to local Room database
            withContext(Dispatchers.IO) {
                habitationDao.insertAllHabitations(habitations)
            }

            onSuccess(habitations)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    suspend fun getHabitationById(habitationId: String): Habitation? {
        return habitationDao.getHabitationById(habitationId)


    }
}
