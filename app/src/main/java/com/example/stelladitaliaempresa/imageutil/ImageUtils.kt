package com.example.stelladitaliaempresa.imageutil

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
import com.example.stelladitaliaempresa.R
import java.io.ByteArrayOutputStream

object ImageUtils {

    // Converte Bitmap para Base64
    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Mais leve
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Converte Base64 para Bitmap com segurança
    fun base64ToBitmap(base64Str: String?): Bitmap? {
        return try {
            if (base64Str.isNullOrEmpty()) return null
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Carrega imagem base64 em ImageView com fallback
    fun loadBase64IntoImageView(base64String: String?, imageView: ImageView) {
        val bitmap = base64ToBitmap(base64String)
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageResource(R.drawable.ic_sem_imagem)
        }
    }
}
