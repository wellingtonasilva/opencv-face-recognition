package br.com.wsilva.opencvfacerecognition

import android.app.Application
import android.util.Log
import br.com.wsilva.opencvfacerecognition.constants.AppConstants
import br.com.wsilva.opencvfacerecognition.di.AppComponent
import br.com.wsilva.opencvfacerecognition.di.AppDatabaseModule
import br.com.wsilva.opencvfacerecognition.di.AppModule
import br.com.wsilva.opencvfacerecognition.di.DaggerAppComponent
import br.com.wsilva.opencvfacerecognition.util.FaceRecognition
import br.com.wsilva.opencvfacerecognition.util.Utils

/**
 * Created by wellingtonasilva on 24/12/17.
 */
class AppApplication: Application()
{
    companion object {
        @JvmStatic lateinit var appComponent : AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .appDatabaseModule(AppDatabaseModule(this))
                .build()
        appComponent.inject(this)

        //Carrega todos os arquivos necessários para serem utilizados pelo OpenCV (Cascade)
        Utils.copyAllCascade(this, AppConstants.CASCADE_DIRECTORY)
    }
}