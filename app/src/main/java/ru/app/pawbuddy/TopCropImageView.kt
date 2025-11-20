package ru.app.pawbuddy

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class TopCropImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    init {
        // Обязательно устанавливаем ScaleType = MATRIX
        scaleType = ScaleType.MATRIX
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        adjustTopCrop()
        return super.setFrame(l, t, r, b)
    }

    private fun adjustTopCrop() {
        val drawable = drawable ?: return

        val imageWidth = drawable.intrinsicWidth
        val imageHeight = drawable.intrinsicHeight
        if (imageWidth == 0 || imageHeight == 0) return

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // Рассчитываем масштаб, аналогичный centerCrop, но смещаем вверх
        val scale: Float
        val dx = 0f
        val dy = 0f

        if (imageWidth * viewHeight > viewWidth * imageHeight) {
            scale = viewHeight / imageHeight
        } else {
            scale = viewWidth / imageWidth
        }

        val matrix = Matrix()
        matrix.setScale(scale, scale)
        // Поскольку хотим "topCrop", не делаем postTranslate вниз (dy=0)
        imageMatrix = matrix
    }
}
