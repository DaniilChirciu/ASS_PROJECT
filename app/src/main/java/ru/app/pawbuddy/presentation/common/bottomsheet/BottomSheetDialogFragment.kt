package ru.app.pawbuddy.presentation.common.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.databinding.FragmentBottomSheetDialogBinding
import java.text.SimpleDateFormat
import java.util.*

class EditPetDataBottomSheet(
    private val petId: String,
    private val dataType: DataType,
    private val onSave: (String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetDialogBinding
    private lateinit var preferenceManager: MyPreferenceManager

    enum class DataType {
        BIRTHDAY, ADOPTION, BREED
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = MyPreferenceManager(requireContext())

        // Устанавливаем заголовок
        binding.title.text = when (dataType) {
            DataType.BIRTHDAY -> "Введите день рождения"
            DataType.ADOPTION -> "Введите дату усыновления"
            DataType.BREED -> "Введите породу питомца" // 🛠 Заголовок для выбора породы
        }

        // Устанавливаем hint в EditText
        binding.editText.hint = when (dataType) {
            DataType.BIRTHDAY -> "Например, 01.01.2020"
            DataType.ADOPTION -> "Например, 15.05.2022"
            DataType.BREED -> "Например, Йоркширский терьер"
        }

        // Кнопка "Сохранить"
        binding.saveButton.setOnClickListener {
            val inputText = binding.editText.text.toString().trim()
            if (inputText.isEmpty()) {
                binding.editText.error = "Поле не может быть пустым"
                return@setOnClickListener
            }

            val petData = preferenceManager.getPetDataById(petId)
            if (petData != null) {
                val updatedPet = when (dataType) {
                    DataType.BIRTHDAY -> petData.copy(
                        petBirthday = inputText,
                        petOld = calculateAge(inputText)
                    )
                    DataType.ADOPTION -> petData.copy(
                        petAdoptDay = formatAdoptDate(inputText)
                    )
                    DataType.BREED -> petData.copy(
                        breedName = inputText // 🛠 Изменяем породу питомца
                    )
                }
                preferenceManager.savePetData(updatedPet)
                onSave(inputText) // Обновляем UI
                dismiss()
            }
        }
    }



    private fun isValidDate(dateStr: String): Boolean {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
            isLenient = false
        }
        return try {
            val date = dateFormat.parse(dateStr) ?: return false
            val today = Calendar.getInstance()
            val inputDate = Calendar.getInstance().apply { time = date }

            // Проверка, что введенная дата не в будущем
            inputDate.time.before(today.time)
        } catch (e: Exception) {
            false
        }
    }

    private fun calculateAge(birthday: String): String {
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return try {
            val birthDate = format.parse(birthday) ?: return "Неизвестно"
            val today = Calendar.getInstance()
            val birthCalendar = Calendar.getInstance().apply { time = birthDate }

            var years = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
            val months = today.get(Calendar.MONTH) - birthCalendar.get(Calendar.MONTH)

            if (months < 0) years -= 1

            when {
                years < 1 -> "Менее года"
                years == 1 -> "1 год"
                years in 2..4 -> "$years года"
                else -> "$years лет"
            }
        } catch (e: Exception) {
            "Неизвестно"
        }
    }

    private fun formatAdoptDate(dateStr: String): String {
        val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
        return try {
            val date = inputFormat.parse(dateStr) ?: return dateStr
            outputFormat.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }
}
