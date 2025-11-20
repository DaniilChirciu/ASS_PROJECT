package ru.app.pawbuddy.presentation.feature.nutrition.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.databinding.NutritionHeaderItemBinding
import ru.app.pawbuddy.databinding.NutritionItemBinding
import ru.app.pawbuddy.domain.model.PetFood

sealed class NutritionListItem {
    data class Header(val title: String) : NutritionListItem()
    data class Item(val petFood: PetFood, val petId: String) : NutritionListItem()
}

class NutritionAdapter(
    private var foodList: List<NutritionListItem>,
    private val context: Context,
    private val preferenceManager: MyPreferenceManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (foodList[position]) {
            is NutritionListItem.Header -> TYPE_HEADER
            is NutritionListItem.Item -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = NutritionHeaderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            TYPE_ITEM -> {
                val binding = NutritionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = foodList[position]) {
            is NutritionListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is NutritionListItem.Item -> (holder as ItemViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = foodList.size

    fun updateData(newFoodList: List<PetFood>, petId: String) {
        foodList = groupByMealTime(newFoodList, petId)
        notifyDataSetChanged()
    }

    private fun groupByMealTime(foodList: List<PetFood>, petId: String): List<NutritionListItem> {
        val groupedItems = mutableListOf<NutritionListItem>()

        // Фиксированный порядок категорий
        val mealOrder = listOf("Завтрак", "Обед", "Полдник", "Ужин", "В любое время", "Другое")

        val sortedFoodMap = foodList.groupBy { it.foodTime }

        mealOrder.forEach { mealTime ->
            if (sortedFoodMap.containsKey(mealTime)) {
                groupedItems.add(NutritionListItem.Header(mealTime))
                sortedFoodMap[mealTime]?.forEach { food ->
                    groupedItems.add(NutritionListItem.Item(food, petId))
                }
            }
        }

        return groupedItems
    }

    inner class HeaderViewHolder(private val binding: NutritionHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: NutritionListItem.Header) {
            binding.mealTimeTitle.text = header.title
        }
    }

    inner class ItemViewHolder(private val binding: NutritionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NutritionListItem.Item) {
            val food = item.petFood
            binding.petFoodTitle.text = food.foodName
            binding.petFoodDesc.text = if (food.foodDescription.isNullOrEmpty()) {
                "Нет комментария"
            } else {
                food.foodDescription
            }

            if (!food.foodImageBase64.isNullOrEmpty()) {
                val bitmap = base64ToBitmap(food.foodImageBase64)
                binding.petFoodImage.setImageBitmap(bitmap)
            }

            // Долгое нажатие с вибрацией и диалогом
            binding.root.setOnLongClickListener {
                vibrateDevice()
                showDeleteDialog(item.petId, food)
                true
            }
        }

        private fun showDeleteDialog(petId: String, food: PetFood) {
            MaterialAlertDialogBuilder(context)
                .setTitle("Удаление питания")
                .setMessage("Вы действительно хотите удалить ${food.foodTime} - ${food.foodName}?")
                .setPositiveButton("Удалить") { _, _ ->
                    preferenceManager.removeFoodFromPet(petId, food)
                    updateData(preferenceManager.getPetDataById(petId)?.foodList ?: emptyList(), petId)
                }
                .setNegativeButton("Отмена", null)
                .show()
        }

        private fun vibrateDevice() {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                )
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
}
