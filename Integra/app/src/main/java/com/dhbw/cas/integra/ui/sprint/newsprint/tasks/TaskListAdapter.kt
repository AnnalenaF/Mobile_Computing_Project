package com.dhbw.cas.integra.ui.sprint.newsprint.tasks

import android.content.Context
import android.view.*
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.Area
import com.dhbw.cas.integra.data.Task
import com.dhbw.cas.integra.ui.MainViewModel
import kotlinx.android.synthetic.main.item_task_assign.view.*
import java.util.*
import kotlin.collections.ArrayList

class TaskListAdapter(private var mainViewModel: MainViewModel) :
    RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder>(), Filterable {

    private lateinit var context: Context
    private var tasks = emptyList<Task>()
    private var areas = emptyList<Area>()
    private var tasksFilterList = ArrayList<Task>()
    var selectedTasks = ArrayList<Task>()

    inner class TaskListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskTitle: TextView = itemView.task_title
        var taskArea: TextView = itemView.task_area
        var taskPrio: TextView = itemView.task_prio
        var taskPrioLabel: TextView = itemView.task_prio_label
        var taskDuration: TextView = itemView.task_duration
        var taskCheckbox: CheckBox = itemView.task_checkbox

        init {
            taskCheckbox.setOnClickListener {
                val currentTask = tasksFilterList[adapterPosition]
                val currentArea = areas.find { it.text == currentTask.area_text }
                if (taskCheckbox.isChecked){
                    selectedTasks.add(currentTask)
                    currentArea!!.remainingCapacity =
                        currentArea.remainingCapacity!! - currentTask.expectedDuration
                } else {
                    selectedTasks.remove(currentTask)
                    currentArea!!.remainingCapacity =
                        currentArea.remainingCapacity!! + currentTask.expectedDuration
                }
                mainViewModel.updateArea(currentArea)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_task_assign, parent, false)

        return TaskListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        with(tasksFilterList[position]) {
            holder.taskTitle.text = title
            holder.taskArea.text = area_text
            holder.taskDuration.text = expectedDuration.toString()
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
            holder.taskCheckbox.isChecked = this in selectedTasks
        }
    }

    override fun getItemCount(): Int {
        return tasksFilterList.size
    }

    fun setTasks(tasks: List<Task>, sortCriterion: Int = 0, sortDirection: Int = 0) {
        val tasksArrayList: ArrayList<Task> = tasks as ArrayList<Task>

        when (sortCriterion) {
            R.id.radio_sort_prio -> { // initial sorting by priority and title
                if (sortDirection == R.id.radio_sort_asc) {
                    tasksArrayList.sortWith(compareBy<Task> { it.priority }.thenBy { it.title })
                } else {
                    tasksArrayList.sortWith(compareByDescending<Task> { it.priority }
                        .thenByDescending { it.title })
                }
            }
            R.id.radio_sort_duration -> {
                if (sortDirection == R.id.radio_sort_asc) {
                    tasksArrayList.sortWith(compareBy<Task> { it.expectedDuration }
                        .thenBy { it.title })
                } else {
                    tasksArrayList.sortWith(compareByDescending<Task> { it.expectedDuration }
                        .thenByDescending { it.title })
                }
            }
            R.id.radio_sort_title -> {
                if (sortDirection == R.id.radio_sort_asc) {
                    tasksArrayList.sortBy { it.title }
                } else {
                    tasksArrayList.sortByDescending { it.title }
                }
            }
            else -> {
                tasksArrayList.sortWith(compareBy<Task> { it.priority }.thenBy { it.title })
            }
        }
        this.tasks = tasksArrayList
        tasksFilterList = tasksArrayList
        notifyDataSetChanged()
    }

    fun getTasks(): List<Task> {
        return tasks
    }

    fun setAreas(areas: List<Area>) {
        this.areas = areas
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val tasksArrayList: ArrayList<Task> = tasks as ArrayList<Task>
                val charSearch = constraint.toString()
                tasksFilterList = if (charSearch.isEmpty()) {
                    tasksArrayList
                } else {
                    val resultList = ArrayList<Task>()
                    for (row in tasks) {
                        if (row.title.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                            or row.area_text.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = tasksFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                tasksFilterList = results?.values as ArrayList<Task>
                notifyDataSetChanged()
            }

        }
    }
}