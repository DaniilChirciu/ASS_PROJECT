package ru.app.pawbuddy.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import ru.app.pawbuddy.domain.model.PetData

class RemotePetDataSource(private val database: FirebaseDatabase) {

    private val petsRef = database.getReference("pets")

    suspend fun addPet(pet: PetData) {
        val id = if (pet.petId.isEmpty()) petsRef.push().key ?: throw IllegalStateException("No key") else pet.petId
        petsRef.child(id).setValue(pet.toMap()).await()
    }

    suspend fun updatePet(pet: PetData) {
        if (pet.petId.isEmpty()) throw IllegalArgumentException("Pet ID required")
        petsRef.child(pet.petId).setValue(pet.toMap()).await()
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
                val list = snapshot.children.mapNotNull { it.toPetData() }
                trySend(list).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        petsRef.addValueEventListener(listener)
        awaitClose { petsRef.removeEventListener(listener) }
    }

    // === Mapper helpers ===
    private fun DataSnapshot.toPetData(): PetData? {
        val id = key ?: return null
        val name = child("petName").getValue(String::class.java) ?: ""
        val breed = child("breedName").getValue(String::class.java) ?: ""
        val image = child("petImageBase64").getValue(String::class.java) ?: ""
        val size = child("petSize").getValue(String::class.java) ?: ""
        val weight = child("petWeight").getValue(Double::class.java) ?: 0.0
        return PetData(
            petId = id,
            petName = name,
            breedName = breed,
            petImageBase64 = image,
            petSize = size,
            petWeight = weight.toString()
        )
    }

    private fun PetData.toMap(): Map<String, Any?> = mapOf(
        "petName" to petName,
        "breedName" to breedName,
        "petImageBase64" to petImageBase64,
        "petSize" to petSize,
        "petWeight" to petWeight
    )
}
