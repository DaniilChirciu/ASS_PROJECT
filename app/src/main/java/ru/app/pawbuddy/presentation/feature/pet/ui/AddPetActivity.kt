package ru.app.pawbuddy.presentation.feature.pet.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.app.pawbuddy.R
import ru.app.pawbuddy.databinding.ActivityAddPetBinding
import ru.app.pawbuddy.presentation.feature.pet.viewmodel.AddPetViewModel
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.presentation.util.bitmapToBase64
import ru.app.pawbuddy.domain.model.PetData

class AddPetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPetBinding
    private lateinit var navController: NavController
    private val addPetViewModel: AddPetViewModel by viewModels()

    // Карта заголовков и описаний для каждого фрагмента
    private val stepInfo = mapOf(
        R.id.breedFragment to Pair("Добавить питомца", "Выберите породу"),
        R.id.nameFragment to Pair("Добавить питомца", "Введите имя и фото"),
        R.id.sizeFragment to Pair("Добавить питомца", "Выберите размер"),
        R.id.weightFragment to Pair("Добавить питомца", "Укажите вес")

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.addPetsFragment) as NavHostFragment
        navController = navHostFragment.navController

        setContinueButtonState(false)

        // Следим за изменениями в ViewModel
        addPetViewModel.selectedBreedLiveData.observe(this) { updateContinueButton() }
        addPetViewModel.petNameLiveData.observe(this) { updateContinueButton() }
        addPetViewModel.petImageLiveData.observe(this) { updateContinueButton() }
        addPetViewModel.petSizeLiveData.observe(this) { updateContinueButton() }
        addPetViewModel.petWeightLiveData.observe(this) { updateContinueButton() }

        // Обновляем кнопку, заголовок и шаги при смене фрагмента
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateUI(destination)
        }

        // Назад по фрагментам
        binding.back.setOnClickListener {
            if (!navController.popBackStack()) {
                finish() // Закрываем активность, если это первый шаг
            }
        }

        binding.continueBtn.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.breedFragment -> navController.navigate(R.id.action_breedFragment_to_nameFragment)
                R.id.nameFragment -> navController.navigate(R.id.action_nameFragment_to_sizeFragment)
                R.id.sizeFragment -> navController.navigate(R.id.action_sizeFragment_to_weightFragment)
                R.id.weightFragment -> {
                    // Собираем данные из ViewModel:
                    val breed = addPetViewModel.selectedBreedLiveData.value?.name ?: ""
                    val name = addPetViewModel.petNameLiveData.value ?: ""
                    val size = addPetViewModel.petSizeLiveData.value ?: ""
                    val weight = addPetViewModel.petWeightLiveData.value ?: 0f

                    // Допустим, для изображения сохраняем его как Base64 (если оно установлено)
                    val petImageBase64 = addPetViewModel.petImageLiveData.value?.let { bitmapToBase64(it) }

                    // Генерируем уникальный id (например, через текущую метку времени)
                    val petId = System.currentTimeMillis().toString()

                    val petData = PetData(petId, breed, name, size, weight, petImageBase64)

                    // Сохраняем данные через MyPreferenceManager
                    val prefManager = MyPreferenceManager(this)
                    prefManager.savePetData(petData)

                    // Закрываем активность
                    finish()
                }
            }
        }



    }

    private fun updateUI(destination: NavDestination? = navController.currentDestination) {
        destination?.id?.let { fragmentId ->
            // Обновляем заголовок и описание
            stepInfo[fragmentId]?.let { (title, desc) ->
                binding.fragmentTitle.text = title
                binding.fragmentDesc.text = desc
            }

            // Обновляем шаги и прогресс
            val stepIndex = stepInfo.keys.indexOf(fragmentId) + 1
            val totalSteps = stepInfo.size
            binding.stepTv.text = "Шаг\n$stepIndex/$totalSteps"
            binding.progressSeek.progress = ((stepIndex.toFloat() / totalSteps) * 100).toInt()

            // Обновляем состояние кнопки "Продолжить"
            updateContinueButton(destination)
        }
    }

    private fun updateContinueButton(destination: NavDestination? = navController.currentDestination) {
        val isEnabled = destination?.id?.let { addPetViewModel.isContinueEnabledForStep(it) } ?: false
        setContinueButtonState(isEnabled)
    }

    private fun setContinueButtonState(isEnabled: Boolean) {
        binding.continueBtn.isEnabled = isEnabled
        val color = if (isEnabled) R.color.main else R.color.secondMain
        binding.continueBtn.setBackgroundColor(ContextCompat.getColor(this, color))
    }
}
