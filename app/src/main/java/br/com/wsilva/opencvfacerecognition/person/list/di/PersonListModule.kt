package br.com.wsilva.opencvfacerecognition.person.list.di

import br.com.wsilva.opencvfacerecognition.model.repository.PersonRepository
import br.com.wsilva.opencvfacerecognition.person.list.PersonListContract
import br.com.wsilva.opencvfacerecognition.person.list.PersonListFragment
import br.com.wsilva.opencvfacerecognition.person.list.PersonListPresenter
import dagger.Module
import dagger.Provides

/**
 * Created by wellingtonasilva on 24/12/17.
 */
@Module
class PersonListModule(val view: PersonListFragment)
{
    @Provides
    fun providesPersonView(): PersonListContract.View {
        return view
    }

    @Provides
    fun providesPersonPresenter(view: PersonListContract.View, repository: PersonRepository): PersonListPresenter {
        return PersonListPresenter(view, repository)
    }
}
