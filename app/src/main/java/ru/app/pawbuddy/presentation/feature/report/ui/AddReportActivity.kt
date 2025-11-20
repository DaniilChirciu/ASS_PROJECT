package ru.app.pawbuddy.presentation.feature.report.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.databinding.ActivityAddReportBinding
import ru.app.pawbuddy.domain.model.PetReport
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddReportBinding
    private lateinit var preferenceManager: MyPreferenceManager
    private var petId: String? = null
    private var selectedImageBase64: String? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = MyPreferenceManager(this)
        petId = intent.getStringExtra("petId") ?: return

        binding.back.setOnClickListener { finish() }

        // Выбор изображения
        binding.plusCard.setOnClickListener { pickImage() }

        // Выбор даты и времени
        binding.petReportTime.setOnClickListener { showDatePicker() }

        // Сохранение отчета
        binding.saveBtn.setOnClickListener { saveReport() }
    }

    private fun pickImage() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            val croppedImage = getCroppedBitmap(uri)
            binding.petImage.setImageBitmap(croppedImage)
            selectedImageBase64 = bitmapToBase64(croppedImage)
        }
    }

    private fun getCroppedBitmap(uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source)

        // Центрируем и обрезаем по меньшему измерению
        val width = bitmap.width
        val height = bitmap.height
        val size = width.coerceAtMost(height) // Берем минимальный размер (квадрат)

        val xOffset = (width - size) / 2
        val yOffset = (height - size) / 2

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Выберите дату прибытия")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(selection))
            showTimePicker()
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun showTimePicker() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText("Выберите время прибытия")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

            // Обновляем поле с датой и временем
            binding.petReportTime.text = "$selectedDate в $selectedTime"
        }

        timePicker.show(supportFragmentManager, "TIME_PICKER")
    }

    private fun saveReport() {
        val reportDesc = binding.petReportDesc.text.toString().trim()
        val ownerEmail = binding.petUserEmail.text.toString().trim()

        if (selectedImageBase64 == null) {
            Toast.makeText(this, "Добавьте фото питомца", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDate == null || selectedTime == null) {
            Toast.makeText(this, "Выберите дату и время", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidEmail(ownerEmail)) {
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show()
            return
        }

        val petReport = PetReport(
            reportDate = "$selectedDate в $selectedTime",
            reportImageBase64 = selectedImageBase64!!,
            reportDescription = reportDesc,
            ownerEmail = ownerEmail
        )

        petId?.let { preferenceManager.addReportToPet(it, petReport) }

        // После сохранения отчёта отправляем письмо
        val imageUri = base64ToUri(selectedImageBase64!!)
        if (imageUri != null) {
            sendEmailWithImage(ownerEmail, "Отчёт о питомце", "📅 Прибыл: ${petReport.reportDate}\n📝 Комментарий:\n$reportDesc", imageUri)
        } else {
            Toast.makeText(this, "Ошибка с изображением", Toast.LENGTH_SHORT).show()
        }

        Toast.makeText(this, "Отчет сохранен и отправлен!", Toast.LENGTH_SHORT).show()
        finish()
    }


    private fun sendEmailWithImage(email: String, subject: String, message: String, imageUri: Uri?) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822" // Указываем тип данных для почты
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email)) // Почта получателя
            putExtra(Intent.EXTRA_SUBJECT, subject) // Тема письма
            putExtra(Intent.EXTRA_TEXT, message) // Текст письма
            imageUri?.let { putExtra(Intent.EXTRA_STREAM, it) } // Прикрепляем изображение
            setPackage("com.google.android.gm") // Указываем Gmail как приложение
        }

        try {
            startActivity(intent) // Запускаем Gmail
        } catch (e: Exception) {
            Toast.makeText(this, "Gmail не найден!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun base64ToUri(base64Str: String): Uri? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "pet_report", null)
            Uri.parse(path)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
