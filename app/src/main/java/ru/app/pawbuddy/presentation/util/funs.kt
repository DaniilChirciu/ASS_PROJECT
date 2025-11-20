package ru.app.pawbuddy.presentation.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import ru.app.pawbuddy.R
import java.io.ByteArrayOutputStream



fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    // Сжимаем изображение в JPEG формате с качеством 80
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun base64ToBitmap(base64Str: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun AppCompatActivity.replaceFragment(fragment: Fragment){
    supportFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment).commit()
}

fun AppCompatActivity.replaceFragmentCustom(fragment: Fragment,layout:Int){
    supportFragmentManager.beginTransaction().replace(layout, fragment).commit()
}