package ru.app.pawbuddy.data.mapper

import com.google.firebase.database.DataSnapshot
import ru.app.pawbuddy.domain.model.PetData

fun PetData.toMap(): Map<String, Any?> = mapOf(
    "name" to name,
    "breed" to breed,
    "weight" to weight,
    "size" to size
)

fun DataSnapshot.toPetData(): Unit? {
    val id = key ?: return null
    val name = child("name").getValue(String::class.java) ?: ""
    val breed = child("breed").getValue(String::class.java) ?: ""
    val weight = child("weight").getValue(Double::class.java) ?: 0.0
    val size = child("size").getValue(String::class.java) ?: ""
    return PetData(id, name, breed, weight.toString(), size)
}

fun PetData(petId: String, breedName: String, petName: String, petSize: String, petWeight: String) {

}
