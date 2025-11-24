package ru.app.pawbuddy.presentation.feature.pet.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.app.pawbuddy.R
import ru.app.pawbuddy.domain.model.PetBreed
import ru.app.pawbuddy.domain.model.PetData
import ru.app.pawbuddy.domain.usecase.AddPetUseCase
import javax.inject.Inject

@HiltViewModel
class AddPetViewModel @Inject constructor(
    private val addPetUseCase: AddPetUseCase
) : ViewModel() {

    // === LiveData для UI ===
    private val _selectedBreedLiveData = MutableLiveData<PetBreed?>()
    val selectedBreedLiveData: LiveData<PetBreed?> = _selectedBreedLiveData

    private val _petNameLiveData = MutableLiveData<String?>()
    val petNameLiveData: LiveData<String?> = _petNameLiveData

    private val _petImageLiveData = MutableLiveData<Bitmap?>()
    val petImageLiveData: LiveData<Bitmap?> = _petImageLiveData

    private val _petSizeLiveData = MutableLiveData<String?>()
    val petSizeLiveData: LiveData<String?> = _petSizeLiveData

    private val _petWeightLiveData = MutableLiveData<Float?>()
    val petWeightLiveData: LiveData<Float?> = _petWeightLiveData

    private val _isSizeStepReady = MutableLiveData(false)
    val isSizeStepReady: LiveData<Boolean> = _isSizeStepReady

    // === StateFlow для добавления питомца ===
    private val _addPetState = MutableStateFlow<AddPetState>(AddPetState.Idle)
    val addPetState: StateFlow<AddPetState> = _addPetState.asStateFlow()

    // === UI setters ===
    fun setSelectedBreed(breed: PetBreed) { _selectedBreedLiveData.value = breed }
    fun setPetName(name: String) { _petNameLiveData.value = name }
    fun setPetImage(image: Bitmap) { _petImageLiveData.value = image }
    fun setPetSize(size: String) {
        _petSizeLiveData.value = size
        _isSizeStepReady.value = true
    }
    fun setPetWeight(weight: Float) { _petWeightLiveData.value = weight }
    fun setSizeStepReady(isReady: Boolean) { _isSizeStepReady.value = isReady }

    fun isContinueEnabledForStep(stepId: Int): Boolean {
        return when (stepId) {
            R.id.breedFragment -> _selectedBreedLiveData.value != null
            R.id.nameFragment -> {
                val isNameNotEmpty = !_petNameLiveData.value.isNullOrEmpty()
                val isImageSelected = _petImageLiveData.value != null
                isNameNotEmpty && isImageSelected
            }
            R.id.sizeFragment -> _petSizeLiveData.value != null
            R.id.weightFragment -> _petWeightLiveData.value != null
            else -> false
        }
    }

    // === Добавление питомца ===
    fun addPet() {
        val breed = _selectedBreedLiveData.value ?: return
        val name = _petNameLiveData.value ?: return
        val size = _petSizeLiveData.value ?: return
        val weight = _petWeightLiveData.value?.toDouble() ?: return

        val pet = PetData(
            name = name,
            breed = breed.name,
            size = size,
            weight = weight
        )

        viewModelScope.launch {
            _addPetState.value = AddPetState.Loading
            try {
                addPetUseCase(pet)
                _addPetState.value = AddPetState.Success
            } catch (e: Exception) {
                _addPetState.value = AddPetState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun PetData(name: String, breed: String, size: String, weight: Double) {

    }
}

// === State для добавления питомца ===
sealed class AddPetState {
    object Idle : AddPetState()
    object Loading : AddPetState()
    object Success : AddPetState()
    data class Error(val message: String) : AddPetState()
}
