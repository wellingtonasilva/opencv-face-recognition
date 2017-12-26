package br.com.wsilva.opencvfacerecognition.di

import android.app.Application
import dagger.Component
import javax.inject.Singleton

/**
 * Created by wellingtonasilva on 24/12/17.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, AppDatabaseModule::class))
interface AppComponent {
    fun inject(application: Application)
    var appDataModule : AppDatabaseModule
    var application : Application
}