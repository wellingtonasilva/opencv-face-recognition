package br.com.wsilva.opencvfacerecognition.person.update

import android.os.Bundle
import br.com.wsilva.opencvfacerecognition.R
import br.com.wsilva.opencvfacerecognition.core.BasicActivity
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.InstallCallbackInterface
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader

/**
 * Created by wellingtonasilva on 26/12/17.
 */
class PersonUpdateActivity: BasicActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lay_person_update_activity)
        //Configuração inicial
        init(savedInstanceState)
    }

    val mBaseLoaderCallback = object : BaseLoaderCallback(this)
    {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                }
                else -> super.onManagerConnected(status)
            }
        }

        override fun onPackageInstall(operation: Int, callback: InstallCallbackInterface) {
            super.onPackageInstall(operation, callback)
        }
    }

    fun init(savedInstanceState: Bundle?)
    {
        val fragmentManager = supportFragmentManager
        var fragment = fragmentManager.findFragmentByTag(PersonUpdateFragment.TAG)
        if (fragment == null) {
            fragment = PersonUpdateFragment.newInstance()
        }

        //Parametros
        if (savedInstanceState == null) {
            fragment.arguments = intent.extras
        } else {
            fragment.arguments = savedInstanceState
        }

        addFragmentToActivity(fragmentManager, fragment, R.id.frameLayout, PersonUpdateFragment.TAG)
    }

    override fun onResume() {
        super.onResume()
        //Inicializa OpenCV
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mBaseLoaderCallback)
    }
}