package br.com.wsilva.opencvfacerecognition.model.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import br.com.wsilva.opencvfacerecognition.model.dao.PersonDAO
import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity

/**
 * Created by wellingtonasilva on 24/12/17.
 */
@Database(entities = arrayOf(PersonEntity::class), version = 1)
abstract class AppDatabase: RoomDatabase()
{
    abstract fun getPersonDAO(): PersonDAO
}