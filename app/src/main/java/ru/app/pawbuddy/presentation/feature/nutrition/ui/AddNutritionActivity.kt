package ru.app.pawbuddy.presentation.feature.nutrition.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.R
import ru.app.pawbuddy.databinding.ActivityAddNutritionBinding
import ru.app.pawbuddy.domain.model.PetFood
import java.io.ByteArrayOutputStream

class AddNutritionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNutritionBinding
    private lateinit var preferenceManager: MyPreferenceManager
    private var selectedFoodImage: String? = null
    private var selectedTime: String? = null
    private var petId: String? = null
    private var selectedButton: MaterialButton? = null // Хранит выбранную кнопку

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNutritionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = MyPreferenceManager(this)
        petId = intent.getStringExtra("petId") ?: return

        binding.back.setOnClickListener { finish() }

        // Выбор фото
        binding.cardView2.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Выбор времени приёма пищи
            binding.breakfastBtn.setOnClickListener { selectTime(it as MaterialButton, "Завтрак") }
        binding.lunchBtn.setOnClickListener { selectTime(it as MaterialButton, "Обед") }
        binding.afternoonSnackBtn.setOnClickListener { selectTime(it as MaterialButton, "Полдник") }
        binding.dinnerBtn.setOnClickListener { selectTime(it as MaterialButton, "Ужин") }
        binding.otherBtn.setOnClickListener { selectTime(it as MaterialButton, "Другое") }
        binding.inAnyTimeBtn.setOnClickListener { selectTime(it as MaterialButton, "В любое время") }

        // Сохранение питания
        binding.saveBtn.setOnClickListener { saveFoodData() }
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                val compressedImage = getCompressedBitmap(uri)
                selectedFoodImage = bitmapToBase64(compressedImage)
                binding.petImage.setImageBitmap(compressedImage)
            } else {
                Log.e("AddNutritionActivity", "No media selected")
            }
        }

    private fun getCompressedBitmap(uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source)

        // Обрезаем изображение по центру, чтобы оно выглядело как `centerCrop`
        val width = bitmap.width
        val height = bitmap.height
        val size = width.coerceAtMost(height) // Берем минимальный размер (квадрат)

        val xOffset = (width - size) / 2
        val yOffset = (height - size) / 2

        val croppedBitmap = Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)

        // Уменьшаем размер после обрезки
        return Bitmap.createScaledBitmap(croppedBitmap, 512, 512, true)
    }


    private fun selectTime(button: MaterialButton, time: String) {
        selectedTime = time
        //Toast.makeText(this, "Выбрано: $time", Toast.LENGTH_SHORT).show()

        // Сбрасываем стиль у предыдущей кнопки
        selectedButton?.apply {
            setBackgroundColor(resources.getColor(android.R.color.transparent, theme))
            setTextColor(resources.getColor(R.color.textSecond, theme))
            setStrokeColorResource(R.color.textSecond)
        }

        // Применяем стиль к новой выбранной кнопке
        button.setBackgroundColor(resources.getColor(R.color.main, theme))
        button.setTextColor(resources.getColor(android.R.color.white, theme))
        button.setStrokeColorResource(R.color.main)

        // Запоминаем выбранную кнопку
        selectedButton = button
    }

    private fun saveFoodData() {
        val foodName = binding.petFoodName.text.toString().trim()
        val foodDescription = binding.petFoodDesc.text.toString().trim()

        if (foodName.isEmpty() || selectedTime == null) {
            Toast.makeText(this, "Введите название и выберите время", Toast.LENGTH_SHORT).show()
            return
        }

        val petFood = PetFood(
            foodImageBase64 = selectedFoodImage,
            foodName = foodName,
            foodDescription = foodDescription,
            foodTime = selectedTime!!
        )

        petId?.let { preferenceManager.addFoodToPet(it, petFood) }
        finish()
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
