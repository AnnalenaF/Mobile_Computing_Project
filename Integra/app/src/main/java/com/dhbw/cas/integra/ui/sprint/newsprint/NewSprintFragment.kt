package com.dhbw.cas.integra.ui.sprint.newsprint

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.Areas
import com.dhbw.cas.integra.databinding.FragmentNewSprintBinding
import com.dhbw.cas.integra.ui.areas.AreasViewModel
import com.google.android.material.snackbar.Snackbar
import com.savvi.rangedatepicker.CalendarPickerView
import kotlinx.android.synthetic.main.fragment_new_sprint.*
import java.util.*

class NewSprintFragment: Fragment() {
    private lateinit var today: Date
    private lateinit var areasViewModel: AreasViewModel
    private lateinit var areasCapacityAdapter: AreasCapacityAdapter
    private var _binding: FragmentNewSprintBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        areasViewModel =
            ViewModelProvider(this).get(AreasViewModel::class.java)
        _binding = FragmentNewSprintBinding.inflate(inflater, container, false)
        val view = binding.root

        val recyclerView: RecyclerView = binding.newSprintAreas
        areasCapacityAdapter = AreasCapacityAdapter()
        recyclerView.adapter = areasCapacityAdapter
        areasViewModel.areas.observe(viewLifecycleOwner, { areas -> areasCapacityAdapter.setAreas(areas) })

        // instantiate calender to pick sprint duration
        val calendar = Calendar.getInstance()
        // clear time information for comparability of dates
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        today = calendar.time
        //default sprint duration is 2 weeks = 14 days
        calendar.add(Calendar.DAY_OF_YEAR, 13)
        val defaultSprintEnd = calendar.time
        //maximum sprint duration is 1 year = 365 days
        calendar.add(Calendar.DAY_OF_YEAR, 364)
        val maxDate = calendar.time
        val calendarView = binding.calendarSprintDuration
        calendarView.init(today, maxDate)
            .inMode(CalendarPickerView.SelectionMode.RANGE)
            .withSelectedDates(listOf<Date>(today, defaultSprintEnd))

        return view
    }

    // enable fragment to display options menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.continue_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // do not display settings during splint planning
    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_settings).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_continue -> { // continue to task assignment
                val selectedDates = calendar_sprint_duration.selectedDates
                when {
                    selectedDates[0] != today -> {
                        Snackbar.make(binding.root, R.string.sprint_start_error, Snackbar.LENGTH_LONG)
                            .setTextColor(ContextCompat.getColor(binding.root.context, R.color.red_error))
                            .show()
                    }
                    selectedDates.size < 5 -> {
                        Snackbar.make(binding.root, getString(R.string.sprint_length_error, 5),
                            Snackbar.LENGTH_LONG)
                            .setTextColor(ContextCompat.getColor(binding.root.context, R.color.red_error))
                            .show()
                    }
                    else -> {
                        val areasNew = areasCapacityAdapter.getAreas()
                        for (area in areasNew){
                            area.totalCapacity = area.totalCapacity!! * 60
                            area.remainingCapacity = area.totalCapacity
                        }
                        val areas = Areas()
                        areas.addAll(areasNew)
                        for (area in areas){
                            areasViewModel.updateArea(area)
                        }
                        val action =
                            NewSprintFragmentDirections.actionNavNewSprintToNavNewSprintTasks(
                                areas,
                                selectedDates[0].time,
                                selectedDates[selectedDates.size - 1].time
                            )
                        binding.root.findNavController().navigate(action)
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}