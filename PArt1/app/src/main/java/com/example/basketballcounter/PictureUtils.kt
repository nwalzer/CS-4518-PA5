package com.example.basketballcounter

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point
import android.media.ExifInterface
import android.util.Log


fun getScaledBitmap(path: String, activity: Activity): Bitmap {
    val size = Point()
    activity.windowManager.defaultDisplay.getSize(size)
    return getScaledBitmap(path, size.x, size.y)
}

fun getScaledBitmap(path: String, destWidth: Int, destHeight:Int): Bitmap{
    var options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)

    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()

    var inSampleSize = 1
    if(srcHeight > destHeight || srcWidth > destWidth){
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth

        val sampleScale = if(heightScale > widthScale){
            heightScale
        } else {
            widthScale
        }
        inSampleSize = Math.round(sampleScale)
    }

    options = BitmapFactory.Options()
    options.inSampleSize = inSampleSize

    var decodedBM = BitmapFactory.decodeFile(path, options)

    val exif = ExifInterface(path)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
    var rotationAngle = 0.0
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotationAngle = 90.0
        ExifInterface.ORIENTATION_ROTATE_180 -> rotationAngle = 180.0
        ExifInterface.ORIENTATION_ROTATE_270 -> rotationAngle = 270.0
    }

    val matrix = Matrix()

    matrix.postRotate(rotationAngle.toFloat())

    decodedBM = Bitmap.createBitmap(
        decodedBM,
        0,
        0,
        decodedBM.width,
        decodedBM.height,
        matrix,
        true
    )

    return decodedBM
}