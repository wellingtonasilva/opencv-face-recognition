package br.com.wsilva.opencvfacerecognition.person.list

import br.com.wsilva.opencvfacerecognition.core.BasicPresenter
import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity

/**
 * Created by wellingtonasilva on 24/12/17.
 */
interface PersonListContract
{
    interface View {
        fun showPerson(list: List<PersonEntity>)
        fun showAdicionarPerson(uuid: String)
        fun showManterPerson(uuid: String)
        fun showConfirmarExcluir(entity: PersonEntity)
        fun showExcluirSucesso()
        fun showExcluirFalha()
        fun showReconhecimentoFacial()
    }

    interface Presenter: BasicPresenter {
        fun listAllPerson()
        fun adicionarPerson()
        fun confirmarExcluirPerson(entity: PersonEntity)
        fun excluirPerson(entity: PersonEntity)
        fun manterPerson(entity: PersonEntity)
    }
}