package br.com.wsilva.opencvfacerecognition.di

import android.app.Application
import android.arch.persistence.room.Room
import br.com.wsilva.opencvfacerecognition.model.dao.PersonDAO
import br.com.wsilva.opencvfacerecognition.model.db.AppDatabase
import br.com.wsilva.opencvfacerecognition.model.repository.PersonRepository
import dagger.Module
import dagger.Provides
import javax.inject.Inject

/**
 * Created by wellingtonasilva on 24/12/17.
 */
@Module
class AppDatabaseModule
{
    companion object {
        @JvmStatic lateinit var appDatabase : AppDatabase
    }

    var application: Application

    @Inject
    constructor(application: Application) {
        this.application = application
        appDatabase = Room.databaseBuilder(application,
                AppDatabase::class.java, "dabase.db").build()
    }

    @Provides
    fun application() : Application {
        return application
    }

    @Provides
    fun providesAppDatabase(application: Application) : AppDatabase {
        return appDatabase
    }

    @Provides
    fun providesPersonDAO(appDatabase: AppDatabase) : PersonDAO {
        return appDatabase.getPersonDAO()
    }

    @Provides
    fun providesPersonRepository(personDAO: PersonDAO): PersonRepository {
        return PersonRepository(personDAO)
    }
}