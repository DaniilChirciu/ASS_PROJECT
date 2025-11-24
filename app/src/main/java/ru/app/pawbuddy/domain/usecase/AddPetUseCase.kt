package ru.app.pawbuddy.domain.usecase

import ru.app.pawbuddy.domain.model.PetData
import ru.app.pawbuddy.domain.repository.PetRepository

class AddPetUseCase(private val repo: PetRepository) {
    suspend operator fun invoke(pet: PetData) = repo.addPet(pet)
}
