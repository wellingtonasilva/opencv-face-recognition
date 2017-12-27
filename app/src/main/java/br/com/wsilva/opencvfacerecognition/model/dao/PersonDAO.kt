package br.com.wsilva.opencvfacerecognition.model.dao

import android.arch.persistence.room.*
import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity
import io.reactivex.Flowable

/**
 * Created by wellingtonasilva on 24/12/17.
 */
@Dao
interface PersonDAO
{
    @Query("SELECT * FROM person")
    fun listAll() : Flowable<List<PersonEntity>>

    @Query("SELECT * FROM person WHERE pers_id = :id")
    fun get(id: Int) : Flowable<PersonEntity>

    @Query("SELECT * FROM person WHERE pers_uuid = :uuid")
    fun getByUUID(uuid: String) : Flowable<PersonEntity>

    @Delete
    fun delete(entity : PersonEntity) : Int

    @Insert
    fun insert(entity : PersonEntity) : Long

    @Update
    fun update(entity: PersonEntity) : Int
}