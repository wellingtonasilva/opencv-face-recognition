package br.com.wsilva.opencvfacerecognition.cameracapture

import android.os.Bundle
import android.view.SurfaceView
import android.view.WindowManager
import br.com.wsilva.opencvfacerecognition.R
import br.com.wsilva.opencvfacerecognition.constants.AppConstants
import br.com.wsilva.opencvfacerecognition.core.BasicActivity
import org.opencv.android.*
import org.opencv.core.*
import org.opencv.core.Core.FONT_HERSHEY_PLAIN
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File

/**
 * Created by wellingtonasilva on 26/12/17.
 */
class CameraCaptureActivity: BasicActivity(), CameraBridgeViewBase.CvCameraViewListener2
{
    lateinit var faceCascade: CascadeClassifier
    lateinit var eyesCascade: CascadeClassifier
    lateinit var cameraBridgeViewBase: CameraBridgeViewBase
    //Id da Pessoa. Será utilizado para salvar a imagem no sistema de arquivos
    lateinit var id: String
    //Indica que o usuário clicou para capturar uma imagem
    var isFotoPendente: Boolean = false
    val CV_HAAR_SCALE_IMAGE = 2
    var mRgba: Mat? = null
    var mGray: Mat? = null

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
        setContentView(R.layout.lay_camera_capture_activity)
        //Permanecer com a tela sempre ativa
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
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

    fun init(savedInstanceState: Bundle?)
    {
        //Id. referente a Pessoa
        if (savedInstanceState == null) {
            val bundle = intent.extras
            id = bundle.getString(AppConstants.KEY_UUID)
        } else {
            id = savedInstanceState.getString(AppConstants.KEY_UUID)
        }

        //Configuração para acesso a camera do disposito
        cameraBridgeViewBase = findViewById<JavaCameraView>(R.id.javaCameraView)
        cameraBridgeViewBase.visibility = SurfaceView.VISIBLE
        cameraBridgeViewBase.setCvCameraViewListener(this)
    }

    override fun onCameraViewStarted(width: Int, height: Int)
    {
        mRgba = Mat()
        mGray = Mat()
    }

    override fun onCameraViewStopped()
    {
        mRgba?.release()
        mGray?.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat
    {
        mGray = inputFrame?.gray()
        mRgba = inputFrame?.rgba()

        /*
        val faces = MatOfRect()

        if (faceCascade != null) {
            //Detecção de Face
            faceCascade.detectMultiScale(mGray, faces, 1.1, 2, CV_HAAR_SCALE_IMAGE,
                    Size(30.0, 30.0), Size(0.0, 0.0))
        }
        */

        /*
        val facesArray = faces.toArray()
        for (i in facesArray.indices)
        {
            //Capturar foto
            if (isFotoPendente)
            {
                //Obter a foto somente da primeira face
                val olhos = facesArray[i]
                val olhosROI = mRgba?.submat(olhos)
                //Igualar o tamanho das imagens
                val face_resized = Mat()
                Imgproc.resize(olhosROI, face_resized, Size(200.0, 200.0), 1.0, 1.0, Imgproc.INTER_CUBIC)
                isFotoPendente = false
                //capturarFoto(face_resized, photoFoldername, photoFilename, true)
            }

            //Exibe retangulo no rosto encontrado
            Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), Scalar(255.0, 0.0, 80.0))
            //Core.putText(mRgba, "Face #" + i.toString(), Point((facesArray[i].x - 10).toDouble(), (facesArray[i].y - 10).toDouble()),
            //        FONT_HERSHEY_PLAIN, 1.5, Scalar(255.0, 0.0, 0.0, 255.0), 2)
        }
        */

        return mRgba!!
    }


    private fun iniciarCascade()
    {
        //Classificador de Face
        val fileFaceCascade = File(filesDir.absoluteFile.toString() + "/" + AppConstants.CASCADE_DIRECTORY
                + "/" + resources.getString(R.string.app_haarcascade_frontalface_alt2).toString())
        faceCascade = CascadeClassifier(fileFaceCascade.absolutePath.toString())

        //Classificador de Olhos
        val fileEyesCascade = File(filesDir.absoluteFile.toString() + "/" + AppConstants.CASCADE_DIRECTORY
                + "/" + resources.getString(R.string.app_haarcascade_eye_tree_eyeglasses).toString())
        eyesCascade = CascadeClassifier(fileEyesCascade.absolutePath.toString())
    }
}