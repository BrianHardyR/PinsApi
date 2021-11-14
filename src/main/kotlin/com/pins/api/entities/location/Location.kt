package com.pins.api.entities.location

import com.pins.api.entities.Entity
import com.pins.api.utils.round
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node

@Node("Location")
data class Location(

    val lat: Double,
    val lon: Double,
    var description: String = "",
    var isIn: Location? = null,
    @Id
    var id: Long = "location-$lat-$lon".hashCode().toLong(),
) : Entity() {

    init {
        isIn = getParentLocation()
    }

    /**
     * @Example lat : 1.11111 : lon 2:22222
     */

    fun getParentLocation(location: Location = this): Location? {
        println("Getting parent location")
        // sanity checks
        val latString = "${location.lat}"
        val lonString = "${location.lon}"

        println("Child $latString - $lonString")

        if (!latString.contains(".", ignoreCase = true) || !lonString.contains(".", ignoreCase = true)) return null

        val latStringLength = latString.split(".")[1].length
        val lonStringLength = lonString.split(".")[1].length

        val length = if (latStringLength == lonStringLength) latStringLength
                    else if (latStringLength > lonStringLength) latStringLength
                    else lonStringLength

        println("Decimal length $latStringLength <> $lonStringLength <> $length")

        if (length <= 0) return null

        val newLat = location.lat.round(length - 1)
        val newLon = location.lon.round(length - 1)

        println("Child $newLat - $newLon")

        if (newLat == lat && newLon == lon) return null

        return Location(
            lat = newLat,
            lon = newLon
        )
    }

}