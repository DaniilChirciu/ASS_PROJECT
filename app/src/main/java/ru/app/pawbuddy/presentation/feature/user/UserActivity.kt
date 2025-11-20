package ru.app.pawbuddy.presentation.feature.user

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.app.pawbuddy.presentation.feature.main.adapter.MyPetsAdapter
import ru.app.pawbuddy.presentation.common.bottomsheet.EditUserNameBottomSheet
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.databinding.ActivityUserBinding
import ru.app.pawbuddy.domain.model.PetData
import ru.app.pawbuddy.presentation.util.base64ToBitmap
import ru.app.pawbuddy.presentation.util.bitmapToBase64

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var preferenceManager: MyPreferenceManager
    private lateinit var adapter: MyPetsAdapter
    private var petList: MutableList<PetData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = MyPreferenceManager(this)

        setupPetsRecyclerView()

        binding.back.setOnClickListener { finish() }

        // Загружаем сохраненные данные
        loadUserData()

        // Открытие галереи при нажатии на фото
        binding.userImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Открытие BottomSheet для редактирования имени
        binding.userName.setOnClickListener {
            openEditNameBottomSheet()
        }
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                val compressedImage = getCompressedBitmap(uri)

                // Сохранение фото в SharedPreferences
                val base64Image = bitmapToBase64(compressedImage)
                preferenceManager.putString("userPhoto", base64Image)

                // Обновление фото
                binding.userImage.setImageBitmap(compressedImage)
            } else {
                Log.e("UserActivity", "No media selected")
            }
        }

    private fun getCompressedBitmap(uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source)

        val width = bitmap.width
        val height = bitmap.height
        val size = width.coerceAtMost(height) // Берем минимальный размер (делаем квадрат)

        val xOffset = (width - size) / 2
        val yOffset = (height - size) / 2

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
    }


    private fun loadUserData() {
        // Загружаем фото профиля
        val base64Image = preferenceManager.getString("userPhoto")
        if (!base64Image.isNullOrEmpty()) {
            val bitmap = base64ToBitmap(base64Image)
            bitmap?.let { binding.userImage.setImageBitmap(it) }
        }

        // Загружаем имя пользователя
        val userName = preferenceManager.getString("userName") ?: "Введите имя"
        binding.userName.text = userName

        // Загружаем список питомцев
        petList = preferenceManager.getAllPetData().toMutableList()
        adapter.updateData(petList)


        updateEmptyState()
    }


    private fun setupPetsRecyclerView() {
        adapter = MyPetsAdapter(petList, this, onDeleteClick = { pet ->
            showDeleteConfirmationDialog(pet)
        })

        binding.petsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.petsRecyclerView.adapter = adapter
    }

    private fun showDeleteConfirmationDialog(pet: PetData) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Удаление питомца")
            .setMessage("Вы действительно хотите удалить питомца ${pet.petName}?")
            .setPositiveButton("Удалить") { _, _ ->
                deletePet(pet.petId)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deletePet(petId: String) {
        // Удаляем питомца
        preferenceManager.deletePetById(petId)

        // Обновляем список
        petList = preferenceManager.getAllPetData().toMutableList()
        adapter.updateData(petList)


        updateEmptyState()
    }


    private fun openEditNameBottomSheet() {
        val bottomSheet = EditUserNameBottomSheet { newName ->
            binding.userName.text = newName
            preferenceManager.putString("userName", newName) // Сохраняем в SharedPreferences
        }
        bottomSheet.show(supportFragmentManager, "EditUserNameBottomSheet")
    }

    private fun updateEmptyState() {
        if (petList.isEmpty()) {
            binding.emptyLayout.root.visibility = View.VISIBLE
            binding.petsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyLayout.root.visibility = View.GONE
            binding.petsRecyclerView.visibility = View.VISIBLE
        }
    }

}
