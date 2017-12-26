package br.com.wsilva.opencvfacerecognition.person.list

import android.os.Bundle
import br.com.wsilva.opencvfacerecognition.R
import br.com.wsilva.opencvfacerecognition.core.BasicActivity

/**
 * Created by wellingtonasilva on 24/12/17.
 */
class PersonListActivity: BasicActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lay_person_list_activity)
        //Configuração inicial
        init(savedInstanceState)
    }

    fun init(savedInstanceState: Bundle?)
    {
        val fragmentManager = supportFragmentManager
        var fragment = fragmentManager.findFragmentByTag(PersonListFragment.TAG)
        if (fragment == null) {
            fragment = PersonListFragment.newInstance()
        }
        fragment.arguments = savedInstanceState
        addFragmentToActivity(fragmentManager, fragment, R.id.frameLayout, PersonListFragment.TAG)
    }
}