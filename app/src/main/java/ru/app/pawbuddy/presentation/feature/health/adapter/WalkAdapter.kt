package ru.app.pawbuddy.presentation.feature.health.adapter

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.R
import ru.app.pawbuddy.databinding.WalkHeaderItemBinding
import ru.app.pawbuddy.databinding.WalkItemBinding
import ru.app.pawbuddy.domain.model.PetWalk
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

sealed class WalkListItem {
    data class Header(val title: String) : WalkListItem()
    data class Item(val petWalk: PetWalk, val petId: String) : WalkListItem()
}

class WalkAdapter(
    private var walkList: List<WalkListItem>,
    private val context: Context,
    private val preferenceManager: MyPreferenceManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (walkList[position]) {
            is WalkListItem.Header -> TYPE_HEADER
            is WalkListItem.Item -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = WalkHeaderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            TYPE_ITEM -> {
                val binding = WalkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = walkList[position]) {
            is WalkListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is WalkListItem.Item -> (holder as ItemViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = walkList.size

    fun updateData(newWalkList: List<PetWalk>, petId: String) {
        walkList = groupByDate(newWalkList, petId)
        notifyDataSetChanged()
    }

    private fun groupByDate(walks: List<PetWalk>, petId: String): List<WalkListItem> {
        val groupedItems = mutableListOf<WalkListItem>()
        val sortedWalks = walks.sortedByDescending { parseDate(it.walkDate) }

        val today = Calendar.getInstance()

        sortedWalks.groupBy { getDateDifference(parseDate(it.walkDate), today) }
            .forEach { (header, walkList) ->
                groupedItems.add(WalkListItem.Header(header))
                walkList.forEach { walk -> groupedItems.add(WalkListItem.Item(walk, petId)) }
            }

        return groupedItems
    }

    private fun parseDate(dateStr: String): Date {
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return format.parse(dateStr) ?: Date()
    }

    private fun getDateDifference(date: Date, today: Calendar): String {
        val walkCalendar = Calendar.getInstance().apply { time = date }
        val diff = (walkCalendar.timeInMillis - today.timeInMillis) / (1000 * 60 * 60 * 24)

        return when {
            diff == 0L -> "Сегодня"
            diff == -1L -> "Вчера"
            diff == -2L -> "Позавчера"
            diff < 0 -> "${-diff} ${getDayString(-diff)} назад"
            diff == 1L -> "Завтра"
            diff == 2L -> "Послезавтра"
            else -> "Через $diff ${getDayString(diff)}"
        }
    }

    private fun getDayString(days: Long): String {
        val lastDigit = (days % 10).toInt()
        val lastTwoDigits = (days % 100).toInt()

        return when {
            lastTwoDigits in 11..14 -> "дней"
            lastDigit == 1 -> "день"
            lastDigit in 2..4 -> "дня"
            else -> "дней"
        }
    }

    inner class HeaderViewHolder(private val binding: WalkHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: WalkListItem.Header) {
            binding.walkTime.text = header.title
        }
    }

    inner class ItemViewHolder(private val binding: WalkItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WalkListItem.Item) {
            val walk = item.petWalk
            binding.petWalkTime.text = "${walk.walkDate} в ${walk.walkTime}"
            binding.petWalkDesc.text = walk.walkDescription ?: "Нет комментария"

            binding.petWalkImage.setImageResource(getRandomImageForTime(walk.walkTime))

            // Долгое нажатие для удаления
            binding.root.setOnLongClickListener {
                vibrateDevice()
                showDeleteDialog(item.petId, walk)
                true
            }
        }

        private fun showDeleteDialog(petId: String, walk: PetWalk) {
            MaterialAlertDialogBuilder(context)
                .setTitle("Удаление прогулки")
                .setMessage("Вы действительно хотите удалить прогулку ${walk.walkDate} в ${walk.walkTime}?")
                .setPositiveButton("Удалить") { _, _ ->
                    preferenceManager.removeWalkFromPet(petId, walk)
                    updateData(preferenceManager.getPetDataById(petId)?.walkList ?: emptyList(), petId)
                }
                .setNegativeButton("Отмена", null)
                .show()
        }

        private fun vibrateDevice() {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            }
        }
    }

    private fun getRandomImageForTime(walkTime: String): Int {
        val hour = walkTime.substringBefore(":").toIntOrNull() ?: return R.drawable.morning_1

        return when (hour) {
            in 5..9 -> getRandomMorningImage()
            in 10..18 -> getRandomDayImage()
            in 19..22 -> getRandomEveningImage()
            else -> getRandomNightImage()
        }
    }

    private fun getRandomMorningImage(): Int {
        return when (Random.nextInt(1, 4)) {
            1 -> R.drawable.morning_1
            2 -> R.drawable.morning_2
            else -> R.drawable.morning_3
        }
    }

    private fun getRandomDayImage(): Int {
        return when (Random.nextInt(1, 4)) {
            1 -> R.drawable.day_1
            2 -> R.drawable.day_2
            else -> R.drawable.day_3
        }
    }

    private fun getRandomEveningImage(): Int {
        return when (Random.nextInt(1, 4)) {
            1 -> R.drawable.evening_1
            2 -> R.drawable.evening_2
            else -> R.drawable.evening_3
        }
    }

    private fun getRandomNightImage(): Int {
        return when (Random.nextInt(1, 4)) {
            1 -> R.drawable.night_1
            2 -> R.drawable.night_2
            else -> R.drawable.night_3
        }
    }
}
