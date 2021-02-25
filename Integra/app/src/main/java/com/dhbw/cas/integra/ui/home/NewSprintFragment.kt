package com.dhbw.cas.integra.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.Areas
import com.dhbw.cas.integra.ui.areas.AreasViewModel
import com.google.android.material.snackbar.Snackbar
import com.savvi.rangedatepicker.CalendarPickerView
import kotlinx.android.synthetic.main.fragment_new_sprint.*
import java.util.*

class NewSprintFragment: Fragment() {
    private lateinit var root: View
    private lateinit var today: Date
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var areasCapacityAdapter: AreasCapacityAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_new_sprint, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.new_sprint_areas)
        areasCapacityAdapter = AreasCapacityAdapter()
        recyclerView.adapter = areasCapacityAdapter
        homeViewModel.areas.observe(viewLifecycleOwner, { areas -> areasCapacityAdapter.setAreas(areas) })


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
        val calendarView = root.findViewById<CalendarPickerView>(R.id.calendar_sprint_duration)
        calendarView.init(today, maxDate)
            .inMode(CalendarPickerView.SelectionMode.RANGE)
            .withSelectedDates(listOf<Date>(today, defaultSprintEnd))

        return root
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
                if (selectedDates[0] != today){
                    Snackbar.make(root, R.string.sprint_start_error, Snackbar.LENGTH_LONG)
                        .setTextColor(ContextCompat.getColor(root.context, R.color.red_error))
                        .show()
                } else if (selectedDates.size < 5){
                    Snackbar.make(root, getString(R.string.sprint_length_error, 5),
                                  Snackbar.LENGTH_LONG)
                        .setTextColor(ContextCompat.getColor(root.context, R.color.red_error))
                        .show()
                } else {
                    val areasNew = areasCapacityAdapter.getAreas()
                    val areas = Areas()
                    areas.addAll(areasNew)
                    val action = NewSprintFragmentDirections.actionNavNewSprintToNavNewSprintTasks(
                        areas, selectedDates[0].time, selectedDates[selectedDates.size-1].time)
                    root.findNavController().navigate(action)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}