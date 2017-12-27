package br.com.wsilva.opencvfacerecognition.facerecognition

import br.com.wsilva.opencvfacerecognition.core.BasicPresenter
import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Mat

/**
 * Created by wellingtonasilva on 27/12/17.
 */
interface FaceRecognitionContract
{
    interface View {
        fun showPersonLocalizado(entity: PersonEntity)
        fun showCamera()
    }

    interface Presenter: BasicPresenter {
        fun localizarPerson(id: Long)
        fun cameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat
        fun init()
    }
}