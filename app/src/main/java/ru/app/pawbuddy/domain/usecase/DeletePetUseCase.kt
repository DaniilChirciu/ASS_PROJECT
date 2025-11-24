package ru.app.pawbuddy.domain.usecase

import ru.app.pawbuddy.domain.repository.PetRepository

class DeletePetUseCase(private val repo: PetRepository) {
    suspend operator fun invoke(id: String) = repo.deletePet(id)
}
