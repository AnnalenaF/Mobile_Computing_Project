package com.dhbw.cas.integra.ui.home

import android.view.*
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.Area
import com.dhbw.cas.integra.data.Task
import com.dhbw.cas.integra.ui.catalogue.CatalogueViewModel
import kotlinx.android.synthetic.main.item_task_tab.view.*


class TabTaskListAdapter(private var catalogueViewModel: CatalogueViewModel) :
    RecyclerView.Adapter<TabTaskListAdapter.TabTaskListViewHolder>() {
    private var tasks = emptyList<Task>()
    private var areas = emptyList<Area>()

    inner class TabTaskListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskTitle: TextView = itemView.task_title
        var taskArea: TextView = itemView.task_area
        var taskPrio: TextView = itemView.task_prio
        var taskPrioLabel: TextView = itemView.task_prio_label
        var taskDurationProgress: ProgressBar = itemView.duration_progress
        private var buttonTaskMenu: ImageButton = itemView.button_task_menu
        init {
            buttonTaskMenu.setOnClickListener {
                val popup = PopupMenu(buttonTaskMenu.context, itemView)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_set_open -> {
                            tasks[adapterPosition].state = 0
                            catalogueViewModel.updateTask(tasks[adapterPosition])
                            notifyDataSetChanged()
                            true
                        }
                        R.id.action_set_progress -> {
                            tasks[adapterPosition].state = 1
                            catalogueViewModel.updateTask(tasks[adapterPosition])
                            notifyDataSetChanged()
                            true
                        }
                        R.id.action_set_done -> {
                            tasks[adapterPosition].state = 2
                            catalogueViewModel.updateTask(tasks[adapterPosition])
                            notifyDataSetChanged()
                            true
                        }
                        R.id.action_set_blocked -> {
                            tasks[adapterPosition].state = 3
                            catalogueViewModel.updateTask(tasks[adapterPosition])
                            notifyDataSetChanged()
                            true
                        }
                        R.id.action_log_work -> {
                            //todo
                            true
                        }
                        else -> false
                    }
                }

                popup.inflate(R.menu.sprint_task_menu)
                popup.gravity = Gravity.END
                popup.show()
                when (tasks[adapterPosition].state){
                    0 -> {
                        popup.menu.findItem(R.id.action_set_open).isVisible = false
                    }
                    1 -> {
                        popup.menu.findItem(R.id.action_set_progress).isVisible = false
                    }
                    2 -> {
                        popup.menu.findItem(R.id.action_set_done).isVisible = false
                        popup.menu.findItem(R.id.action_log_work).isVisible = false
                    }
                    3 -> {
                        popup.menu.findItem(R.id.action_set_blocked).isVisible = false
                        popup.menu.findItem(R.id.action_log_work).isVisible = false
                    }
                }
            }
        }

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