package com.dhbw.cas.integra.ui.areas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_new_area.*


class AreasFragment : Fragment() {

    private lateinit var areasViewModel: AreasViewModel
    private lateinit var dialog: AlertDialog
    private lateinit var areasAdapter: AreasAdapter
    var labelArray = arrayOf(
        R.drawable.shape_area_label_0, R.drawable.shape_area_label_1,
        R.drawable.shape_area_label_2, R.drawable.shape_area_label_3,
        R.drawable.shape_area_label_4, R.drawable.shape_area_label_5,
        R.drawable.shape_area_label_6, R.drawable.shape_area_label_7,
        R.drawable.shape_area_label_8, R.drawable.shape_area_label_9
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        areasViewModel =
                ViewModelProvider(this).get(AreasViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_areas, container, false)

        // get recycler view containing area list and set adapter
        val recyclerView: RecyclerView = root.findViewById(R.id.areas_list)
        val main : AppCompatActivity = activity as AppCompatActivity
        areasAdapter = AreasAdapter(root, areasViewModel, main, labelArray)
        areasViewModel.areas.observe(main) { areas -> areasAdapter.setAreas(areas) }
        recyclerView.adapter = areasAdapter

        // add divider to recycler view list
        val layoutManager : LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            layoutManager.getOrientation()
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        // add Listener to Add Button
        val fab: FloatingActionButton = root.findViewById(R.id.action_add_area)
        fab.setOnClickListener { view ->
            if (areasAdapter.getAreas().size == 10){
                Snackbar.make(view, R.string.max_num_areas, Snackbar.LENGTH_LONG ).show()
            } else {
                val builder = AlertDialog.Builder(view.context)
                builder.apply {
                    setTitle(R.string.new_area)
                    setView(R.layout.dialog_new_area)
                    setPositiveButton(R.string.okay, null)
                    setNegativeButton(R.string.cancel, null)
                }
                dialog = builder.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val correct = validateArea()
                    if (correct) {
                        createArea()
                        dialog.dismiss()
                    }
                }
                adjustLabelArray()
                val spinnerAdapter = AreaLabelSpinnerAdapter(view.context, labelArray)
                dialog.new_area_label_spinner.adapter = spinnerAdapter
            }
        }
        return root
    }

    private fun createArea() {
        val text = dialog.new_area_text.text.toString()
        val label = dialog.new_area_label_spinner.selectedItem as Int
        areasViewModel.createArea(text, label)
    }

    private fun validateArea() : Boolean {
        val text = dialog.new_area_text.getText().toString()
        val areas = areasAdapter.getAreas()
        var text_non_unique = false
        for (area in areas) {
            if (area.text == text){
                text_non_unique = true
            }
        }
        if(text.length==0) {
            dialog.new_area_text.requestFocus()
            dialog.new_area_text.setError(getString(R.string.area_text_empty_error))
            return false
        } else if (text_non_unique == true){
            dialog.new_area_text.requestFocus()
            dialog.new_area_text.setError(getString(R.string.area_text_not_unique_error))
            return false
        } else {
            return true
        }
    }

    private fun adjustLabelArray() {
        val labelArrayList = arrayListOf(
            R.drawable.shape_area_label_0, R.drawable.shape_area_label_1,
            R.drawable.shape_area_label_2, R.drawable.shape_area_label_3,
            R.drawable.shape_area_label_4, R.drawable.shape_area_label_5,
            R.drawable.shape_area_label_6, R.drawable.shape_area_label_7,
            R.drawable.shape_area_label_8, R.drawable.shape_area_label_9
        )
        val areas = areasAdapter.getAreas()
        for (area in areas){
            labelArrayList.remove(area.label)
        }
        labelArray = labelArrayList.toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
}