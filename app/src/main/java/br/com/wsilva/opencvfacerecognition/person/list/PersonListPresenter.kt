package br.com.wsilva.opencvfacerecognition.person.list

import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity
import br.com.wsilva.opencvfacerecognition.model.repository.PersonRepository
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

    override fun listAllPerson() {
        val list = ArrayList<PersonEntity>()
        list.add(PersonEntity("Joe Satriani", "+55 9999-9999", "joe@email.com", ""))
        list.add(PersonEntity("Steve Vai", "+55 9999-9999", "joe@email.com", ""))
        list.add(PersonEntity("John Petrucci", "+55 9999-9999", "joe@email.com", ""))
        list.add(PersonEntity("Ritchie Blackmore", "+55 9999-9999", "joe@email.com", ""))
        list.add(PersonEntity("Roger Waters", "+55 9999-9999", "joe@email.com", ""))
        view.showPerson(list)
    }

    override fun adicionarPerson() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun confirmarExcluirPerson(entity: PersonEntity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun excluirPerson(entity: PersonEntity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}