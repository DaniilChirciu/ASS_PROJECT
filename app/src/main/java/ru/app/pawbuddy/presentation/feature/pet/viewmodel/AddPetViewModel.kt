package ru.app.pawbuddy.presentation.feature.pet.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.app.pawbuddy.R
import ru.app.pawbuddy.domain.model.PetBreed

class AddPetViewModel : ViewModel() {

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

    private val _isSizeStepReady = MutableLiveData<Boolean>(false)
    val isSizeStepReady: LiveData<Boolean> = _isSizeStepReady

    fun setSelectedBreed(breed: PetBreed) {
        _selectedBreedLiveData.value = breed
    }

    fun setPetName(name: String) {
        _petNameLiveData.value = name
    }

    fun setPetImage(image: Bitmap) {
        _petImageLiveData.value = image
    }

    fun setPetSize(size: String) {
        _petSizeLiveData.value = size
        _isSizeStepReady.value = true
    }

    fun setPetWeight(weight: Float) {
        _petWeightLiveData.value = weight
    }

    fun setSizeStepReady(isReady: Boolean) {
        _isSizeStepReady.value = isReady
    }

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
}
