package br.com.wsilva.opencvfacerecognition.facerecognition

import br.com.wsilva.opencvfacerecognition.model.repository.PersonRepository
import javax.inject.Inject

/**
 * Created by wellingtonasilva on 27/12/17.
 */
class FaceRecognitionPresenter: FaceRecognitionContract.Presenter
{
    var view: FaceRecognitionContract.View
    var repository: PersonRepository

    @Inject
    constructor(view: FaceRecognitionContract.View, repository: PersonRepository) {
        this.view = view
        this.repository = repository
    }

    override fun destroy() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun localizarPerson(id: Long)
    {
    }
}