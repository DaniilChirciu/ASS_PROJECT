package ru.app.pawbuddy.presentation.feature.health.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.app.pawbuddy.presentation.common.ui.FullScreenImageDialog
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.databinding.ActivityPetInsuranceBinding
import ru.app.pawbuddy.databinding.BottomSheetInfoBinding
import java.io.ByteArrayOutputStream

class PetInsuranceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPetInsuranceBinding
    private var petId: String? = null
    private lateinit var preferenceManager: MyPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetInsuranceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = MyPreferenceManager(this)
        petId = intent.getStringExtra("petId") ?: return

        binding.back.setOnClickListener { finish() }

        // Обработчики нажатий на info-кнопки
        binding.infoPetInsurance.setOnClickListener {
            showInfoDialog("Страховка питомца", "Страховка питомца — это страховой полис, который обеспечивает финансовую защиту в случае болезни, травмы или других непредвиденных ситуаций. Владелец животного обращается в ветеринарную клинику, а расходы на услуги покрывает страховая компания.")
        }

        binding.infoPetPassport.setOnClickListener {
            showInfoDialog("Ветеринарный паспорт", "В нём должны стоять отметки о дегельминтизации, обработке от эктопаразитов, прививках.")
        }

        binding.infoPetCard.setOnClickListener {
            showInfoDialog("Щенячья карточка", "Щенячья карточка (метрика). В ней указывается порода, полная кличка щенка, пол, окрас, дата рождения, код и номер клейма, фамилия и адрес заводчика и владельца щенка, сведения о происхождении.")
        }

        // Обработчики нажатий на изображения для выбора фото
        binding.petInsurance.setOnClickListener { pickImage("insurance") }
        binding.petPassport.setOnClickListener { pickImage("passport") }
        binding.petCard.setOnClickListener { pickImage("card") }

        // Загрузка сохраненных изображений
        loadSavedImages()


        binding.card.setOnClickListener { showFullScreenDialog("insurance") }
        binding.card2.setOnClickListener { showFullScreenDialog("passport") }
        binding.card3.setOnClickListener { showFullScreenDialog("card") }

    }

    private fun showInfoDialog(title: String, description: String) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bindingSheet = BottomSheetInfoBinding.inflate(LayoutInflater.from(this))

        bindingSheet.infoTitle.text = title
        bindingSheet.infoDescription.text = description

        bottomSheetDialog.setContentView(bindingSheet.root)
        bottomSheetDialog.show()
    }

    private fun pickImage(type: String) {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        selectedImageType = type
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            val compressedImage = getCompressedBitmap(uri)
            val imageBase64 = bitmapToBase64(compressedImage)

            when (selectedImageType) {
                "insurance" -> {
                    binding.petInsurance.setImageBitmap(compressedImage)
                    petId?.let { preferenceManager.savePetInsurance(it, imageBase64) }
                }
                "passport" -> {
                    binding.petPassport.setImageBitmap(compressedImage)
                    petId?.let { preferenceManager.savePetPassport(it, imageBase64) }
                }
                "card" -> {
                    binding.petCard.setImageBitmap(compressedImage)
                    petId?.let { preferenceManager.savePetCard(it, imageBase64) }
                }
            }
        }
    }

    private fun getCompressedBitmap(uri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options()

        // Сначала просто считываем размеры, без загрузки в память
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        val maxWidth = 2000  // Максимальная ширина
        val maxHeight = 2000 // Максимальная высота
        var width = options.outWidth
        var height = options.outHeight

        var inSampleSize = 1
        while (width > maxWidth || height > maxHeight) {
            width /= 2
            height /= 2
            inSampleSize *= 2
        }

        // Теперь загружаем уменьшенное изображение
        options.inJustDecodeBounds = false
        options.inSampleSize = inSampleSize
        val finalInputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(finalInputStream, null, options)
        finalInputStream?.close()

        return bitmap ?: throw IllegalArgumentException("Не удалось загрузить изображение")
    }


    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)  // Сжимаем до 85%

        val byteArray = outputStream.toByteArray()

        // Проверяем размер (ограничение в 5MB)
        if (byteArray.size > 5 * 1024 * 1024) {
            throw IllegalArgumentException("Файл слишком большой. Выберите другое изображение.")
        }

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


    private fun loadSavedImages() {
        petId?.let {
            preferenceManager.getPetInsurance(it)?.let { image ->
                binding.petInsurance.setImageBitmap(base64ToBitmap(image))
            }
            preferenceManager.getPetPassport(it)?.let { image ->
                binding.petPassport.setImageBitmap(base64ToBitmap(image))
            }
            preferenceManager.getPetCard(it)?.let { image ->
                binding.petCard.setImageBitmap(base64ToBitmap(image))
            }
        }
    }

    private fun base64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showFullScreenDialog(type: String) {
        val imageBase64 = when (type) {
            "insurance" -> petId?.let { preferenceManager.getPetInsurance(it) }
            "passport" -> petId?.let { preferenceManager.getPetPassport(it) }
            "card" -> petId?.let { preferenceManager.getPetCard(it) }
            else -> null
        }

        if (!imageBase64.isNullOrEmpty()) {
            FullScreenImageDialog(this, imageBase64).show()
        }
    }



    private var selectedImageType: String? = null
}
