package br.com.wsilva.opencvfacerecognition.person.update.di

import br.com.wsilva.opencvfacerecognition.di.AppDatabaseModule
import br.com.wsilva.opencvfacerecognition.di.ForActivity
import br.com.wsilva.opencvfacerecognition.person.update.PersonUpdateFragment
import br.com.wsilva.opencvfacerecognition.person.update.PersonUpdatePresenter
import dagger.Component

/**
 * Created by wellingtonasilva on 26/12/17.
 */
@ForActivity
@Component(modules = arrayOf(PersonUpdateModule::class, AppDatabaseModule::class))
interface PersonUpdateComponent {
    fun inject(presenter: PersonUpdatePresenter)
    fun inject(fragment: PersonUpdateFragment)
}