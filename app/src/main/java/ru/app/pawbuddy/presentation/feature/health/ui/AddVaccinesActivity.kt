package ru.app.pawbuddy.presentation.feature.health.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.databinding.ActivityAddVaccinesBinding
import ru.app.pawbuddy.domain.model.PetVaccine
import java.text.SimpleDateFormat
import java.util.*

class AddVaccinesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddVaccinesBinding
    private var petId: String? = null
    private lateinit var preferenceManager: MyPreferenceManager
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVaccinesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = MyPreferenceManager(this)
        petId = intent.getStringExtra("petId") ?: return

        binding.back.setOnClickListener { finish() }

        // Открываем выбор даты прививки
        binding.petVaccineTime.setOnClickListener {
            showDatePicker()
        }

        // Сохранение прививки
        binding.saveBtn.setOnClickListener {
            saveVaccine()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Выберите дату прививки")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(selection))
            binding.petVaccineTime.text = selectedDate
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun saveVaccine() {
        val vaccineName = binding.petVaccineName.text.toString().trim()

        if (selectedDate.isNullOrEmpty() || vaccineName.isEmpty()) {
            Toast.makeText(this, "Введите название прививки и выберите дату", Toast.LENGTH_SHORT).show()
            return
        }

        val vaccine = PetVaccine(
            vaccineName = vaccineName,
            vaccineDate = selectedDate!!
        )

        petId?.let { preferenceManager.addVaccineToPet(it, vaccine) }

        Toast.makeText(this, "Прививка сохранена!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
