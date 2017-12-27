package br.com.wsilva.opencvfacerecognition.facerecognition

import android.content.Context
import android.util.Log
import br.com.wsilva.opencvfacerecognition.R
import br.com.wsilva.opencvfacerecognition.constants.AppConstants
import br.com.wsilva.opencvfacerecognition.model.repository.PersonRepository
import br.com.wsilva.opencvfacerecognition.util.FaceRecognition
import br.com.wsilva.opencvfacerecognition.util.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.*
import org.opencv.highgui.Highgui
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import javax.inject.Inject

/**
 * Created by wellingtonasilva on 27/12/17.
 */
class FaceRecognitionPresenter: FaceRecognitionContract.Presenter
{
    var context: Context
    var view: FaceRecognitionContract.View
    var repository: PersonRepository

    //Imagens já processadas
    lateinit var fileCSV: File

    //Imagem a ser utilizada como auxiliar
    lateinit var photoFilename: File

    //Classificador de Face
    lateinit var fileFaceCascade: File
    lateinit var faceCascade: CascadeClassifier

    //Classificador de Olhos
    lateinit var fileEyesCascade: File
    lateinit var eyesCascade: CascadeClassifier

    @Inject
    constructor(context: Context, view: FaceRecognitionContract.View, repository: PersonRepository)
    {
        this.view = view
        this.repository = repository
        this.context = context
    }

    override fun init()
    {
        fileCSV = File(context.filesDir.toString(), "csv.txt")
        //Imagem a ser utilizada como auxiliar
        photoFilename = File(context.filesDir.toString(), "temp.jpg")
        //Classificador de Face
        fileFaceCascade = File(context.filesDir.canonicalPath.toString() + "/" + AppConstants.CASCADE_DIRECTORY
                + "/" + context.resources.getString(R.string.app_haarcascade_frontalface_alt2).toString())
        //Classificador de Olhos
        fileEyesCascade = File(context.filesDir.canonicalPath.toString() + "/" + AppConstants.CASCADE_DIRECTORY
                + "/" + context.resources.getString(R.string.app_haarcascade_eye_tree_eyeglasses).toString())
        //Carregar XML
        faceCascade = CascadeClassifier(fileFaceCascade.canonicalPath.toString())
        eyesCascade = CascadeClassifier(fileEyesCascade.canonicalPath.toString())
        //Show camera
        view.showCamera()
    }

    override fun destroy() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun localizarPerson(id: Long)
    {
    }

    override fun cameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat
    {
        val mGray = inputFrame?.gray()
        val mRgba = inputFrame?.rgba()
        val faces = MatOfRect()

        if (faceCascade != null) {
            //Detecção de Face
            faceCascade.detectMultiScale(mGray, faces, 1.1, 2, Utils.CV_HAAR_SCALE_IMAGE,
                    Size(30.0, 30.0), Size(0.0, 0.0))
        }

        val facesArray = faces.toArray()
        for (i in facesArray.indices)
        {
            //Obter a foto somente da primeira face
            val olhos = facesArray[i]
            val olhosROI = mRgba?.submat(olhos)
            //Igualar o tamanho das imagens
            val face_resized = Mat()
            Imgproc.resize(olhosROI, face_resized, Size(200.0, 200.0), 1.0, 1.0, Imgproc.INTER_CUBIC)
            val dst = Mat()
            Imgproc.cvtColor(face_resized, dst, Imgproc.COLOR_BGR2RGB)
            if (!Highgui.imwrite(photoFilename.canonicalPath, dst)) {
                Log.e("XXX", "Erro ao salvar a  imagem no caminho: " + photoFilename.canonicalPath)
            }

            //Tenta localizar imagem na base de dados montada
            try
            {
                val faceRecognition = FaceRecognition(context)
                //Ler arquivo com imagens já processadas
                var idImagemLocalizada = -1
                if (fileCSV.exists() && fileCSV.length() > 0) {
                    idImagemLocalizada = faceRecognition.Find(photoFilename.canonicalPath,
                            fileFaceCascade.canonicalPath, fileCSV.canonicalPath)
                }

                if (idImagemLocalizada != -1)
                {
                    repository.get(idImagemLocalizada)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe{entity -> if (entity != null)
                            {
                                //Exibe retangulo no rosto encontrado
                                Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), Scalar(255.0, 0.0, 80.0))
                                //Exibe texto com o número do rosto encontrado
                                Core.putText(mRgba, "Face de " + entity.name, Point((facesArray[i].x - 10).toDouble(), (facesArray[i].y - 10).toDouble()),
                                        Core.FONT_HERSHEY_PLAIN, 1.5, Scalar(255.0, 0.0, 0.0, 255.0), 2)
                            }}
                }
            } catch (e: Exception) {
                Log.e("TAG", "onCameraFrame: " + e.message)
            }
        }
        return mRgba!!
    }
}