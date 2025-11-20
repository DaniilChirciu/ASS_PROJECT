package ru.app.pawbuddy.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import ru.app.pawbuddy.domain.model.PetData
import ru.app.pawbuddy.domain.model.PetFood
import ru.app.pawbuddy.domain.model.PetReport
import ru.app.pawbuddy.domain.model.PetVaccine
import ru.app.pawbuddy.domain.model.PetWalk

class MyPreferenceManager(context: Context) {
    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun putString(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    // Сохраняем данные питомца в формате JSON
    fun savePetData(petData: PetData) {
        val json = Gson().toJson(petData)
        putString("petData_${petData.petId}", json)
    }


    fun getAllPetData(): List<PetData> {
        val petList = mutableListOf<PetData>()
        // Получаем все записи из SharedPreferences
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            // Фильтруем по префиксу ключа "petData_"
            if (key.startsWith("petData_") && value is String) {
                val pet = Gson().fromJson(value, PetData::class.java)
                petList.add(pet)
            }
        }
        return petList
    }

    fun getPetDataById(id: String): PetData? {
        val json = getString("petData_$id")
        return if (json != null) {
            Gson().fromJson(json, PetData::class.java)
        } else null
    }

    fun deletePetById(id: String) {
        sharedPreferences.edit().remove("petData_$id").apply()
    }

    fun addFoodToPet(petId: String, petFood: PetFood) {
        val petData = getPetDataById(petId)
        petData?.let {
            val updatedFoodList = it.foodList.orEmpty() + petFood // Гарантируем, что список не null
            val updatedPet = it.copy(foodList = updatedFoodList)
            savePetData(updatedPet)
        }
    }

    fun removeFoodFromPet(petId: String, petFood: PetFood) {
        val petData = getPetDataById(petId)
        petData?.let {
            val updatedFoodList = it.foodList.filterNot { it == petFood } // Удаляем еду
            val updatedPet = it.copy(foodList = updatedFoodList)
            savePetData(updatedPet)
        }
    }


    fun addWalkToPet(petId: String, petWalk: PetWalk) {
        val petData = getPetDataById(petId)
        petData?.let {
            val updatedWalkList = it.walkList.orEmpty() + petWalk // Добавляем прогулку
            val updatedPet = it.copy(walkList = updatedWalkList)
            savePetData(updatedPet)
        }
    }

    fun removeWalkFromPet(petId: String, petWalk: PetWalk) {
        val petData = getPetDataById(petId)
        petData?.let {
            val updatedWalkList = it.walkList.filterNot { it == petWalk } // Удаляем прогулку
            val updatedPet = it.copy(walkList = updatedWalkList)
            savePetData(updatedPet)
        }
    }




    fun getPetInsurance(petId: String): String? {
        return getPetDataById(petId)?.petInsuranceImage
    }

    fun getPetPassport(petId: String): String? {
        return getPetDataById(petId)?.petPassportImage
    }

    fun getPetCard(petId: String): String? {
        return getPetDataById(petId)?.petCardImage
    }

    fun addVaccineToPet(petId: String, petVaccine: PetVaccine) {
        val petData = getPetDataById(petId)
        petData?.let {
            val updatedVaccineList = it.vaccineList.orEmpty() + petVaccine
            val updatedPet = it.copy(vaccineList = updatedVaccineList)
            savePetData(updatedPet)
        }
    }

    fun removeVaccineFromPet(petId: String, petVaccine: PetVaccine) {
        val petData = getPetDataById(petId)
        petData?.let {
            val updatedVaccineList = it.vaccineList.filterNot { it == petVaccine }
            val updatedPet = it.copy(vaccineList = updatedVaccineList)
            savePetData(updatedPet)
        }
    }

    fun getVaccinesForPet(petId: String): List<PetVaccine> {
        return getPetDataById(petId)?.vaccineList ?: emptyList()
    }


    fun addReportToPet(petId: String, petReport: PetReport) {
        val petData = getPetDataById(petId)
        petData?.let {
            val updatedReportList = it.reports.orEmpty() + petReport
            val updatedPet = it.copy(reports = updatedReportList)
            savePetData(updatedPet)
        }
    }

    fun removeReportFromPet(petId: String, petReport: PetReport) {
        val petData = getPetDataById(petId)
        petData?.let {
            val updatedReportList = it.reports.filterNot { it == petReport }
            val updatedPet = it.copy(reports = updatedReportList)
            savePetData(updatedPet)
        }
    }

    fun getReportsForPet(petId: String): List<PetReport> {
        return getPetDataById(petId)?.reports ?: emptyList()
    }


    fun savePetInsurance(petId: String, imageBase64: String) {
        val petData = getPetDataById(petId)
        petData?.let {
            val updatedPet = it.copy(
                petInsuranceImage = imageBase64,
                vaccineList = it.vaccineList ?: emptyList(),
                walkList = it.walkList ?: emptyList(),
                foodList = it.foodList ?: emptyList(),
                reports = it.reports ?: emptyList()
            )
            savePetData(updatedPet)
        }
    }

    fun savePetPassport(petId: String, imageBase64: String) {
        val petData = getPetDataById(petId)
        petData?.let {
            val updatedPet = it.copy(
                petPassportImage = imageBase64,
                vaccineList = it.vaccineList ?: emptyList(),
                walkList = it.walkList ?: emptyList(),
                foodList = it.foodList ?: emptyList(),
                reports = it.reports ?: emptyList()
            )
            savePetData(updatedPet)
        }
    }

    fun savePetCard(petId: String, imageBase64: String) {
        val petData = getPetDataById(petId)
        petData?.let {
            val updatedPet = it.copy(
                petCardImage = imageBase64,
                vaccineList = it.vaccineList ?: emptyList(),
                walkList = it.walkList ?: emptyList(),
                foodList = it.foodList ?: emptyList(),
                reports = it.reports ?: emptyList()
            )
            savePetData(updatedPet)
        }
    }

    fun savePetGender(petId: String, gender: String) {
        val petData = getPetDataById(petId)
        petData?.let {
            val updatedPet = it.copy(petGender = gender)
            savePetData(updatedPet)
        }
    }

    fun savePetBreed(petId: String, breedName: String) {
        val petData = getPetDataById(petId)
        petData?.let {
            val updatedPet = it.copy(breedName = breedName)
            savePetData(updatedPet)
        }
    }




}
