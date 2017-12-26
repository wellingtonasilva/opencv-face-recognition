package br.com.wsilva.opencvfacerecognition.person.list

import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity
import br.com.wsilva.opencvfacerecognition.model.repository.PersonRepository
import br.com.wsilva.opencvfacerecognition.util.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * Created by wellingtonasilva on 24/12/17.
 */
class PersonListPresenter: PersonListContract.Presenter
{


    var view: PersonListContract.View
    var repository: PersonRepository

    @Inject
    constructor(view: PersonListContract.View, repository: PersonRepository) {
        this.view = view
        this.repository = repository
    }

    override fun destroy() {
    }

    override fun listAllPerson()
    {
        repository.listAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{list -> view.showPerson(list)}
    }

    override fun adicionarPerson() {
        view.showAdicionarPerson(Utils.getRandomUUID().toString())
    }

    override fun manterPerson(entity: PersonEntity) {
        view.showManterPerson(entity.uuid)
    }

    override fun confirmarExcluirPerson(entity: PersonEntity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun excluirPerson(entity: PersonEntity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}