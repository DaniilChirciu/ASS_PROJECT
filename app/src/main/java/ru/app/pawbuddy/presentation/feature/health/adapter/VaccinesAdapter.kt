package ru.app.pawbuddy.presentation.feature.health.adapter

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.databinding.VaccineItemBinding
import ru.app.pawbuddy.domain.model.PetVaccine
import ru.app.pawbuddy.presentation.feature.health.ui.VaccinesActivity
import java.text.SimpleDateFormat
import java.util.*

class VaccinesAdapter(
    private val context: Context,
    private val preferenceManager: MyPreferenceManager,
    private val petId: String
) : ListAdapter<PetVaccine, VaccinesAdapter.VaccineViewHolder>(DiffCallback()) {

    class VaccineViewHolder(
        private val binding: VaccineItemBinding,
        private val context: Context,
        private val preferenceManager: MyPreferenceManager,
        private val petId: String
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(vaccine: PetVaccine) {
            binding.petVaccineName.text = vaccine.vaccineName

            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val vaccineDate: Date? = sdf.parse(vaccine.vaccineDate)
            val currentDate = Date()

            // Проверяем дату: если она в будущем, изменяем текст и выделяем цвет
            val isUpcoming = vaccineDate != null && vaccineDate.after(currentDate)
            binding.petVaccineTime.text = if (isUpcoming) {
                "Будет проставлена ${vaccine.vaccineDate}"
            } else {
                "Проставлена ${vaccine.vaccineDate}"
            }

            // Меняем прозрачность элемента: 1.0 для будущих, 0.8 для проставленных
            binding.root.alpha = if (isUpcoming) 1.0f else 0.7f

            // Долгое нажатие для удаления с вибрацией
            binding.root.setOnLongClickListener {
                vibrateDevice()
                showDeleteDialog(vaccine)
                true
            }
        }

        private fun showDeleteDialog(vaccine: PetVaccine) {
            MaterialAlertDialogBuilder(context)
                .setTitle("Удаление прививки")
                .setMessage("Вы действительно хотите удалить прививку ${vaccine.vaccineName}?")
                .setPositiveButton("Удалить") { _, _ ->
                    preferenceManager.removeVaccineFromPet(petId, vaccine)
                    (binding.root.context as? VaccinesActivity)?.loadVaccines() // Обновляем список
                }
                .setNegativeButton("Отмена", null)
                .show()
        }

        private fun vibrateDevice() {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccineViewHolder {
        val binding = VaccineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VaccineViewHolder(binding, context, preferenceManager, petId)
    }

    override fun onBindViewHolder(holder: VaccineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<PetVaccine>() {
        override fun areItemsTheSame(oldItem: PetVaccine, newItem: PetVaccine): Boolean {
            return oldItem.vaccineName == newItem.vaccineName && oldItem.vaccineDate == newItem.vaccineDate
        }

        override fun areContentsTheSame(oldItem: PetVaccine, newItem: PetVaccine): Boolean {
            return oldItem == newItem
        }
    }
}
