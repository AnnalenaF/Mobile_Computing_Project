package com.dhbw.cas.integra.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.MainActivity
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.ui.areas.AreasViewModel
import com.dhbw.cas.integra.ui.catalogue.CatalogueViewModel


class NewSprintTasksFragment : Fragment(), SortTasksDialogFragment.SortDialogListener {
    private lateinit var root: View
    private lateinit var taskListAdapter: TaskListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_new_sprint_tasks, container, false)
        val main = activity as AppCompatActivity
        val catalogueViewModel =
            ViewModelProvider(this).get(CatalogueViewModel::class.java)
        val areasViewModel =
            ViewModelProvider(this).get(AreasViewModel::class.java)

        // get recycler view containing task list, set adapter and its data by observing
        val recyclerViewTasks: RecyclerView = root.findViewById(R.id.task_list_new_sprint)
        taskListAdapter = TaskListAdapter(areasViewModel)
        recyclerViewTasks.adapter = taskListAdapter
        catalogueViewModel.tasks.observe(
            viewLifecycleOwner,
            { tasks -> taskListAdapter.setTasks(tasks) })

        // add divider to recycler view list
        val layoutManager: LinearLayoutManager =
            recyclerViewTasks.layoutManager as LinearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(
            recyclerViewTasks.context,
            layoutManager.orientation
        )
        recyclerViewTasks.addItemDecoration(dividerItemDecoration)

        val args: NewSprintTasksFragmentArgs by navArgs()
        val areas = args.areas
        taskListAdapter.setAreas(areas)

        // get recycler view containing area capacities, set adapter and its data by observing
        val recyclerViewAreas: RecyclerView = root.findViewById(R.id.available_capacities)
        val areasCapacityAvailAdapter = AreasCapacityAvailAdapter()
        recyclerViewAreas.adapter = areasCapacityAvailAdapter
        areasViewModel.areas.observe(main) { liveAreas ->
            areasCapacityAvailAdapter.setAreas(liveAreas)
        }

        val sortButton = root.findViewById<ImageButton>(R.id.button_sort_tasks)
        val filterButton = root.findViewById<ImageButton>(R.id.button_filter_tasks)

        sortButton.setOnClickListener {
            val dialogFrag = SortTasksDialogFragment()
            dialogFrag.setTargetFragment(this, 1)
            dialogFrag.show(parentFragmentManager, "SortTasksDialogFragment")
        }

        // set query listener for task search
        val searchView = root.findViewById<SearchView>(R.id.task_search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                taskListAdapter.filter.filter(newText)
                return false
            }
        })

        return root
    }

    // enable fragment to display options menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.start_sprint_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // do not display settings during splint planning
    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_settings).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_start -> { // save sprint and display current sprint
                // restart activity
                val intent = Intent(root.context, MainActivity::class.java)
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            or Intent.FLAG_ACTIVITY_NEW_TASK
                )
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSortDialogPositiveClick(dialog: DialogFragment) {
        val radioGroupCriterion =
            dialog.dialog!!.findViewById<RadioGroup>(R.id.radiogroup_sort_by)
        val radioGroupDirection =
            dialog.dialog!!.findViewById<RadioGroup>(R.id.radiogroup_sort_how)
        val checkedCriterionButtonId = radioGroupCriterion.checkedRadioButtonId
        val checkedDirectionButtonId = radioGroupDirection.checkedRadioButtonId
        val tasks = taskListAdapter.getTasks()
        taskListAdapter.setTasks(tasks, checkedCriterionButtonId, checkedDirectionButtonId)
    }
}