package com.dhbw.cas.integra.ui.catalogue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.ui.areas.Area
import com.dhbw.cas.integra.ui.areas.AreasViewModel
import kotlinx.android.synthetic.main.item_task.view.*

class CatalogueAdapter(): RecyclerView.Adapter<CatalogueAdapter.CatalogueViewHolder>() {
    private var tasks = emptyList<Task>()
    private var areas = emptyList<Area>()


    inner class CatalogueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskTitle = itemView.task_title
        var taskArea = itemView.task_area
        var taskPrio = itemView.task_prio
        var taskDuration = itemView.task_duration
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogueViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task, parent, false)
        return CatalogueViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatalogueViewHolder, position: Int) {
        with(tasks[position]) {
            holder.taskTitle.text = title
            holder.taskArea.text = area_text
            holder.taskPrio.setText(priority.toString())
            holder.taskDuration.setText(expectedDuration.toString())
            val area = areas.find { it.text == area_text }
            if (area != null) {
                holder.taskArea.setBackgroundResource(area.label)
            }
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

    fun setAreas(areas: List<Area>) {
        this.areas = areas
        notifyDataSetChanged()
    }
}