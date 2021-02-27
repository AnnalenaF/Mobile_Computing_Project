package com.dhbw.cas.integra.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.dhbw.cas.integra.MainActivity
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.Sprint
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment(), FinishSprintDialogFragment.FinishSprintDialogListener {

    private lateinit var sprintViewModel: SprintViewModel
    private lateinit var root: View
    private lateinit var menuItemFinishSprint: MenuItem
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPagerAdapter: TabsViewPagerAdapter
    private var activeSprint: Any? = Any()
    private var sprintActive: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sprintViewModel =
            ViewModelProvider(this).get(SprintViewModel::class.java)
        activeSprint = sprintViewModel.activeSprint
        sprintActive = activeSprint is Sprint

        if (sprintActive) {
            root = inflater.inflate(R.layout.fragment_sprint, container, false)

            // display current sprint date range
            val sprintDuration = root.findViewById<TextView>(R.id.sprint_daterange)
            val startDate = Date((activeSprint as Sprint).startDate)
            val startDateString: String =
                SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(startDate)
            val endDate = Date((activeSprint as Sprint).endDate)
            val endDateString: String =
                SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(endDate)
            val dateRange = "$startDateString - $endDateString"
            sprintDuration.text = getString(R.string.sprint_daterange_label, dateRange)

            // display current sprint time
            val calendar = Calendar.getInstance()
            // clear time information for comparability of dates
            calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val today: Date = calendar.time

            // calculate days of sprint date range
            var totalDays: Long = endDate.time - startDate.time
            totalDays = TimeUnit.MILLISECONDS.toDays(totalDays) + 1
            var passedDays: Long = today.time - startDate.time
            passedDays = TimeUnit.MILLISECONDS.toDays(passedDays) + 1
            // display days in textview
            val sprintProgressLabel = root.findViewById<TextView>(R.id.sprint_progress_label)
            sprintProgressLabel.text =
                getString(R.string.sprint_progress_label, passedDays, totalDays)
            // display days in progress bar
            val sprintProgress = root.findViewById<ProgressBar>(R.id.sprint_progress)
            sprintProgress.max = totalDays.toInt()
            sprintProgress.progress = passedDays.toInt()

            // create and assign adapter to tablayout and viewpager
            tabLayout = root.findViewById(R.id.tablayout_sprint)
            val viewPager = root.findViewById<ViewPager2>(R.id.viewpager_tabs)
            viewPagerAdapter = TabsViewPagerAdapter(requireActivity())
            viewPager.adapter = viewPagerAdapter
            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            // create tabs
            val tabBlocked: TabLayout.Tab = tabLayout.newTab()
            tabBlocked.text = getString(R.string.tab_blocked)
            tabLayout.addTab(tabBlocked)

            val tabOpen: TabLayout.Tab = tabLayout.newTab()
            tabOpen.text = getString(R.string.tab_open)
            tabLayout.addTab(tabOpen, true)

            val tabProcess: TabLayout.Tab = tabLayout.newTab()
            tabProcess.text = getString(R.string.tab_in_process)
            tabLayout.addTab(tabProcess)

            val tabDone: TabLayout.Tab = tabLayout.newTab()
            tabDone.text = getString(R.string.tab_done)
            tabLayout.addTab(tabDone)

            tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        } else {
            root = inflater.inflate(R.layout.fragment_no_active_sprint, container, false)

            val buttonPlanSprint = root.findViewById<Button>(R.id.button_plan_sprint)
            buttonPlanSprint.setOnClickListener {
                val action = HomeFragmentDirections.actionNavHomeToNavNewSprint()
                root.findNavController().navigate(action)
            }
        }

        return root
    }

    // instantiate tab fragment
    private inner class TabsViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = tabLayout.tabCount
        override fun createFragment(position: Int): Fragment = TabPageFragment(position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (sprintActive) {
            menuItemFinishSprint = menu.add(R.string.finish_sprint)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            (sprintActive) && (item.itemId == menuItemFinishSprint.itemId) -> { // finish sprint
                val dialogFrag = FinishSprintDialogFragment()
                dialogFrag.setTargetFragment(this, 1)
                dialogFrag.show(parentFragmentManager, "FinishSprintDialogFragment")
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    // finish sprint
    override fun onFinishDialogPositiveClick(dialog: DialogFragment, view: View) {
        val delOpen = view.findViewById<MaterialCheckBox>(R.id.checkbox_delete_open).isChecked
        val delProgress =
            dialog.dialog!!.findViewById<MaterialCheckBox>(R.id.checkbox_delete_in_process).isChecked
        val delDone =
            dialog.dialog!!.findViewById<MaterialCheckBox>(R.id.checkbox_delete_done).isChecked
        val delBlocked =
            dialog.dialog!!.findViewById<MaterialCheckBox>(R.id.checkbox_delete_blocked).isChecked

        val activeSprint = activeSprint as Sprint

        // delete tasks by selected states
        if (delOpen) {
            sprintViewModel.deleteSprintTasksByState(activeSprint.id, 0)
        }
        if (delProgress) {
            sprintViewModel.deleteSprintTasksByState(activeSprint.id, 1)
        }
        if (delDone) {
            sprintViewModel.deleteSprintTasksByState(activeSprint.id, 2)
        }
        if (delBlocked) {
            sprintViewModel.deleteSprintTasksByState(activeSprint.id, 3)
        }

        // reset state of tasks
        sprintViewModel.resetTaskStates()

        // delete Sprint (possible adjustment: in case sprint persistent is required: just remove active tag)
        sprintViewModel.deleteSprint(activeSprint)

        // restart activity
        val intent = Intent(root.context, MainActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
    }
}