package com.minirogue.holocanonlocaldata

import com.minirogue.starwarscanontracker.core.model.room.entity.MediaType
import javax.inject.Inject

class MediaTypeLocalDataSource @Inject constructor(/*private val daoType: DaoType*/) {
    fun getAllMediaTypes(): List<MediaType> = emptyList() // daoType.allNonLive
}
