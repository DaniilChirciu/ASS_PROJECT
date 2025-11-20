package ru.app.pawbuddy.presentation.feature.pet.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.databinding.ActivityAddPetActivityBinding
import ru.app.pawbuddy.domain.model.PetWalk
import java.text.SimpleDateFormat
import java.util.*

class AddPetActivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPetActivityBinding
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private lateinit var preferenceManager: MyPreferenceManager
    private var petId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPetActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = MyPreferenceManager(this)
        petId = intent.getStringExtra("petId") ?: return

        binding.back.setOnClickListener { finish() }

        // Открываем выбор даты
        binding.petWalkTime.setOnClickListener {
            showDatePicker()
        }

        // Сохраняем прогулку
        binding.saveBtn.setOnClickListener {
            saveWalkData()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Выберите дату прогулки")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(selection))
            showTimePicker()
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun showTimePicker() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H) // 24-часовой формат
            .setTitleText("Выберите время прогулки")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

            // Обновляем поле с датой и временем
            binding.petWalkTime.text = "$selectedDate в $selectedTime"
        }

        timePicker.show(supportFragmentManager, "TIME_PICKER")
    }

    private fun saveWalkData() {
        val walkDescription = binding.petWalkDesc.text.toString().trim()

        if (selectedDate == null || selectedTime == null) {
            Toast.makeText(this, "Выберите дату и время прогулки", Toast.LENGTH_SHORT).show()
            return
        }

        val petWalk = PetWalk(
            walkDate = selectedDate!!,
            walkTime = selectedTime!!,
            walkDescription = walkDescription.ifEmpty { "Нет комментария" }
        )

        petId?.let { preferenceManager.addWalkToPet(it, petWalk) }

        Toast.makeText(this, "Прогулка сохранена!", Toast.LENGTH_SHORT).show()
        Log.d("PetWalk", "Сохранено: ${petWalk.walkDate} в ${petWalk.walkTime} - ${petWalk.walkDescription}")

        finish()
    }
}
