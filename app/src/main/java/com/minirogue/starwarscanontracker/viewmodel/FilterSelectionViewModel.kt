package com.minirogue.starwarscanontracker.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.minirogue.starwarscanontracker.model.PrefsRepo
import com.minirogue.starwarscanontracker.model.repository.SWMRepository
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.model.room.pojo.FullFilter


class FilterSelectionViewModel @ViewModelInject constructor(private val repository: SWMRepository,
                                                            prefsRepo: PrefsRepo) : ViewModel() {


    val filterTypes = repository.getAllFilterTypes()
    val checkBoxVisibilty = prefsRepo.checkBoxVisibility


    fun flipFilterType(filterType: FilterType) {
        filterType.isFilterPositive = !filterType.isFilterPositive
        repository.update(filterType)
    }

    fun flipFilterActive(filterObject: FilterObject) {
        filterObject.active = !filterObject.active
        repository.update(filterObject)
    }
    fun setFilterInactive(filterObject: FilterObject){
        filterObject.active = false
        repository.update(filterObject)
    }

    fun getFiltersOfType(filterType: FilterType) = Transformations.map(repository.getFiltersOfType(filterType.typeId)) { filterList -> filterList.map { FullFilter(it, filterType.isFilterPositive) } }

    fun getActiveFilters() = repository.getActiveFilters()

    fun deactivateFilter(filterObject: FilterObject){
        filterObject.active = false
        repository.update(filterObject)
    }

}
