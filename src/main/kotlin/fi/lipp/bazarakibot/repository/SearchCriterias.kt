package fi.lipp.bazarakibot.repository

import fi.lipp.bazarakibot.model.Pets
import org.jetbrains.exposed.dao.id.IntIdTable

object SearchCriterias : IntIdTable() {
    val minPrice = integer("min_price").nullable()
    val maxPrice = integer("max_price").nullable()

    val minBedrooms = integer("min_bedrooms").nullable()
    val maxBedrooms = integer("max_bedrooms").nullable()

    val minBathrooms = integer("min_bathrooms").nullable()
    val maxBathrooms = integer("max_bathrooms").nullable()

    val minArea = integer("min_area").nullable()
    val maxArea = integer("max_area").nullable()

    val pets = enumeration("pets", Pets::class).nullable()
    val location = text("location").nullable()
}