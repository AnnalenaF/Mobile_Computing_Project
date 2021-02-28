package com.dhbw.cas.integra.ui.sprint

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
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.Task
import com.dhbw.cas.integra.databinding.FragmentCurrentSprintTabBinding
import com.dhbw.cas.integra.ui.catalogue.CatalogueViewModel

class TabPageFragment() : Fragment(),
    LogWorkDialogFragment.LogWorkDialogListener {
    private lateinit var taskListAdapter: TabTaskListAdapter
    private lateinit var catalogueViewModel: CatalogueViewModel
    private var tabPosition: Int = 0
    private var _binding: FragmentCurrentSprintTabBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(tabPosition: Int): Fragment {
            val instance = TabPageFragment()
            instance.tabPosition = tabPosition
            return instance
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentSprintTabBinding.inflate(inflater, container, false)
        val view = binding.root

        val homeViewModel =
            ViewModelProvider(this).get(SprintViewModel::class.java)
        catalogueViewModel =
            ViewModelProvider(this).get(CatalogueViewModel::class.java)

        val recyclerView = binding.tabTaskList
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

        return view
    }

    override fun onLogDialogPositiveClick(dialog: DialogFragment, task: Task) {
        val loggedWork =
            dialog.dialog!!.findViewById<EditText>(R.id.logged_time_input).text.toString().toInt()
        task.loggedDuration = task.loggedDuration + loggedWork
        catalogueViewModel.updateTask(task)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}