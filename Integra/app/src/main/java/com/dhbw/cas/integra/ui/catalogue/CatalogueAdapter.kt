package com.dhbw.cas.integra.ui.catalogue

import android.content.Context
import android.view.*
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.view.isInvisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.Area
import com.dhbw.cas.integra.data.Task
import com.dhbw.cas.integra.ui.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_empty_task.view.*
import kotlinx.android.synthetic.main.item_task.view.*

class CatalogueAdapter(
    private val view: View,
    private val activity: AppCompatActivity,
    private val mainViewModel: MainViewModel
):
        RecyclerView.Adapter<CatalogueAdapter.BaseViewHolder<*>>(), ActionMode.Callback {

    private val viewTypeEmpty = 0
    private val viewTypeNotEmpty = 1

    private lateinit var context : Context
    private var adapterDataList: List<Any> = emptyList()
    private var tasks = emptyList<Task>()
    private var areas = emptyList<Area>()
    private val selectedItems = arrayListOf<Task>()
    private var multiSelect = false
    private val adapter = this

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    inner class TaskViewHolder(itemView: View) : BaseViewHolder<Task>(itemView) {
        private var taskTitle: TextView = itemView.task_title
        private var taskArea: TextView = itemView.task_area
        private var taskPrio: TextView = itemView.task_prio
        private var taskPrioLabel: TextView = itemView.task_prio_label
        private var taskDuration: TextView = itemView.task_duration

        override fun bind(item: Task) {
            var currentArea : Area? = null
            with(tasks[adapterPosition]) {
                taskTitle.text = title
                taskArea.text = area_text
                if (priority != 99) {
                    taskPrio.text = priority.toString()
                } else {
                    taskPrioLabel.isInvisible = true
                    taskPrio.isInvisible = true
                }
                taskDuration.text = expectedDuration.toString()
                currentArea = areas.find { it.text == area_text }
                if (currentArea != null) {
                    taskArea.setBackgroundResource(currentArea!!.label)
                }
            }

            val currentTask = tasks[adapterPosition]
            if (selectedItems.contains(currentTask)) {
                // if the item is selected, let the user know by adding a dark layer above it
                itemView.alpha = 0.3f
            } else { // else, keep it as it is
                itemView.alpha = 1.0f
            }

            // start multi selection when long pressing a task and display context menu for deletion
            itemView.setOnLongClickListener {
                if (!multiSelect) {
                    multiSelect = true
                    // add task to list of selected items
                    selectItem(this, currentTask)
                    // show context menu
                    val appCompatActivity : AppCompatActivity = activity
                    appCompatActivity.startSupportActionMode(adapter)
                    true
                } else
                    false
            }

            // if task is clicked in multi selection add it to list of selected items
            itemView.setOnClickListener {
                if (multiSelect)
                    selectItem(this, currentTask)
                else {
                    val action = CatalogueFragmentDirections.actionNavCatalogueToNavTask(
                        currentTask,
                        currentArea!!.label
                    )
                    view.findNavController().navigate(action)
                }
            }
        }
    }

    inner class EmptyViewHolder(itemView: View) : BaseViewHolder<String>(itemView) {
        private var emptyText: TextView = itemView.empty_item_text

        override fun bind(item: String) {
            emptyText.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : BaseViewHolder<*> {
        context = parent.context
        val view: View
        return when (viewType) {
            viewTypeNotEmpty -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.item_task, parent, false)
                TaskViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.item_empty_task, parent, false)
                EmptyViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = adapterDataList[position]
        when (holder) {
            is TaskViewHolder -> holder.bind(element as Task)
            is EmptyViewHolder -> holder.bind(element as String)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int {
        return adapterDataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (tasks.isEmpty()) {
            viewTypeEmpty
        }else{
            viewTypeNotEmpty
        }
    }

    fun setTasks(tasks: List<Task>) {
        this.tasks = tasks
        this.adapterDataList = tasks
        if (tasks.isEmpty()){
            this.adapterDataList =
                listOf(view.context.getString(R.string.placeholder_no_task))
        }
        notifyDataSetChanged()
    }

    fun setAreas(areas: List<Area>) {
        this.areas = areas
        notifyDataSetChanged()
    }

    private fun selectItem(viewHolder: CatalogueAdapter.TaskViewHolder, task: Task) {
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
                mainViewModel.deleteTask(selItem)
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
                    mainViewModel.createTask(
                        title = taskDel.title,
                        description = taskDel.description,
                        priority = taskDel.priority,
                        area_text = taskDel.area_text,
                        expectedDuration = taskDel.expectedDuration
                    )
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