package fi.lipp.bazarakibot.model

import fi.lipp.bazarakibot.constants.SearchCriteriaConstants
import fi.lipp.bazarakibot.repository.SearchCriterias
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class SearchCriteria(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<SearchCriteria>(SearchCriterias)
    var minPrice by SearchCriterias.minPrice
    var maxPrice by SearchCriterias.maxPrice
    var minBedrooms by SearchCriterias.minBedrooms
    var maxBedrooms by SearchCriterias.maxBedrooms
    var minBathrooms by SearchCriterias.minBathrooms
    var maxBathrooms by SearchCriterias.maxBathrooms
    var minArea by SearchCriterias.minArea
    var maxArea by SearchCriterias.maxArea

    var pets by SearchCriterias.pets

    var location: Set<Location> by SearchCriterias.location.transform(
        {
            it.joinToString(separator = ",")
        },
        { locations ->
            locations?.split(",")
            ?.mapNotNull { location -> Location.values().firstOrNull { value -> location == value.toString()  } }
            ?.toSet()
            ?: setOf()
        }
    )

    override fun toString(): String {
        val result = StringBuilder()
        if (minPrice != null) result.append(SearchCriteriaConstants.minPrice).append(": ").append(minPrice).append("\n")
        if (maxPrice != null) result.append(SearchCriteriaConstants.maxPrice).append(": ").append(maxPrice).append("\n")
        if (minBedrooms != null) result.append(SearchCriteriaConstants.minBedrooms).append(": ").append(minBedrooms).append("\n")
        if (maxBedrooms != null) result.append(SearchCriteriaConstants.maxBedrooms).append(": ").append(maxBedrooms).append("\n")
        if (minBathrooms != null) result.append(SearchCriteriaConstants.minBathrooms).append(": ").append(minBathrooms).append("\n")
        if (maxBathrooms != null) result.append(SearchCriteriaConstants.maxBathrooms).append(": ").append(maxBathrooms).append("\n")
        if (minArea != null) result.append(SearchCriteriaConstants.minArea).append(": ").append(minArea).append("\n")
        if (maxArea != null) result.append(SearchCriteriaConstants.maxArea).append(": ").append(maxArea).append("\n")
        if (pets != null) {
            result
                .append(SearchCriteriaConstants.pets).append(": ")
                .append(when (pets) {
                    Pets.ALLOWED -> SearchCriteriaConstants.petsAllowed
                    Pets.NOT_ALLOWED -> SearchCriteriaConstants.petsNotAllowed
                    else -> throw ConcurrentModificationException()
                })
                .append("\n")
        }
        if (location.size != 10) {
            if (location.isEmpty()) {
                result.append(SearchCriteriaConstants.noLocationIsSelected).append("\n")
            } else {
                result.append(SearchCriteriaConstants.location).append(": ")
                    .append(location.joinToString(separator = ", ")).append("\n")
            }
        }

        return result.toString()
    }
}
