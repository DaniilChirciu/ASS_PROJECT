package ru.app.pawbuddy.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.app.pawbuddy.data.remote.RemotePetDataSource
import ru.app.pawbuddy.domain.model.PetData
import ru.app.pawbuddy.domain.repository.PetRepository

class PetRepositoryImpl(
    private val remote: RemotePetDataSource
) : PetRepository {

    override suspend fun addPet(pet: Unit) = runCatching {
        remote.addPet(pet)
    }

    override suspend fun updatePet(pet: PetData) = runCatching {
        remote.updatePet(pet)
    }

    override suspend fun deletePet(petId: String) = runCatching {
        remote.deletePet(petId)
    }

    override fun getPetsStream(): Flow<List<PetData>> {
        return remote.observePets().map { it } // уже List<PetData>
    }

    override suspend fun getPetById(id: String) = runCatching {
        remote.getPetOnce(id)
    }
}
