package br.com.wsilva.opencvfacerecognition.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by wellingtonasilva on 24/12/17.
 */
@Module
class AppModule(val application : Application)
{
    @Singleton
    @Provides
    fun provides() : Context {
        return application
    }
}