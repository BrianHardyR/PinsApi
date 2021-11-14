package com.pins.api.service

import com.pins.api.entities.location.Location
import com.pins.api.repository.LocationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class LocationService {

    @Autowired lateinit var locationRepository: LocationRepository


    @Transactional
    fun saveLocation(location: Location):Location{
        return locationRepository.save(location)
    }

}