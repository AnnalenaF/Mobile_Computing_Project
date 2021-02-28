package com.dhbw.cas.integra.ui.sprint.newsprint.tasks

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.RadioGroup
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.MainActivity
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.databinding.FragmentNewSprintTasksBinding
import com.dhbw.cas.integra.ui.MainViewModel
import com.google.android.material.snackbar.Snackbar


class NewSprintTasksFragment : Fragment(), SortTasksDialogFragment.SortDialogListener {
    private lateinit var taskListAdapter: TaskListAdapter
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentNewSprintTasksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewSprintTasksBinding.inflate(inflater, container, false)
        val view = binding.root

        val main = activity as AppCompatActivity

        // get recycler view containing task list, set adapter and its data by observing
        val recyclerViewTasks: RecyclerView = binding.taskListNewSprint
        taskListAdapter = TaskListAdapter(mainViewModel)
        recyclerViewTasks.adapter = taskListAdapter
        mainViewModel.tasks.observe(
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
        val recyclerViewAreas: RecyclerView = binding.availableCapacities
        val areasCapacityAvailAdapter = AreasCapacityAvailAdapter()
        recyclerViewAreas.adapter = areasCapacityAvailAdapter
        mainViewModel.areas.observe(main) { liveAreas ->
            areasCapacityAvailAdapter.setAreas(liveAreas)
        }

        val sortButton = binding.buttonSortTasks

        sortButton.setOnClickListener {
            val dialogFrag = SortTasksDialogFragment()
            dialogFrag.setTargetFragment(this, 1)
            dialogFrag.show(parentFragmentManager, "SortTasksDialogFragment")
        }

        // set query listener for task search
        val searchView = binding.taskSearch
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                taskListAdapter.filter.filter(newText)
                return false
            }
        })

        return view
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
                // check tasks assigned to sprint
                if (taskListAdapter.selectedTasks.size == 0){
                    val snackbarError = Snackbar.make(binding.root, R.string.sprint_no_tasks, Snackbar.LENGTH_LONG )
                    snackbarError.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red_error))
                    snackbarError.show()
                } else {
                    // reset capacities of areas
                    val args: NewSprintTasksFragmentArgs by navArgs()
                    for (area in args.areas) {
                        area.totalCapacity = 0
                        area.remainingCapacity = 0
                        mainViewModel.updateArea(area)
                    }
                    // save sprint
                    mainViewModel.createSprintWithTasks(
                        args.startDate,
                        args.endDate,
                        taskListAdapter.selectedTasks
                    )
                    // restart activity
                    val intent = Intent(binding.root.context, MainActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                                or Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                    startActivity(intent)
                }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}