package br.com.wsilva.opencvfacerecognition.util

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.util.Log
import br.com.wsilva.opencvfacerecognition.R
import br.com.wsilva.opencvfacerecognition.constants.AppConstants
import okhttp3.internal.Util
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Size
import org.opencv.highgui.Highgui
import org.opencv.highgui.Highgui.CV_LOAD_IMAGE_GRAYSCALE
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.cvtColor
import org.opencv.objdetect.CascadeClassifier
import java.io.*
import java.util.*

/**
 * Created by wellingtonasilva on 25/12/17.
 */
class Utils
{
    companion object
    {
        val TAG = "Utils"
        val CV_HAAR_SCALE_IMAGE = 2
        val NOME_ARQUIVO_CSV = "csv.txt"

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

        fun getRandomUUID(): UUID {
            return UUID.randomUUID()
        }

        fun capturePhoto(context: Context, uuid: String, id: Long) : Boolean
        {
            val filename = context.filesDir.canonicalPath + "/JPEG_" + uuid
            val filename1 = filename + "_1.jpg"
            val filename2 = filename + "_2.jpg"
            val mGray = Highgui.imread(filename + ".jpg", CV_LOAD_IMAGE_GRAYSCALE)
            //val mGray = Mat()
            val faceCascade: CascadeClassifier
            val faces = MatOfRect()

            //Converter para Gray
            //Imgproc.cvtColor(mMat, mGray, Imgproc.COLOR_BGR2RGB)

            //Classificador de Face
            val fileFaceCascade = File(context.filesDir.absoluteFile.toString() + "/" + AppConstants.CASCADE_DIRECTORY
                    + "/" + context.resources.getString(R.string.app_haarcascade_frontalface_alt2).toString())
            faceCascade = CascadeClassifier(fileFaceCascade.absolutePath.toString())

            //Detecção de Face
            faceCascade?.detectMultiScale(mGray, faces, 1.1, 2, CV_HAAR_SCALE_IMAGE,
                    Size(30.0, 30.0), Size(0.0, 0.0))

            val facesArray = faces.toArray()
            for (i in facesArray.indices)
            {
                //Obter a foto somente da primeira face
                val olhos = facesArray[i]
                val olhosROI = mGray?.submat(olhos)
                //Igualar o tamanho das imagens
                val face_resized = Mat()
                Imgproc.resize(olhosROI, face_resized, Size(200.0, 200.0), 1.0, 1.0, Imgproc.INTER_CUBIC)

                //Criar imagems
                Highgui.imwrite(filename1, face_resized)
                Highgui.imwrite(filename2, face_resized)

                //Adicionar no arquivo CSV
                adicionarImagemNoCSV(context, NOME_ARQUIVO_CSV, context.filesDir.toString(), filename1, id)
                adicionarImagemNoCSV(context, NOME_ARQUIVO_CSV, context.filesDir.toString(), filename2, id)
                break
            }

            Highgui.imwrite(filename2, mGray)
            return true
        }

        @Throws(IOException::class)
        fun createImageFile(context: Context, filename: String): File
        {
            val imageFileName = "JPEG_" + filename
            val storageDir = context.filesDir

            return File.createTempFile(
                    imageFileName, /* prefix */
                    ".jpg", /* suffix */
                    storageDir          /* directory */
            )
        }

        fun createAlertDialog(context: Context, dialogMessage: String,
                              dialogTitle: Int, dialogOK: Int, dialogCancel: Int,
                              onPositiveButtonClickListener: DialogInterface.OnClickListener?,
                              onNegativeButtonClickListener: DialogInterface.OnClickListener?) {
            val resources = context.getResources()
            // Cria Builder
            val builder = AlertDialog.Builder(context)
            // Configura Message
            builder.setMessage(dialogMessage)
            builder.setTitle(resources.getText(dialogTitle))
            if (onPositiveButtonClickListener != null) {
                builder.setPositiveButton(resources.getText(dialogOK), onPositiveButtonClickListener)
            }
            if (onNegativeButtonClickListener != null) {
                builder.setNegativeButton(resources.getText(dialogCancel), onNegativeButtonClickListener)
            }
            // Cria Alerta
            val dialog = builder.create()
            dialog.show()
        }

        fun adicionarImagemNoCSV(context: Context, filenameCSV: String, pathName: String,
                                 filename: String, contatoId: Long): Boolean {
            try
            {
                //Arquivo para salvar o CSV
                val fileCSV = File(pathName, filenameCSV)
                //Verifica se o arquivo já existe para adicionar uma nova linha
                val newline = fileCSV.exists()
                //Abri arquivo para gravação
                val writer = BufferedWriter(FileWriter(fileCSV, true))
                if (newline) writer.newLine()
                writer.append(pathName + "/" + filename + ";" + contatoId.toString())
                writer.close()

                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }

        }
    }
}