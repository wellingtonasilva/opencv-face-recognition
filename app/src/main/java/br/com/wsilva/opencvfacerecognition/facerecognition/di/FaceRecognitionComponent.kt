package br.com.wsilva.opencvfacerecognition.facerecognition.di

import br.com.wsilva.opencvfacerecognition.di.AppDatabaseModule
import br.com.wsilva.opencvfacerecognition.di.ForActivity
import br.com.wsilva.opencvfacerecognition.facerecognition.FaceRecognitionActivity
import br.com.wsilva.opencvfacerecognition.facerecognition.FaceRecognitionPresenter
import dagger.Component

/**
 * Created by wellingtonasilva on 27/12/17.
 */
@ForActivity
@Component(modules = arrayOf(FaceRecognitionModule::class, AppDatabaseModule::class))
interface FaceRecognitionComponent {
    fun inject(activity: FaceRecognitionActivity)
    fun inject(presenter: FaceRecognitionPresenter)
}