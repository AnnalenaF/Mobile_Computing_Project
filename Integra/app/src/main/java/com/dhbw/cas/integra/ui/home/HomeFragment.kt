package com.dhbw.cas.integra.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.dhbw.cas.integra.R
import com.dhbw.cas.integra.data.SprintWithTasks
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var root: View
    private lateinit var tabLayout: TabLayout
    private var activeSprint: Any? = Any()
    private var sprintActive: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        activeSprint = homeViewModel.activeSprint
        sprintActive = activeSprint is SprintWithTasks

        if (sprintActive) {
            root = inflater.inflate(R.layout.fragment_sprint, container, false)

            // create tabs
            tabLayout = root.findViewById(R.id.tablayout_sprint)
            val viewPager = root.findViewById<ViewPager2>(R.id.viewpager_tabs)
            val tabBlocked: TabLayout.Tab = tabLayout.newTab()
            tabBlocked.text = getString(R.string.tab_blocked)
            tabLayout.addTab(tabBlocked)

            val tabOpen: TabLayout.Tab = tabLayout.newTab()
            tabOpen.text = getString(R.string.tab_open)
            tabLayout.addTab(tabOpen)

            val tabProcess: TabLayout.Tab = tabLayout.newTab()
            tabProcess.text = getString(R.string.tab_in_process)
            tabLayout.addTab(tabProcess, true)

            val tabDone: TabLayout.Tab = tabLayout.newTab()
            tabDone.text = getString(R.string.tab_done)
            tabLayout.addTab(tabDone)

            tabLayout.tabGravity = TabLayout.GRAVITY_FILL

            // create and assign adapter to tablayout and viewpager
            val viewPagerAdapter = TabsViewPagerAdapter(requireActivity())
            viewPager.adapter = viewPagerAdapter
            viewPager.currentItem = 1
            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

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
        if (sprintActive){
            val menuItemFinishSprint: MenuItem
            menuItemFinishSprint = menu.add(R.string.finish_sprint)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }
}