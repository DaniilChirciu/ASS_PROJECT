package ru.app.pawbuddy.domain.model

data class PetFood(
    val foodImageBase64: String?, // Фото еды
    val foodName: String, // Название еды
    val foodDescription: String?, // Описание еды
    val foodTime: String // Когда ест
)

data class PetVaccine(
    val vaccineName: String, // Название прививки
    val vaccineDate: String  // Дата прививки
)

data class PetWalk(
    val walkDate: String,  // Дата прогулки
    val walkTime: String,  // Время прогулки
    val walkDescription: String? // Комментарий
)



data class PetReport(
    val reportDate: String, // Дата отчета
    val reportImageBase64: String, // Фото отчета
    val reportDescription: String, // Комментарий
    val ownerEmail: String // Почта владельца
)


data class PetData(
    val petId: String,
    val breedName: String,
    val petName: String,
    val petSize: String,
    val petWeight: Float,
    val petImageBase64: String?,
    val petBirthday: String? = null,
    val petOld: String? = null,
    val petAdoptDay: String? = null,
    val foodList: List<PetFood> = emptyList(),
    val petGender: String? = null,
    val walkList: List<PetWalk> = emptyList(),
    val vaccineList: List<PetVaccine> = emptyList(),
    val reports: List<PetReport> = emptyList(),
    val petInsuranceImage: String? = null, // Фото страховки
    val petPassportImage: String? = null, // Фото паспорта
    val petCardImage: String? = null // Фото карточки
)

