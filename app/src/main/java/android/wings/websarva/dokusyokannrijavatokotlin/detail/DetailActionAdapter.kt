package android.wings.websarva.dokusyokannrijavatokotlin.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.realm.`object`.ActionPlanObject
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList

class DetailActionAdapter(
    private val bookActionList: RealmList<ActionPlanObject>?
) : RecyclerView.Adapter<DetailActionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val actionDate = view.findViewById<TextView>(R.id.date_recycler_cell)
        val actionDo = view.findViewById<TextView>(R.id.do_recycler_cell)
        val actionNext = view.findViewById<TextView>(R.id.next_recycler_cell)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.action_recyclerview_cell, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return bookActionList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val action = bookActionList?.get(position)
        holder.actionDate.text = action?.date
        holder.actionDo.text = action?.actionPlans
        holder.actionNext.text = action?.nextActionPlans
    }
}