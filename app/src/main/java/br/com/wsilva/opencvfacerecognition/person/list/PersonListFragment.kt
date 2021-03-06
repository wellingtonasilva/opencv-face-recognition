package br.com.wsilva.opencvfacerecognition.person.list

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.Toast
import br.com.wsilva.opencvfacerecognition.AppApplication
import br.com.wsilva.opencvfacerecognition.R
import br.com.wsilva.opencvfacerecognition.facerecognition.FaceRecognitionActivity
import br.com.wsilva.opencvfacerecognition.constants.AppConstants
import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity
import br.com.wsilva.opencvfacerecognition.model.repository.PersonRepository
import br.com.wsilva.opencvfacerecognition.person.list.di.DaggerPersonListComponent
import br.com.wsilva.opencvfacerecognition.person.list.di.PersonListModule
import br.com.wsilva.opencvfacerecognition.person.update.PersonUpdateActivity
import kotlinx.android.synthetic.main.lay_person_list_fragment.*
import javax.inject.Inject

/**
 * Created by wellingtonasilva on 24/12/17.
 */
class PersonListFragment: Fragment(), PersonListContract.View, PersonListAdapter.PersonListAdapterListener
{
    @Inject
    lateinit var presenter: PersonListPresenter

    @Inject
    lateinit var repository: PersonRepository

    lateinit var adapter: PersonListAdapter

    companion object {
        val TAG: String = "PersonListFragment"
        fun newInstance(): PersonListFragment {
            return PersonListFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerPersonListComponent
                .builder()
                .appDatabaseModule(AppApplication.appComponent.appDataModule)
                .personListModule(PersonListModule(this))
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater?.inflate(R.layout.lay_person_list_fragment, container, false)
        view?.findViewById<FloatingActionButton>(R.id.fab)?.setOnClickListener { presenter.adicionarPerson()}
        (activity as AppCompatActivity).setSupportActionBar(view?.findViewById<Toolbar>(R.id.toolbar))
        setHasOptionsMenu(true)

        return view
    }

    override fun onResume() {
        super.onResume()
        presenter.listAllPerson()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?)
    {
        inflater?.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        if (item?.itemId == R.id.action_face_recognition) {
            showReconhecimentoFacial()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showPerson(list: List<PersonEntity>)
    {
        adapter = PersonListAdapter(activity, list, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
    }

    override fun showAdicionarPerson(uuid: String)
    {
        val intent = Intent(activity, PersonUpdateActivity::class.java)
        val bundle = Bundle()
        bundle.putString(AppConstants.KEY_UUID, uuid)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun showReconhecimentoFacial()
    {
        val intent = Intent(activity, FaceRecognitionActivity::class.java)
        startActivity(intent)
    }

    override fun showManterPerson(uuid: String) {
        val intent = Intent(activity, PersonUpdateActivity::class.java)
        val bundle = Bundle()
        bundle.putString(AppConstants.KEY_UUID, uuid)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun showConfirmarExcluir(entity: PersonEntity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showExcluirSucesso() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showExcluirFalha() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnItemClickListener(personEntity: PersonEntity) {
        presenter.manterPerson(personEntity)
    }

    override fun OnItemDeleteClickListener(personEntity: PersonEntity) {
        Toast.makeText(activity, personEntity.name, Toast.LENGTH_SHORT).show()
    }
}