package com.minirogue.starwarscanontracker.model.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.model.room.pojo.FullFilter

@Dao
interface DaoFilter {


    //FilterTypes:
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(filterType: FilterType)

    @Update
    fun update(filterType: FilterType)

    @Query("SELECT * FROM filter_object")
    fun getAllFilters(): LiveData<List<FilterObject>>

//    @Query("SELECT * FROM filter_object WHERE is_active=1")
//    fun getActiveFilters(): LiveData<List<FilterObject>>

    @Query("SELECT * FROM filter_object WHERE type_id=:typeId")
    fun getFiltersWithType(typeId: Int): LiveData<List<FilterObject>>


    //FilterObjects:
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(filterObject: FilterObject)

    @Update
    fun update(filterObject: FilterObject)

    @Query("SELECT * FROM filter_type")
    fun getAllFilterTypes(): LiveData<List<FilterType>>

    @Query("SELECT * FROM filter_type")
    fun getAllFilterTypesNonLive(): List<FilterType>

    @Query("SELECT * FROM filter_type WHERE id=:id LIMIT 1")
    fun getFilterType(id: Int): FilterType

    //FullFilter
    @Query("SELECT filter_object.*,filter_type.is_positive FROM filter_object INNER JOIN filter_type ON filter_object.type_id = filter_type.id WHERE filter_object.is_active = 1")
    fun getActiveFilters(): LiveData<List<FullFilter>>

}