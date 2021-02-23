package com.dhbw.cas.integra.ui.catalogue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.ui.areas.AreasViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.dialog_new_task.*

class CatalogueFragment : Fragment() {

    private lateinit var catalogueViewModel: CatalogueViewModel
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        catalogueViewModel =
                ViewModelProvider(this).get(CatalogueViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_catalogue, container, false)

        // get recycler view containing task list, set adapter and observe tasks
        val recyclerView: RecyclerView = root.findViewById(R.id.task_list)
        val catalogueAdapter = CatalogueAdapter()
        recyclerView.adapter = catalogueAdapter
        val main : AppCompatActivity = activity as AppCompatActivity
        catalogueViewModel.tasks.observe(main) { tasks -> catalogueAdapter.setTasks(tasks) }

        // add divider to recycler view list
        val layoutManager : LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        // add Listener to Add Button
        val fab: FloatingActionButton = root.findViewById(R.id.action_add_task)
        fab.setOnClickListener { view ->
            //create and open dialog to create area
            val builder = AlertDialog.Builder(view.context)
            builder.apply {
                setTitle(R.string.new_task)
                setView(R.layout.dialog_new_task)
                setPositiveButton(R.string.okay, null)
                setNegativeButton(R.string.cancel, null)
            }
            dialog = builder.create()
            dialog.show()
            //check and create area when dialog is left via "OK"
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val correct = validateTask()
                if (correct) {
                    createTask()
                    dialog.dismiss()
                }
            }
            // set adapter for areas spinner
            val areasViewModel =
                    ViewModelProvider(this).get(AreasViewModel::class.java)
            areasViewModel.getAreaTexts().observe(viewLifecycleOwner, { spinnerData ->
                val spinnerAdapter = ArrayAdapter(main, android.R.layout.simple_spinner_item, spinnerData)
                dialog.new_task_area_spinner.adapter = spinnerAdapter
            })
        }

        return root
    }

    private fun validateTask(): Boolean{
        return true
    }

    private fun createTask(){
        val title = dialog.new_task_title.text.toString()
        val descr = dialog.new_task_descr.text.toString()
        val prio = dialog.new_task_prio.text.toString().toInt()
        val duration = dialog.new_task_duration.text.toString().toInt()
        val area = dialog.new_task_area_spinner.selectedItem as String
        catalogueViewModel.createTask(title, descr, prio, area, duration)
    }
}