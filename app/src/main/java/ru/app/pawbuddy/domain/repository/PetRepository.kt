package ru.app.pawbuddy.domain.repository


import kotlinx.coroutines.flow.Flow
import ru.app.pawbuddy.domain.model.PetData

interface PetRepository {
    suspend fun addPet(pet: PetData): Result<Unit>
    suspend fun updatePet(pet: PetData): Result<Unit>
    suspend fun deletePet(petId: String): Result<Unit>
    fun getPetsStream(): Flow<List<PetData>>
    suspend fun getPetById(id: String): Result<PetData?>
}
