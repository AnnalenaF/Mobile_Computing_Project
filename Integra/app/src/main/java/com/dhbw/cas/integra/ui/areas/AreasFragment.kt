package com.dhbw.cas.integra.ui.areas

import com.dhbw.cas.integra.R
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_new_area.*


class AreasFragment : Fragment() {

    private lateinit var areasViewModel: AreasViewModel
    private lateinit var dialog: AlertDialog
    private lateinit var areasAdapter: AreasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        areasViewModel =
                ViewModelProvider(this).get(AreasViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_areas, container, false)

        // get recycler view containing area list, set adapter and observe areas
        val recyclerView: RecyclerView = root.findViewById(R.id.areas_list)
        val main : AppCompatActivity = activity as AppCompatActivity
        areasAdapter = AreasAdapter(root, areasViewModel, main)
        recyclerView.adapter = areasAdapter
        areasViewModel.areas.observe(main) { areas -> areasAdapter.setAreas(areas) }

        // add divider to recycler view list
        val layoutManager : LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        // add Listener to Add Button
        val fab: FloatingActionButton = root.findViewById(R.id.action_add_area)
        fab.setOnClickListener { view ->
            if (areasAdapter.getAreas().size == 10){ // limit number of areas to 10
                Snackbar.make(view, R.string.max_num_areas, Snackbar.LENGTH_LONG ).show()
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
        return root
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
}