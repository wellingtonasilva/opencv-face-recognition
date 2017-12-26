package br.com.wsilva.opencvfacerecognition.person.list.di

import br.com.wsilva.opencvfacerecognition.di.AppDatabaseModule
import br.com.wsilva.opencvfacerecognition.di.ForActivity
import br.com.wsilva.opencvfacerecognition.person.list.PersonListActivity
import br.com.wsilva.opencvfacerecognition.person.list.PersonListFragment
import br.com.wsilva.opencvfacerecognition.person.list.PersonListPresenter
import dagger.Component

/**
 * Created by wellingtonasilva on 24/12/17.
 */
@ForActivity
@Component(modules = arrayOf(PersonListModule::class, AppDatabaseModule::class))
interface PersonListComponent {
    fun inject(activity: PersonListActivity)
    fun inject(fragment: PersonListFragment)
    fun inject(presenter: PersonListPresenter)
}