package ru.app.pawbuddy.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import ru.app.pawbuddy.domain.model.PetData

/**
 * Remote data source that communicates with Firebase Realtime Database.
 * Базовые операции: add/update/delete/get stream.
 */
class RemotePetDataSource(
    private val database: FirebaseDatabase
) {
    private val petsRef = database.getReference("pets")

    suspend fun addPet(pet: PetData) {
        val id = pet.id.ifEmpty { petsRef.push().key ?: throw IllegalStateException("no key") }
        val map = pet.toMap()
        petsRef.child(id).setValue(map).await()
    }

    suspend fun updatePet(pet: PetData) {
        if (pet.id.isEmpty()) throw IllegalArgumentException("pet id required")
        petsRef.child(pet.id).setValue(pet.toMap()).await()
    }

    suspend fun deletePet(id: String) {
        petsRef.child(id).removeValue().await()
    }

    suspend fun getPetOnce(id: String): PetData? {
        val snapshot = petsRef.child(id).get().await()
        return snapshot.toPetData()
    }

    fun observePets() = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<PetData>()
                snapshot.children.forEach { it.toPetData()?.let(list::add) }
                trySend(list).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        petsRef.addValueEventListener(listener)
        awaitClose { petsRef.removeEventListener(listener) }
    }

    // Simple mapper helpers (adjust to your PetData fields)
    private fun DataSnapshot.toPetData(): PetData? {
        val id = key ?: return null
        val name = child("name").getValue(String::class.java) ?: ""
        val breed = child("breed").getValue(String::class.java) ?: ""
        val weight = child("weight").getValue(Double::class.java) ?: 0.0
        val size = child("size").getValue(String::class.java) ?: ""
        return PetData(id = id, name = name, breed = breed, weight = weight, size = size)
    }

    private fun PetData.toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "breed" to breed,
        "weight" to weight,
        "size" to size
    )
}

private fun Any.ifEmpty(function: () -> Nothing): Any {

}
