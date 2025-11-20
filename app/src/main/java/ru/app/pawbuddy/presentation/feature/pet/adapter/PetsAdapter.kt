package ru.app.pawbuddy.presentation.feature.pet.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.app.pawbuddy.presentation.common.ui.AboutActivity
import ru.app.pawbuddy.presentation.util.base64ToBitmap
import ru.app.pawbuddy.databinding.MyPetItemBinding
import ru.app.pawbuddy.domain.model.PetData

class PetsAdapter(
    private var petList: List<PetData>,
    private var context: Context,
    private val onItemClick: (PetData) -> Unit
) : RecyclerView.Adapter<PetsAdapter.PetsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        // Используем класс биндинга, который генерируется по имени файла `my_pet_item.xml`
        val binding = MyPetItemBinding.inflate(inflater, parent, false)
        return PetsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PetsViewHolder, position: Int) {
        val pet = petList[position]
        holder.bind(pet)
        // Клик по всей карточке (или binding.root)
        holder.itemView.setOnClickListener {
            onItemClick(pet)
        }

        // Исправлено: теперь корректно передаём petId
        holder.itemView.setOnClickListener {
            val intent = Intent(context, AboutActivity::class.java).apply {
                putExtra("petId", pet.petId) // Передаём ID питомца
            }
            Log.d("PetsAdapter", "Передаём petId: ${pet.petId}")
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = petList.size

    fun updateData(newList: List<PetData>) {
        petList = newList
        notifyDataSetChanged()
    }

    inner class PetsViewHolder(private val binding: MyPetItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(petData: PetData) {
            // Присваиваем значения
            binding.petName.text = petData.petName
            binding.petTypeAndBreeds.text = "${petData.breedName}"

            // Если есть Base64 -> Bitmap, здесь можно конвертировать:
             val bitmap = base64ToBitmap(petData.petImageBase64?:"")
             binding.petImage.setImageBitmap(bitmap)


        }
    }


}
