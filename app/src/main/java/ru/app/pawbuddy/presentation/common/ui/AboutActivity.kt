package ru.app.pawbuddy.presentation.common.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.databinding.ActivityAboutBinding
import ru.app.pawbuddy.domain.model.PetData
import ru.app.pawbuddy.presentation.common.bottomsheet.EditPetDataBottomSheet
import ru.app.pawbuddy.presentation.common.bottomsheet.GenderSelectionBottomSheetDialog
import ru.app.pawbuddy.presentation.util.base64ToBitmap

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    private lateinit var preferenceManager: MyPreferenceManager
    private var petId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }

        preferenceManager = MyPreferenceManager(this)

        petId = intent.getStringExtra("petId")
        Log.d("AboutActivity", "Получен petId: $petId")

        petId?.let { id ->
            val petData = preferenceManager.getPetDataById(id)
            if (petData != null) {
                updateUI(petData)
            } else {
                Log.e("AboutActivity", "Питомец не найден")
                binding.petName.text = "Ошибка данных"
            }
        }
    }

    private fun updateUI(petData: PetData) {
        binding.petName.text = petData.petName
        binding.petBreed.text =  if (petData.breedName.equals("Свой вариант")) "Нажмите чтобы настроить" else petData.breedName
        binding.petWeight.text = "${petData.petWeight} кг"
        binding.petSize.text = petData.petSize
        binding.petImage.setImageBitmap(base64ToBitmap(petData.petImageBase64 ?: ""))
        binding.petBirthday.text = petData.petBirthday ?: "Нажмите чтобы настроить"
        binding.petOld.text = petData.petOld ?: ""
        binding.petAdoptDay.text = petData.petAdoptDay ?: "Нажмите чтобы настроить"

        // Отображение пола питомца

        binding.petGender.text = petData.petGender ?: "Нажмите чтобы выбрать"

        // Нажатие на породу (открывает BottomSheet)
        binding.petBreed.setOnClickListener { openBottomSheet(EditPetDataBottomSheet.DataType.BREED) }
        binding.petGender.setOnClickListener { openGenderSelectionBottomSheet() }
        binding.petBirthday.setOnClickListener { openBottomSheet(EditPetDataBottomSheet.DataType.BIRTHDAY) }
        binding.petAdoptDay.setOnClickListener { openBottomSheet(EditPetDataBottomSheet.DataType.ADOPTION) }
    }



    private fun openBottomSheet(dataType: EditPetDataBottomSheet.DataType) {
        petId?.let { id ->
            val bottomSheet = EditPetDataBottomSheet(id, dataType) { updatedValue ->
                when (dataType) {
                    EditPetDataBottomSheet.DataType.BIRTHDAY -> {
                        binding.petBirthday.text = updatedValue
                        binding.petOld.text = preferenceManager.getPetDataById(id)?.petOld ?: "Неизвестно"
                    }
                    EditPetDataBottomSheet.DataType.ADOPTION -> {
                        binding.petAdoptDay.text = updatedValue
                    }
                    EditPetDataBottomSheet.DataType.BREED -> {
                        binding.petBreed.text = updatedValue
                    }
                }
            }
            bottomSheet.show(supportFragmentManager, "EditPetDataBottomSheet")
        }
    }



    private fun openGenderSelectionBottomSheet() {
        petId?.let { id ->
            val genderBottomSheet = GenderSelectionBottomSheetDialog(this, id, preferenceManager) { selectedGender ->
                binding.petGender.text = selectedGender
            }
            genderBottomSheet.show()
        }
    }



}
