package ru.app.pawbuddy.presentation.feature.pet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sinaseyfi.advancedcardview.AdvancedCardView
import ru.app.pawbuddy.R
import ru.app.pawbuddy.domain.model.PetBreed

class PetBreedAdapter(
    private val petList: List<PetBreed>,
    private val onItemClick: (PetBreed) -> Unit
) : RecyclerView.Adapter<PetBreedAdapter.PetBreedViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetBreedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pet_breeds_item, parent, false)
        return PetBreedViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetBreedViewHolder, position: Int) {
        val item = petList[position]
        holder.bind(item, position == selectedPosition)

        holder.itemView.setOnClickListener {
            val adapterPosition = holder.bindingAdapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION && selectedPosition != adapterPosition) {
                notifyItemChanged(selectedPosition)
                selectedPosition = adapterPosition
                notifyItemChanged(selectedPosition)
            }
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = petList.size

    inner class PetBreedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: AdvancedCardView = itemView.findViewById(R.id.cardView)
        private val petNameText: TextView = itemView.findViewById(R.id.pet_breeds)
        private val petImage: ImageView = itemView.findViewById(R.id.pet_image)

        fun bind(breed: PetBreed, isSelected: Boolean) {
            petNameText.text = breed.name

            Glide.with(itemView.context)
                .load(breed.imageUrl)

                .into(petImage)

            val context = itemView.context
            val mainColor = ContextCompat.getColor(context, R.color.main)
            val defaultColor = ContextCompat.getColor(context, R.color.white)

            cardView.stroke_Color = if (isSelected) mainColor else defaultColor
        }
    }
}
