package br.com.wsilva.opencvfacerecognition.person.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.wsilva.opencvfacerecognition.R
import br.com.wsilva.opencvfacerecognition.model.entity.PersonEntity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.lay_person_list_apdater.view.*

/**
 * Created by wellingtonasilva on 24/12/17.
 */
class PersonListAdapter(private val context: Context,
                        private val list: List<PersonEntity>,
                        private val listener: PersonListAdapterListener): RecyclerView.Adapter<PersonListAdapter.ViewHolder>()
{
    interface PersonListAdapterListener {
        fun OnItemClickListener(personEntity: PersonEntity)
        fun OnItemDeleteClickListener(personEntity: PersonEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder
    {
        var view = LayoutInflater.from(context).inflate(R.layout.lay_person_list_apdater, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int)
    {
        val entity = list.get(position)
        holder?.lblName?.text = entity.name
        holder?.lblEmail?.text = entity.email
        holder?.lblPhone?.text = entity.phone
        //Picasso.with(context).load(entity.photoFilename).into(holder?.imgPerson)
        holder?.cardView?.setOnClickListener{listener.OnItemClickListener(entity)}
        holder?.btnExcluir?.setOnClickListener{listener.OnItemDeleteClickListener(entity)}
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val lblName = itemView.lblName
        val lblEmail = itemView.lblEmail
        val lblPhone = itemView.lblPhone
        val cardView = itemView.card_view
        val btnExcluir = itemView.btnExcluir
        val imgPerson = itemView.imgPerson
    }
}