package ru.app.pawbuddy.presentation.common.ui

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import ru.app.pawbuddy.databinding.DialogFullScreenImageBinding

class FullScreenImageDialog(context: Context, private val imageBase64: String) : Dialog(context) {
    private lateinit var binding: DialogFullScreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Устанавливаем без заголовка
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Раздуваем макет
        binding = DialogFullScreenImageBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        // Устанавливаем изображение
        binding.fullScreenImage.setImageBitmap(base64ToBitmap(imageBase64))

        // Закрытие по кнопке
        binding.closeButton.setOnClickListener { dismiss() }

        // Устанавливаем полноэкранный режим
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setDimAmount(0.7f) // Затемнение фона
    }

    private fun base64ToBitmap(base64Str: String): android.graphics.Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
