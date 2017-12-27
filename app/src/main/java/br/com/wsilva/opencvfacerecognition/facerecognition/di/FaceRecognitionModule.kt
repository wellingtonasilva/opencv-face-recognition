package br.com.wsilva.opencvfacerecognition.facerecognition.di

import br.com.wsilva.opencvfacerecognition.facerecognition.FaceRecognitionActivity
import br.com.wsilva.opencvfacerecognition.facerecognition.FaceRecognitionContract
import br.com.wsilva.opencvfacerecognition.facerecognition.FaceRecognitionPresenter
import br.com.wsilva.opencvfacerecognition.model.repository.PersonRepository
import dagger.Module
import dagger.Provides

/**
 * Created by wellingtonasilva on 27/12/17.
 */
@Module
class FaceRecognitionModule(val activity: FaceRecognitionActivity)
{
    @Provides
    fun providesFaceRecognitionView(): FaceRecognitionContract.View {
        return activity
    }

    @Provides
    fun providesFaceRecognitionPresenter(view: FaceRecognitionContract.View, repository: PersonRepository): FaceRecognitionPresenter {
        return FaceRecognitionPresenter(view, repository)
    }
}