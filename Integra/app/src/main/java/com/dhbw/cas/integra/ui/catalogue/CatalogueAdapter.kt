package com.dhbw.cas.integra.ui.catalogue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R

class CatalogueAdapter(): RecyclerView.Adapter<CatalogueAdapter.CatalogueViewHolder>() {
    private var tasks: List<Task> = emptyList<Task>()

    inner class CatalogueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogueViewHolder {
        //context = parent.context
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_area, parent, false)
        return CatalogueViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatalogueViewHolder, position: Int) {
        with(tasks[position]) {
            //TODO("Not yet implemented")
            //holder.areaText.setText(text)
            //holder.areaLabelSpinner.setSelection(labelArray.indexOf(label))
        }
    }

    override fun getItemCount(): Int {
        return this.tasks.size
    }

    fun getTasks() : List<Task> {
        return this.tasks
    }

    fun setTasks(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }
}