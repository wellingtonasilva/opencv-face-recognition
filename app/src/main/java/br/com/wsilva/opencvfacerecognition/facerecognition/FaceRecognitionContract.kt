package br.com.wsilva.opencvfacerecognition.facerecognition

import br.com.wsilva.opencvfacerecognition.core.BasicPresenter
import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity

/**
 * Created by wellingtonasilva on 27/12/17.
 */
interface FaceRecognitionContract
{
    interface View {
        fun showPersonLocalizado(entity: PersonEntity)
    }

    interface Presenter: BasicPresenter {
        fun  localizarPerson(id: Long)
    }
}