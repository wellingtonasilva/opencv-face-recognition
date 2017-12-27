package br.com.wsilva.opencvfacerecognition.model.repository

import br.com.wsilva.opencvfacerecognition.core.BasicRepository
import br.com.wsilva.opencvfacerecognition.model.dao.PersonDAO
import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity
import io.reactivex.Flowable
import javax.inject.Inject

/**
 * Created by wellingtonasilva on 24/12/17.
 */
class PersonRepository: BasicRepository<PersonEntity>
{
    var dao: PersonDAO

    @Inject
    constructor(dao: PersonDAO) {
        this.dao = dao
    }

    override fun listAll(): Flowable<List<PersonEntity>> {
        return dao.listAll()
    }

    override fun get(id: Int): Flowable<PersonEntity> {
        return dao.get(id)
    }

    fun getByUUID(uuid: String): Flowable<PersonEntity> {
        return dao.getByUUID(uuid)
    }

    override fun delete(entity: PersonEntity): Int {
        return dao.delete(entity)
    }

    override fun insert(entity: PersonEntity): Long {
        return dao.insert(entity)
    }

    override fun update(entity: PersonEntity): Int {
        return dao.update(entity)
    }
}