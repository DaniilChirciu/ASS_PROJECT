package ru.app.pawbuddy.presentation.feature.report.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.databinding.ReportItemBinding
import ru.app.pawbuddy.domain.model.PetReport
import ru.app.pawbuddy.presentation.feature.report.ui.ReportActivity

class ReportAdapter(
    private val context: Context,
    private val preferenceManager: MyPreferenceManager,
    private val petId: String
) : ListAdapter<PetReport, ReportAdapter.ReportViewHolder>(DiffCallback()) {

    class ReportViewHolder(
        private val binding: ReportItemBinding,
        private val context: Context,
        private val preferenceManager: MyPreferenceManager,
        private val petId: String
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(report: PetReport) {
            // Загружаем данные питомца
            val petData = preferenceManager.getPetDataById(petId)

            binding.petName.text = petData?.petName ?: "Имя неизвестно"
            binding.petBreed.text = petData?.breedName ?: "Порода неизвестна"

            binding.reportTime.text = "Прибыл ${report.reportDate}"
            binding.reportDesc.text = report.reportDescription
            binding.reportUserEmail.text = "Отправлено на ${report.ownerEmail}"

            // Декодируем изображение из base64
            val bitmap = base64ToBitmap(report.reportImageBase64)
            if (bitmap != null) {
                binding.reportImage.setImageBitmap(bitmap)
            }

            // Долгое нажатие для удаления отчета
            binding.root.setOnLongClickListener {
                vibrateDevice()
                showDeleteDialog(report)
                true
            }
        }

        private fun showDeleteDialog(report: PetReport) {
            MaterialAlertDialogBuilder(context)
                .setTitle("Удаление отчета")
                .setMessage("Вы действительно хотите удалить этот отчет?")
                .setPositiveButton("Удалить") { _, _ ->
                    preferenceManager.removeReportFromPet(petId, report)
                    (binding.root.context as? ReportActivity)?.loadReports()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ReportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding, context, preferenceManager, petId)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<PetReport>() {
        override fun areItemsTheSame(oldItem: PetReport, newItem: PetReport): Boolean {
            return oldItem.reportDate == newItem.reportDate && oldItem.ownerEmail == newItem.ownerEmail
        }

        override fun areContentsTheSame(oldItem: PetReport, newItem: PetReport): Boolean {
            return oldItem == newItem
        }
    }
}
