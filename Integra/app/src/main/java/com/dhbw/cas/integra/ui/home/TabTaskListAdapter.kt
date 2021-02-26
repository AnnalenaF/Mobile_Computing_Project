package com.dhbw.cas.integra.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.Area
import com.dhbw.cas.integra.data.Task
import kotlinx.android.synthetic.main.item_task_assign.view.*
import kotlinx.android.synthetic.main.item_task_assign.view.task_area
import kotlinx.android.synthetic.main.item_task_assign.view.task_prio
import kotlinx.android.synthetic.main.item_task_assign.view.task_prio_label
import kotlinx.android.synthetic.main.item_task_assign.view.task_title
import kotlinx.android.synthetic.main.item_task_tab.view.*

class TabTaskListAdapter :
    RecyclerView.Adapter<TabTaskListAdapter.TabTaskListViewHolder>() {
    private var tasks = emptyList<Task>()
    private var areas = emptyList<Area>()

    inner class TabTaskListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskTitle: TextView = itemView.task_title
        var taskArea: TextView = itemView.task_area
        var taskPrio: TextView = itemView.task_prio
        var taskPrioLabel: TextView = itemView.task_prio_label
        var taskDurationProgress: ProgressBar = itemView.duration_progress

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabTaskListViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_task_tab, parent, false)

        return TabTaskListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TabTaskListViewHolder, position: Int) {
        with(tasks[position]) {
            holder.taskTitle.text = title
            holder.taskArea.text = area_text
            holder.taskDurationProgress.max = expectedDuration
            holder.taskDurationProgress.progress = loggedDuration
            if (priority != 99) {
                holder.taskPrio.text = priority.toString()
                holder.taskPrioLabel.isInvisible = false
                holder.taskPrio.isInvisible = false
            } else {
                holder.taskPrioLabel.isInvisible = true
                holder.taskPrio.isInvisible = true
            }
            val currentArea = areas.find { it.text == area_text }
            if (currentArea != null) {
                holder.taskArea.setBackgroundResource(currentArea.label)
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
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