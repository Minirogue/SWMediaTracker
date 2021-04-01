package com.minirogue.usecase

import com.minirogue.holocanonrepository.MediaTypeRepository
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaType
import javax.inject.Inject

class GetAllMediaTypesUseCase @Inject constructor(private val mediaTypeRepository: MediaTypeRepository) {

    /**
     * WARNING: THIS IS NOT THREAD SAFE.
     */
    operator fun invoke(): List<MediaType> = emptyList() //mediaTypeRepository.getAllTypes()
}
