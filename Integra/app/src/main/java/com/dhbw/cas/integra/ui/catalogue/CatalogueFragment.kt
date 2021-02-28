package com.dhbw.cas.integra.ui.catalogue

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.databinding.FragmentCatalogueBinding
import com.dhbw.cas.integra.ui.areas.AreasViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.dialog_new_task.*

class CatalogueFragment : Fragment(), CreateTaskDialogFragment.CreateTaskDialogListener {

    private lateinit var catalogueViewModel: CatalogueViewModel
    private var _binding: FragmentCatalogueBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // get View Models
        catalogueViewModel =
            ViewModelProvider(this).get(CatalogueViewModel::class.java)
        val areasViewModel =
            ViewModelProvider(this).get(AreasViewModel::class.java)

        // get view binding and activity
        _binding = FragmentCatalogueBinding.inflate(inflater, container, false)
        val view = binding.root
        val main: AppCompatActivity = activity as AppCompatActivity

        // get recycler view containing task list, set adapter and its data by observing
        val recyclerView: RecyclerView = binding.taskList
        val catalogueAdapter = CatalogueAdapter(binding.root, main, catalogueViewModel)
        recyclerView.adapter = catalogueAdapter
        catalogueViewModel.tasks.observe(main) { tasks -> catalogueAdapter.setTasks(tasks) }
        areasViewModel.areas.observe(main) { areas -> catalogueAdapter.setAreas(areas) }

        // add divider to recycler view list
        val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        // add Listener to Add Button
        val fab: FloatingActionButton = binding.actionAddTask
        fab.setOnClickListener {
            //create and open dialog to create task
            val dialogFrag = CreateTaskDialogFragment()
            dialogFrag.setTargetFragment(this, 1)
            dialogFrag.show(parentFragmentManager, "CreateTaskDialogFragment")

            // set adapter for areas spinner
            areasViewModel.getAreaTexts().observe(viewLifecycleOwner, { spinnerData ->
                val spinnerAdapter =
                    ArrayAdapter(
                        activity as AppCompatActivity,
                        android.R.layout.simple_spinner_item,
                        spinnerData
                    )
                dialogFrag.setSpinnerAdapter(spinnerAdapter)
            })
        }

        return view
    }

    // validate that title and duration are not empty
    private fun validateTask(dialog: Dialog): Boolean {
        val title = dialog.new_task_title.text.toString()
        val duration = dialog.new_task_duration.text.toString()
        return when {
            title.isEmpty() -> {
                dialog.new_task_title.requestFocus()
                dialog.new_task_title.error = getString(R.string.task_title_empty_error)
                false
            }
            duration.isEmpty() -> {
                dialog.new_task_duration.requestFocus()
                dialog.new_task_duration.error = getString(R.string.task_duration_empty_error)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun createTask(dialog: Dialog) {
        val title = dialog.new_task_title.text.toString()
        val descr = dialog.new_task_descr.text.toString()
        val prio = dialog.new_task_prio.text.toString()
        var prioInt = 99
        if (prio.isNotEmpty()) {
            prioInt = prio.toInt()
        }
        val duration = dialog.new_task_duration.text.toString().toInt()
        val area = dialog.new_task_area_spinner.selectedItem as String
        catalogueViewModel.createTask(title, descr, prioInt, area, duration)
    }

    // enable fragment to display options menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //check and create task when dialog is left via "OK"
    override fun onCreateDialogPositiveClick(dialog: DialogFragment, view: View) {
        val correct = validateTask(dialog.dialog!!)
        if (correct) {
            createTask(dialog.dialog!!)
            dialog.dismiss()
        }
    }
}