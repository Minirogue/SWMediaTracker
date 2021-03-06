package com.minirogue.starwarscanontracker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.minirogue.starwarscanontracker.databinding.FragmentFilterSelectionBinding
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.core.model.room.pojo.FullFilter
import com.minirogue.starwarscanontracker.view.FilterChip
import com.minirogue.starwarscanontracker.view.adapter.FilterSelectionAdapter
import com.minirogue.starwarscanontracker.viewmodel.FilterSelectionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterSelectionFragment : Fragment() {

    private val viewModel: FilterSelectionViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentBinding = FragmentFilterSelectionBinding.inflate(inflater, container, false)
        val fragmentContext = fragmentBinding.root.context

        val recyclerView = fragmentBinding.generalRecyclerview

        val activeChipGroup = fragmentBinding.selectedChipgroup

        viewModel.getActiveFilters().asLiveData(lifecycleScope.coroutineContext).observe(viewLifecycleOwner,
            { activeFilters ->
                activeChipGroup.removeAllViews(); activeFilters.forEach {
                activeChipGroup.addView(makeCurrentFilterChip(it))
            }
            })

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(fragmentContext)
        val adapter = FilterSelectionAdapter()
        adapter.setOnClickListeners(object : FilterSelectionAdapter.OnClickListeners {
            override fun onFilterClicked(filterObject: FilterObject) {
                viewModel.flipFilterActive(filterObject)
            }

            override fun setFilterGroupObservation(chipGroup: ChipGroup, filterType: FilterType) {
                viewModel.getFiltersOfType(filterType).asLiveData(lifecycleScope.coroutineContext).observe(
                    viewLifecycleOwner,
                    { filters ->
                        chipGroup.removeAllViews()
                        filters.forEach {
                            if (!it.filterObject.active) {
                                val filterChip = FilterChip(it, fragmentContext)
                                val filterObject = it.filterObject
                                filterChip.setOnClickListener { viewModel.flipFilterActive(filterObject) }
                                filterChip.setOnCloseIconClickListener { viewModel.setFilterInactive(filterObject) }
                                chipGroup.addView(filterChip)
                            }
                        }
                    })
            }

            override fun onFilterTypeSwitchClicked(filterType: FilterType) {
                viewModel.flipFilterType(filterType)
            }
        })
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = mLayoutManager

        viewModel.checkBoxVisibility.observe(viewLifecycleOwner, { visibility ->
            val list = ArrayList<Int>()
            if (!visibility[0]) {
                list.add(FilterType.FILTERCOLUMN_CHECKBOX_ONE)
            }
            if (!visibility[1]) {
                list.add(FilterType.FILTERCOLUMN_CHECKBOX_TWO)
            }
            if (!visibility[2]) {
                list.add(FilterType.FILTERCOLUMN_CHECKBOX_THREE)
            }
            adapter.updateExcludedTypes(list)
        })
        viewModel.filterTypes.asLiveData(lifecycleScope.coroutineContext).observe(viewLifecycleOwner,
            { filterTypes -> adapter.updateList(filterTypes) })

        return fragmentBinding.root
    }

    private fun makeCurrentFilterChip(fullFilter: FullFilter): Chip {
        val filterChip: Chip = FilterChip(fullFilter, requireView().context)
        filterChip.setOnCloseIconClickListener { viewModel.deactivateFilter(fullFilter.filterObject) }
        return filterChip
    }
}
