package br.com.wsilva.opencvfacerecognition.person.update.di

import br.com.wsilva.opencvfacerecognition.model.repository.PersonRepository
import br.com.wsilva.opencvfacerecognition.person.update.PersonUpdateContract
import br.com.wsilva.opencvfacerecognition.person.update.PersonUpdateFragment
import br.com.wsilva.opencvfacerecognition.person.update.PersonUpdatePresenter
import dagger.Module
import dagger.Provides

/**
 * Created by wellingtonasilva on 26/12/17.
 */
@Module
class PersonUpdateModule(val view: PersonUpdateFragment)
{
    @Provides
    fun providesPersonUpdateView(): PersonUpdateContract.View {
        return view
    }

    @Provides
    fun providesPersonUpdatePresenter(view: PersonUpdateContract.View, repository: PersonRepository): PersonUpdateContract.Presenter
    {
        return PersonUpdatePresenter(view, repository)
    }
}