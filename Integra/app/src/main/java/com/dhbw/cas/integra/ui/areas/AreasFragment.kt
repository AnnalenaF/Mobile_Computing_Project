package com.dhbw.cas.integra.ui.areas

import com.dhbw.cas.integra.R
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.dhbw.cas.integra.databinding.FragmentAreasBinding
import com.dhbw.cas.integra.ui.catalogue.CatalogueViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_new_area.*


class AreasFragment : Fragment() {

    private lateinit var areasViewModel: AreasViewModel
    private lateinit var dialog: AlertDialog
    private lateinit var areasAdapter: AreasAdapter
    private val catalogueViewModel: CatalogueViewModel by activityViewModels()

    private var _binding: FragmentAreasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        areasViewModel =
                ViewModelProvider(this).get(AreasViewModel::class.java)
        //get view binding
        _binding = FragmentAreasBinding.inflate(inflater, container, false)
        val view = binding.root

        // get recycler view containing area list, set adapter and observe areas
        val recyclerView: RecyclerView = binding.areasList
        val main : AppCompatActivity = activity as AppCompatActivity

        areasAdapter = AreasAdapter(binding.root, areasViewModel, catalogueViewModel, main)
        recyclerView.adapter = areasAdapter
        areasViewModel.areasWithTasks.observe(main) { areas -> areasAdapter.setAreas(areas) }

        // add divider to recycler view list
        val layoutManager : LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        // add Listener to Add Button
        val fab: FloatingActionButton = binding.actionAddArea
        fab.setOnClickListener { buttonView ->
            if (areasAdapter.getAreas().size == 10){ // limit number of areas to 10
                Snackbar.make(buttonView, R.string.max_num_areas, Snackbar.LENGTH_LONG ).show()
            } else { // create and open dialog to create area
                val builder = AlertDialog.Builder(view.context)
                builder.apply {
                    setTitle(R.string.new_area)
                    setView(R.layout.dialog_new_area)
                    setPositiveButton(R.string.okay, null)
                    setNegativeButton(R.string.cancel, null)
                }
                dialog = builder.create()
                dialog.show()
                //check and create area when dialog is left via "OK"
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val correct = validateArea()
                    if (correct) {
                        createArea()
                        dialog.dismiss()
                    }
                }
                // set adapter for label spinner containing only color labels left
                val spinnerAdapter = AreaLabelSpinnerAdapter(view.context, areasAdapter.getLabelArray())
                dialog.new_area_label_spinner.adapter = spinnerAdapter
            }
        }
        return view
    }

    private fun createArea() {
        val text = dialog.new_area_text.text.toString()
        val label = dialog.new_area_label_spinner.selectedItem as Int
        areasViewModel.createArea(text, label)
    }

    // validate text of to-be-created area to be not initial and not already used in another area
    private fun validateArea() : Boolean {
        val text = dialog.new_area_text.text.toString()
        val areas = areasAdapter.getAreas()
        var textNonUnique = false
        for (area in areas) {
            if (area.text == text){
                textNonUnique = true
            }
        }
        return when {
            text.isEmpty() -> {
                dialog.new_area_text.requestFocus()
                dialog.new_area_text.error = getString(R.string.area_text_empty_error)
                false
            }
            textNonUnique -> {
                dialog.new_area_text.requestFocus()
                dialog.new_area_text.error = getString(R.string.area_text_not_unique_error)
                false
            }
            else -> {
                true
            }
        }
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
}