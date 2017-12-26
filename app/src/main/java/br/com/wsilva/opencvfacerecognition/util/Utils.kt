package br.com.wsilva.opencvfacerecognition.util

import android.content.Context
import android.util.Log
import java.io.*

/**
 * Created by wellingtonasilva on 25/12/17.
 */
class Utils
{
    companion object
    {
        val TAG = "Utils"

        fun copyFileFromRAW(context: Context, origin: String, destiny: String,
                                    destinyDirectory: String): File
        {
            //Diretório interno da aplicação
            val filesDir = context.filesDir

            //Verifica se o diretório destino existe no sistema de arquivos
            //Caso não exista, cria.
            val fileDestinyDirectory = File(filesDir.absoluteFile.toString() + "/" + destinyDirectory)
            if (!fileDestinyDirectory.exists()) {
                fileDestinyDirectory.mkdirs()
            }

            //Carrega o arquivo a partir da pasta RAW
            val inputStream = context.getResources().openRawResource(context.getResources().getIdentifier(origin,
                    "raw", context.getPackageName()))

            val fileDestiny = File(fileDestinyDirectory, destiny)
            val outputStream = FileOutputStream(fileDestiny)

            inputStream.copyTo(outputStream, DEFAULT_BUFFER_SIZE)

            return fileDestiny
        }

        fun copyAllCascade(context: Context, directory: String)
        {
            val files = listOf<String>("haarcascade_eye_tree_eyeglasses",
                    "haarcascade_frontalface_alt", "haarcascade_frontalface_alt2","lbpcascade_frontalface")
            for (filename in files) {
                val file = File(context.filesDir.absoluteFile.toString() + "/" + directory
                        + "/" + filename + ".xml")
                if (!file.exists()) {
                    copyFileFromRAW(context, filename, filename + ".xml", directory)
                }
            }
        }
    }
}