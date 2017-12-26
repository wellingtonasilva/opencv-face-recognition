package br.com.wsilva.opencvfacerecognition.util

import android.content.Context

/**
 * Created by welli on 28/11/2016.
 */

class FaceRecognition(private val context: Context)
{
    external fun FindFeatures(width: Int, height: Int, yuv: ByteArray, rgba: IntArray)
    external fun FindFaces(imageName: String, FileName: String)
    external fun Find(imageName: String, FileName: String, Csv: String): Int
    external fun stringFromJNI(): String

    init {
        System.loadLibrary("face_recognition")
    }
}
