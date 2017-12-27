package br.com.wsilva.opencvfacerecognition.facerecognition

import android.os.Bundle
import android.view.SurfaceView
import br.com.wsilva.opencvfacerecognition.AppApplication
import br.com.wsilva.opencvfacerecognition.R
import br.com.wsilva.opencvfacerecognition.core.BasicActivity
import br.com.wsilva.opencvfacerecognition.facerecognition.di.DaggerFaceRecognitionComponent
import br.com.wsilva.opencvfacerecognition.facerecognition.di.FaceRecognitionModule
import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity
import br.com.wsilva.opencvfacerecognition.model.repository.PersonRepository
import org.opencv.android.*
import org.opencv.core.*
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

    lateinit var cameraBridgeViewBase: CameraBridgeViewBase


    val mBaseLoaderCallback = object : BaseLoaderCallback(this)
    {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    presenter.init()
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
        return presenter.cameraFrame(inputFrame)
    }

    override fun showPersonLocalizado(entity: PersonEntity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showCamera()
    {
        //Configuração para acesso a camera do disposito
        cameraBridgeViewBase = findViewById<JavaCameraView>(R.id.javaCameraView)
        cameraBridgeViewBase.visibility = SurfaceView.VISIBLE
        cameraBridgeViewBase.setCvCameraViewListener(this)
        cameraBridgeViewBase.enableView()
    }
}