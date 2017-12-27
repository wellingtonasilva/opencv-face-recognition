package br.com.wsilva.opencvfacerecognition.facerecognition

import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import br.com.wsilva.opencvfacerecognition.AppApplication
import br.com.wsilva.opencvfacerecognition.R
import br.com.wsilva.opencvfacerecognition.constants.AppConstants
import br.com.wsilva.opencvfacerecognition.core.BasicActivity
import br.com.wsilva.opencvfacerecognition.facerecognition.di.DaggerFaceRecognitionComponent
import br.com.wsilva.opencvfacerecognition.facerecognition.di.FaceRecognitionModule
import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity
import br.com.wsilva.opencvfacerecognition.model.repository.PersonRepository
import br.com.wsilva.opencvfacerecognition.util.FaceRecognition
import br.com.wsilva.opencvfacerecognition.util.Utils.Companion.CV_HAAR_SCALE_IMAGE
import io.reactivex.android.schedulers.AndroidSchedulers
import org.opencv.android.*
import org.opencv.core.*
import org.opencv.core.Core.FONT_HERSHEY_PLAIN
import org.opencv.highgui.Highgui
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import javax.inject.Inject

/**
 * Created by wellingtonasilva on 27/12/17.
 */
class FaceRecognitionActivity : BasicActivity(), CameraBridgeViewBase.CvCameraViewListener2, FaceRecognitionContract.View
{

    @Inject
    lateinit var presenter: FaceRecognitionPresenter

    @Inject
    lateinit var repository: PersonRepository

    lateinit var faceCascade: CascadeClassifier
    lateinit var eyesCascade: CascadeClassifier
    lateinit var cameraBridgeViewBase: CameraBridgeViewBase
    //Imagens já processadas
    lateinit var fileCSV: File
    //Imagem a ser utilizada como auxiliar
    lateinit var photoFilename: File
    //Classificador de Face
    lateinit var fileFaceCascade: File
    //Classificador de Olhos
    lateinit var fileEyesCascade: File

    val mBaseLoaderCallback = object : BaseLoaderCallback(this)
    {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    cameraBridgeViewBase.enableView()
                    iniciarCascade()
                }
                else -> super.onManagerConnected(status)
            }
        }

        override fun onPackageInstall(operation: Int, callback: InstallCallbackInterface) {
            super.onPackageInstall(operation, callback)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lay_camera_face_recognition_activity)
        DaggerFaceRecognitionComponent.builder()
                .appDatabaseModule(AppApplication.appComponent.appDataModule)
                .faceRecognitionModule(FaceRecognitionModule(this))
                .build()
                .inject(this)
        //Configuração inicial
        init(savedInstanceState)
    }

    override fun onResume()
    {
        super.onResume()
        //Inicializa OpenCV
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mBaseLoaderCallback)
    }

    override fun onPause()
    {
        super.onPause()
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView()
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView()
        }
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
    }

    override fun onCameraViewStopped() {
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat
    {
        val mGray = inputFrame?.gray()
        val mRgba = inputFrame?.rgba()
        val faces = MatOfRect()

        if (faceCascade != null) {
            //Detecção de Face
            faceCascade.detectMultiScale(mGray, faces, 1.1, 2, CV_HAAR_SCALE_IMAGE,
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
                val faceRecognition = FaceRecognition(applicationContext)
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
                                        FONT_HERSHEY_PLAIN, 1.5, Scalar(255.0, 0.0, 0.0, 255.0), 2)
                            }}
                }
            } catch (e: Exception) {
                Log.e("TAG", "onCameraFrame: " + e.message)
            }
        }

        return mRgba!!
    }

    override fun showPersonLocalizado(entity: PersonEntity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun init(savedInstanceState: Bundle?)
    {
        fileCSV = File(filesDir.toString(), "csv.txt")
        //Imagem a ser utilizada como auxiliar
        photoFilename = File(filesDir.toString(), "temp.jpg")
        //Classificador de Face
        fileFaceCascade = File(filesDir.absoluteFile.toString() + "/" + AppConstants.CASCADE_DIRECTORY
                + "/" + resources.getString(R.string.app_haarcascade_frontalface_alt2).toString())
        //Classificador de Olhos
        fileEyesCascade = File(filesDir.absoluteFile.toString() + "/" + AppConstants.CASCADE_DIRECTORY
                + "/" + resources.getString(R.string.app_haarcascade_eye_tree_eyeglasses).toString())

        //Configuração para acesso a camera do disposito
        cameraBridgeViewBase = findViewById<JavaCameraView>(R.id.javaCameraView)
        cameraBridgeViewBase.visibility = SurfaceView.VISIBLE
        cameraBridgeViewBase.setCvCameraViewListener(this)
    }

    private fun iniciarCascade()
    {
        faceCascade = CascadeClassifier(fileFaceCascade.absolutePath.toString())
        eyesCascade = CascadeClassifier(fileEyesCascade.absolutePath.toString())
    }
}