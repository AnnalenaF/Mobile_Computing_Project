package com.dhbw.cas.integra.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.Task
import com.dhbw.cas.integra.ui.catalogue.CatalogueViewModel

class TabPageFragment(private var tabPosition: Int) : Fragment(),
    LogWorkDialogFragment.LogWorkDialogListener {
    private lateinit var taskListAdapter: TabTaskListAdapter
    private lateinit var catalogueViewModel: CatalogueViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_current_sprint_tab, container, false)
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        catalogueViewModel =
            ViewModelProvider(this).get(CatalogueViewModel::class.java)

        val recyclerView = root.findViewById<RecyclerView>(R.id.tab_task_list)
        taskListAdapter = TabTaskListAdapter(catalogueViewModel, this, parentFragmentManager)
        recyclerView.adapter = taskListAdapter
        homeViewModel.activeSprintLive.observe(viewLifecycleOwner, {
            if (it != null) {

                val tasks = ArrayList<Task>()
                var desiredState = 0
                when (tabPosition) {
                    0 -> { //blocked
                        desiredState = 3
                    }
                    1 -> { //open
                        desiredState = 0 // default state is open
                    }
                    2 -> { //in progress
                        desiredState = 1
                    }
                    3 -> { //done
                        desiredState = 2
                    }
                }
                for (task in it.tasks) {
                    if (task.state == desiredState) {
                        tasks.add(task)
                    }
                }
                taskListAdapter.setTasks(tasks)
            }
        })
        homeViewModel.areas.observe(viewLifecycleOwner, {
            taskListAdapter.setAreas(it)
        })

        // add divider to recycler view list
        val layoutManager: LinearLayoutManager =
            recyclerView.layoutManager as LinearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        return root
    }

    override fun onLogDialogPositiveClick(dialog: DialogFragment, task: Task) {
        val loggedWork =
            dialog.dialog!!.findViewById<EditText>(R.id.logged_time_input).text.toString().toInt()
        task.loggedDuration = task.loggedDuration + loggedWork
        catalogueViewModel.updateTask(task)
    }
}