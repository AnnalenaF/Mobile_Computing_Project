package com.dhbw.cas.integra.ui.catalogue

import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.view.isInvisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.Task
import com.dhbw.cas.integra.data.Area
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_task.view.*

class CatalogueAdapter( private val view: View,
                        private val activity: AppCompatActivity,
                        private val catalogueViewModel: CatalogueViewModel):
        RecyclerView.Adapter<CatalogueAdapter.CatalogueViewHolder>(), ActionMode.Callback {

    private lateinit var context : Context
    private var tasks = emptyList<Task>()
    private var areas = emptyList<Area>()
    private val selectedItems = arrayListOf<Task>()
    private var multiSelect = false

    inner class CatalogueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskTitle: TextView = itemView.task_title
        var taskArea: TextView = itemView.task_area
        var taskPrio: TextView = itemView.task_prio
        var taskPrioLabel: TextView = itemView.task_prio_label
        var taskDuration: TextView = itemView.task_duration
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogueViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context)
                .inflate(R.layout.item_task, parent, false)
        return CatalogueViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatalogueViewHolder, position: Int) {
        var currentArea : Area? = null
        with(tasks[position]) {
            holder.taskTitle.text = title
            holder.taskArea.text = area_text
            if (priority != 0) {
                holder.taskPrio.text = priority.toString()
            } else {
                holder.taskPrioLabel.isInvisible = true
                holder.taskPrio.isInvisible = true
            }
            holder.taskDuration.text = expectedDuration.toString()
            currentArea = areas.find { it.text == area_text }
            if (currentArea != null) {
                holder.taskArea.setBackgroundResource(currentArea!!.label)
            }
        }

        val currentTask = tasks[position]
        if (selectedItems.contains(currentTask)) {
            // if the item is selected, let the user know by adding a dark layer above it
            holder.itemView.alpha = 0.3f
        } else { // else, keep it as it is
            holder.itemView.alpha = 1.0f
        }

        // start multi selection when long pressing a task and display context menu for deletion
        holder.itemView.setOnLongClickListener {
            if (!multiSelect) {
                multiSelect = true
                // add task to list of selected items
                selectItem(holder, currentTask)
                // show context menu
                val appCompatActivity : AppCompatActivity = activity
                appCompatActivity.startSupportActionMode(this)
                true
            } else
                false
        }

        // if task is clicked in multi selection add it to list of selected items
        holder.itemView.setOnClickListener {
            if (multiSelect)
                selectItem(holder, currentTask)
            else {
                val action = CatalogueFragmentDirections.actionNavCatalogueToNavTask(
                    currentTask.id, currentTask.title, currentTask.description, currentTask.priority,
                    currentTask.expectedDuration, currentTask.loggedDuration, currentTask.area_text, currentArea!!.label)
                view.findNavController().navigate(action)
            }
        }
    }

    override fun getItemCount(): Int {
        return this.tasks.size
    }

    fun setTasks(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    fun setAreas(areas: List<Area>) {
        this.areas = areas
        notifyDataSetChanged()
    }

    private fun selectItem(viewHolder: CatalogueAdapter.CatalogueViewHolder, task: Task) {
        if (selectedItems.contains(task)) {
            selectedItems.remove(task)
            viewHolder.itemView.alpha = 1.0f
        } else {
            selectedItems.add(task)
            viewHolder.itemView.alpha = 0.3f
        }
    }

    override fun onCreateActionMode(
        mode: ActionMode,
        menu: Menu?
        ): Boolean {
            // hide main action bar and inflate context menu for deletion
            activity.supportActionBar?.hide()
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.multiselect_menu, menu)
            return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(
        mode: ActionMode?,
        item: MenuItem?
    ): Boolean {
        if (item?.itemId == R.id.action_multi_delete) {
            // delete selected task(s)
            val tasksDel = mutableListOf<Task>()
            for (selItem in selectedItems) {
                tasksDel.add(selItem)
                catalogueViewModel.deleteTask(selItem)
            }

            // show success message containing number of deleted areas
            val message : String = if (selectedItems.size == 1) {
                context.getString(R.string.message_task_deleted, 1)
            } else {
                context.getString(R.string.message_tasks_deleted, selectedItems.size)
            }
            val snackbarSuccess = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            // enable undo action on success message snackbar
            snackbarSuccess.setAction(R.string.action_undo) {
                for (taskDel in tasksDel) {
                    catalogueViewModel.createTask(title = taskDel.title,
                                                  description = taskDel.description,
                                                  priority = taskDel.priority,
                                                  area_text = taskDel.area_text,
                                                  expectedDuration = taskDel.expectedDuration)
                }
            }
            snackbarSuccess.show()
            mode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        // show main action bar again when context menu for deletion is closed
        activity.supportActionBar?.show()
        // end multi selection mode and clear list of selected areas
        multiSelect = false
        selectedItems.clear()
        notifyDataSetChanged()
    }
}