package br.com.wsilva.opencvfacerecognition.person.update

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.Button
import br.com.wsilva.opencvfacerecognition.AppApplication
import br.com.wsilva.opencvfacerecognition.R
import br.com.wsilva.opencvfacerecognition.constants.AppConstants
import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity
import br.com.wsilva.opencvfacerecognition.model.repository.PersonRepository
import br.com.wsilva.opencvfacerecognition.person.update.di.DaggerPersonUpdateComponent
import br.com.wsilva.opencvfacerecognition.person.update.di.PersonUpdateModule
import br.com.wsilva.opencvfacerecognition.util.Utils
import kotlinx.android.synthetic.main.lay_person_update_fragment.*
import java.io.File
import javax.inject.Inject

/**
 * Created by wellingtonasilva on 26/12/17.
 */
class PersonUpdateFragment: Fragment(), PersonUpdateContract.View
{
    @Inject
    lateinit var presenter: PersonUpdatePresenter

    @Inject
    lateinit var repository: PersonRepository

    var uuid: String = ""
    var id: Long = 0
    var filename: String = ""
    val REQUEST_TAKE_PHOTO = 1
    val MY_CAMERA_REQUEST_CODE = 100

    companion object {
        val TAG: String = "PersonUpdateFragment"
        fun newInstance(): PersonUpdateFragment {
            return PersonUpdateFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerPersonUpdateComponent.builder()
                .appDatabaseModule(AppApplication.appComponent.appDataModule)
                .personUpdateModule(PersonUpdateModule(this))
                .build()
                .inject(this)

        //Id. referente a Pessoa
        if (savedInstanceState == null) {
            val bundle = activity.intent.extras
            uuid = bundle.getString(AppConstants.KEY_UUID)
        } else {
            uuid = savedInstanceState.getString(AppConstants.KEY_UUID)
            filename = savedInstanceState.getString(AppConstants.KEY_FILENAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater?.inflate(R.layout.lay_person_update_fragment, container, false)
        view?.findViewById<FloatingActionButton>(R.id.fab)?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                presenter.capturarFoto(uuid)
            } else {
                verificarPermissaoAcessoCamera()
            }
        }
        view?.findViewById<Button>(R.id.btnSalvar)?.setOnClickListener {
            var entity = PersonEntity()
            entity.id = id
            entity.address = edtAddress.text.toString()
            entity.email = edtEmail.text.toString()
            entity.phone = edtPhone.text.toString()
            entity.name = edtName.text.toString()
            entity.photoFilename1 = filename
            entity.uuid = uuid
            presenter.salvarPerson(entity)
        }
        //Toolbar
        (activity as AppCompatActivity).setSupportActionBar(view?.findViewById<Toolbar>(R.id.toolbar))
        (activity as AppCompatActivity).setTitle("Person")

        return view
    }

    override fun onResume()
    {
        super.onResume()
        //Carrega dados da Pessoa
        presenter.loadPerson(uuid)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?)
    {
        inflater?.inflate(R.menu.menu_person_update, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(AppConstants.KEY_UUID, uuid)
        outState?.putString(AppConstants.KEY_FILENAME, filename)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            showImage(filename)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        when (requestCode)
        {
            MY_CAMERA_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    presenter.capturarFoto(uuid)
                }
                return
            }
        }
    }

    override fun showCapturarFoto(uiid: String)
    {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null)
        {
            var photoFile: File? = Utils.createImageFile(activity, uuid)
            if (photoFile != null)
            {
                //Salva o caminho completo da imagem
                filename = photoFile.absoluteFile.toString()
                val photoURI = FileProvider.getUriForFile(activity,
                        "br.com.wsilva.opencvfacerecognition.fileprovider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    fun verificarPermissaoAcessoCamera()
    {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA))
            {
            } else {
                ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE), MY_CAMERA_REQUEST_CODE)
            }
        }
    }

    fun showImage(filename: String?)
    {
        if (filename != null) {
            // Get the dimensions of the View
            val targetW = 180
            val targetH = 180

            // Get the dimensions of the bitmap
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filename, bmOptions)
            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            // Determine how much to scale down the image
            val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor
            bmOptions.inPurgeable = true

            val bitmap = BitmapFactory.decodeFile(filename, bmOptions)
            imgContato.setImageBitmap(bitmap)
        }
    }

    override fun showPerson(entity: PersonEntity)
    {
        id = entity.id
        edtEmail.setText(entity.email)
        edtName.setText(entity.name)
        edtPhone.setText(entity.phone)
        edtAddress.setText(entity.address)
        filename = entity.photoFilename1
        showImage(filename)
    }

    override fun showSalvarSucesso()
    {
        Utils.createAlertDialog(activity, "Usuário salvo com sucesso.",
                R.string.app_name, R.string.app_ok, R.string.app_cancela,
                DialogInterface.OnClickListener { dialogInterface, i -> Log.d("OK", "OK")},
                null)
    }

    override fun showSalvarFalha() {
        Utils.createAlertDialog(activity, "Falha ao tentar salvar o Usuário.",
                R.string.app_name, R.string.app_ok, R.string.app_cancela,
                DialogInterface.OnClickListener { dialogInterface, i -> close()},
                null)
    }

    override fun close() {
        activity.finish()
    }
}