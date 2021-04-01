package com.minirogue.holocanonrepository

import com.minirogue.starwarscanontracker.core.model.room.entity.MediaType
import javax.inject.Inject

class MediaTypeRepository @Inject constructor(/*private val localDataSource: MediaTypeLocalDataSource*/) {

    /**
     * TODO this should be replaced with a coroutine version of this function.
     */
    fun getAllTypes(): List<MediaType> = emptyList()//localDataSource.getAllMediaTypes()
}
