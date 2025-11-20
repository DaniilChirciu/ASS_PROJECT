package ru.app.pawbuddy.presentation.feature.health.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sinaseyfi.advancedcardview.AdvancedCardView
import ru.app.pawbuddy.R

class WeightAdapter(
    private val sizeList: List<String>
) : RecyclerView.Adapter<WeightAdapter.WeightViewHolder>() {

    private var centerPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weight, parent, false)
        return WeightViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeightViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = sizeList[position]
        holder.bind(item, position == centerPosition)
    }

    override fun getItemCount(): Int = sizeList.size

    fun updateCenterPosition(pos: Int) {
        centerPosition = pos
        notifyDataSetChanged()
    }

    inner class WeightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val rootCard: AdvancedCardView = itemView.findViewById(R.id.weightRootCard)
        private val iconBackgroundCard: CardView = itemView.findViewById(R.id.iconBackgroundCard)
        private val weightIcon: ImageView = itemView.findViewById(R.id.weightIcon)
        private val weightText: TextView = itemView.findViewById(R.id.weightText)

        fun bind(sizeString: String, isCenter: Boolean) {
            weightText.text = sizeString

            val context = itemView.context
            val mainColor = ContextCompat.getColor(context, R.color.main)
            val textSecond = ContextCompat.getColor(context, R.color.textSecond)
            val gray = ContextCompat.getColor(context, R.color.gray)
            val white = ContextCompat.getColor(context, R.color.white)

            if (isCenter) {
                weightText.setTextColor(mainColor)
                weightIcon.imageTintList = ColorStateList.valueOf(mainColor)
                iconBackgroundCard.setCardBackgroundColor(white)
                rootCard.stroke_Color = mainColor
            } else {
                weightText.setTextColor(textSecond)
                weightIcon.imageTintList = ColorStateList.valueOf(textSecond)
                iconBackgroundCard.setCardBackgroundColor(gray)
                rootCard.stroke_Color = white
            }
        }
    }
}
