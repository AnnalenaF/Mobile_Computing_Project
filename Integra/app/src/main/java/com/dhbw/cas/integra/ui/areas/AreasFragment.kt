package com.dhbw.cas.integra.ui.areas

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.MainActivity
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.databinding.FragmentAreasBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_new_area.*


class AreasFragment : Fragment() {

    private lateinit var areasViewModel: AreasViewModel
    private lateinit var binding: FragmentAreasBinding
    private lateinit var dialog: AlertDialog
    val labelArray = arrayOf(R.drawable.shape_area_label_0, R.drawable.shape_area_label_1,
                             R.drawable.shape_area_label_2, R.drawable.shape_area_label_3,
                             R.drawable.shape_area_label_4, R.drawable.shape_area_label_5,
                             R.drawable.shape_area_label_6, R.drawable.shape_area_label_7,
                             R.drawable.shape_area_label_8, R.drawable.shape_area_label_9)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        areasViewModel =
                ViewModelProvider(this).get(AreasViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_areas, container, false)
        val root = binding.getRoot()
        //val root = inflater.inflate(R.layout.fragment_areas, container, false)

        // get recycler view containing area list and set adapter
        val recyclerView: RecyclerView = binding.areasList
        val main : AppCompatActivity = activity as AppCompatActivity
        val areasAdapter = AreasAdapter(root, main, labelArray)
        areasAdapter.setAreas(areasViewModel.areas)
        recyclerView.adapter = areasAdapter

        // add divider to recycler view list
        val layoutManager : LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            layoutManager.getOrientation()
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        // add Listener to Add Button
        val fab: FloatingActionButton = binding.actionAddArea
        fab.setOnClickListener { view ->
            val builder = AlertDialog.Builder(view.context)
            builder.apply {
                setTitle(R.string.new_area)
                setView(R.layout.dialog_new_area)
                setPositiveButton(R.string.okay) {dialog, id ->
                    createArea()
                }
                setNegativeButton(R.string.cancel, null)
            }
            dialog = builder.create()
            dialog.show()
            val spinnerAdapter = AreaLabelSpinnerAdapter(view.context, labelArray)
            dialog.new_area_label_spinner.adapter = spinnerAdapter
        }
        return root
    }

    private fun createArea() {
        val text = dialog.new_area_text.text.toString()
        val label = dialog.new_area_label_spinner.selectedItem as Int
        areasViewModel.createArea(text, label)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
}