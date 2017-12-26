package br.com.wsilva.opencvfacerecognition.person.update

import android.content.Context
import br.com.wsilva.opencvfacerecognition.core.BasicPresenter
import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity

/**
 * Created by wellingtonasilva on 26/12/17.
 */
interface PersonUpdateContract {

    interface View {
        fun showCapturarFoto(uuid: String)
        fun showPerson(entity: PersonEntity)
        fun showSalvarSucesso()
        fun showSalvarFalha()
        fun close()
    }

    interface Presenter: BasicPresenter {
        fun capturarFoto(uuid: String)
        fun loadPerson(uuid: String)
        fun salvarPerson(entity: PersonEntity)
        fun inserirPerson(entity: PersonEntity)
        fun atualizarPerson(entity: PersonEntity)
        fun criarImagemFace(context: Context, uuid: String, id: Long)
    }
}