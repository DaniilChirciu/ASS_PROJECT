package ru.app.pawbuddy.presentation.feature.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.app.pawbuddy.presentation.util.base64ToBitmap
import ru.app.pawbuddy.databinding.ItemPetBinding
import ru.app.pawbuddy.domain.model.PetData

class MyPetsAdapter(
    private var petList: List<PetData>,
    private val context: Context,
    private val onDeleteClick: (PetData) -> Unit
) : RecyclerView.Adapter<MyPetsAdapter.PetsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPetBinding.inflate(inflater, parent, false)
        return PetsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PetsViewHolder, position: Int) {
        val pet = petList[position]
        holder.bind(pet)

        holder.binding.deleteIc.setOnClickListener {
            onDeleteClick(pet)
        }
    }

    override fun getItemCount(): Int = petList.size

    fun updateData(newList: List<PetData>) {
        petList = newList
        notifyDataSetChanged()
    }

    inner class PetsViewHolder(val binding: ItemPetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(petData: PetData) {
            binding.petName.text = petData.petName
            binding.petTypeAndBreeds.text = petData.breedName

            // Если есть Base64 -> Bitmap, здесь можно конвертировать:
            val bitmap = base64ToBitmap(petData.petImageBase64?:"")
            binding.petImage.setImageBitmap(bitmap)
        }
    }
}
