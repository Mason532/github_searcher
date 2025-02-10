package com.example.myappgitmanager.data.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.myappgitmanager.data.interfaces.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

class BitmapImageLoader: ImageLoader<String, Bitmap?> {
    override suspend fun loadImage(input: String): Bitmap? {
        return try {
            withContext(Dispatchers.IO) {
                val inputStream = URL(input).openStream()
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: IOException) {
            null
        }
    }
}