package android.wings.websarva.dokusyokannrijavatokotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmList
import io.realm.RealmRecyclerViewAdapter

class DetailActionAdapter(
    private val context: Context,
    private val bookActionList: OrderedRealmCollection<BookListObject>,
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<BookListObject, DetailActionAdapter.ViewHolder>(bookActionList, autoUpdate) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val actionDate = view.findViewById<TextView>(R.id.date_recycler_cell)
        val actionDo = view.findViewById<TextView>(R.id.do_recycler_cell)
        val actionNext = view.findViewById<TextView>(R.id.next_recycler_cell)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.action_recyclerview_cell, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return bookActionList[0].actionPlanDairy.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val action = bookActionList[0].actionPlanDairy[position]
        holder.actionDate.text = action?.date
        holder.actionDo.text = action?.actionPlans
        holder.actionNext.text = action?.nextActionPlans
    }
}