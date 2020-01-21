package com.minirogue.starwarscanontracker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.minirogue.starwarscanontracker.R


class TabbedListContainerFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tabbed_list_container, container, false)
        // Setting ViewPager for each Tabs
        val viewPager = view.findViewById(R.id.viewpager) as ViewPager
        setupViewPager(viewPager)
        // Set Tabs inside Toolbar
        val tabs = view.findViewById(R.id.result_tabs) as TabLayout
        tabs.setupWithViewPager(viewPager)


        return view
    }




    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager) {


        val adapter = MyAdapter(childFragmentManager)
        adapter.addFragment(HomeFragment(), "Home")
        adapter.addFragment(MediaListFragment(), "Canon List")
        adapter.addFragment(FilterSelectionFragment(), "Filters")
        //adapter.addFragment(SettingsFragment(), "Settings")
        viewPager.adapter = adapter


    }

    internal class MyAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

}