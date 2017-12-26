package br.com.wsilva.opencvfacerecognition.person.update

import android.content.Context
import android.util.Log
import br.com.wsilva.opencvfacerecognition.AppApplication
import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity
import br.com.wsilva.opencvfacerecognition.model.repository.PersonRepository
import br.com.wsilva.opencvfacerecognition.util.Utils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by wellingtonasilva on 26/12/17.
 */
class PersonUpdatePresenter: PersonUpdateContract.Presenter
{
    var view: PersonUpdateContract.View
    var repository: PersonRepository

    @Inject
    constructor(view: PersonUpdateContract.View, repository: PersonRepository) {
        this.view = view
        this.repository = repository
    }

    override fun destroy() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun capturarFoto(id: String) {
        view.showCapturarFoto(id)
    }

    override fun loadPerson(uuid: String) {
        repository.getByUUID(uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{entity -> if (entity != null) view.showPerson(entity) else view.showPerson(PersonEntity())}
    }

    override fun salvarPerson(entity: PersonEntity)
    {
        if (entity.id.compareTo(0) == 0) {
            inserirPerson(entity)
        } else {
            atualizarPerson(entity)
        }

        criarImagemFace(AppApplication.appComponent.application, entity.uuid, entity.id)
    }

    override fun inserirPerson(entity: PersonEntity)
    {
        val observable: Observable<Long> = Observable.create<Long>{ emmiter -> emmiter.onNext(repository.insert(entity))}
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { id -> if (id > 0) view.showSalvarSucesso() else view.showSalvarFalha()}
    }

    override fun atualizarPerson(entity: PersonEntity)
    {
        val observable: Observable<Int> = Observable.create<Int>{ emmiter -> emmiter.onNext(repository.update(entity))}
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { id -> if (id > 0) view.showSalvarSucesso() else view.showSalvarFalha()}
    }

    override fun criarImagemFace(context: Context, uuid: String, id: Long)
    {
        val observable: Observable<Boolean> = Observable.create<Boolean>{ emmiter -> emmiter.onNext(Utils.capturePhoto(context, uuid, id))}
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { result -> if (result) Log.d("xxx", "Imagens foram criadas.")}
    }
}