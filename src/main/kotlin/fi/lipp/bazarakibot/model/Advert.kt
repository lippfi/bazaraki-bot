package fi.lipp.bazarakibot.model

import fi.lipp.bazarakibot.constants.SearchCriteriaConstants
import java.time.LocalDateTime

data class Advert(
  val url: String,
  val publicationTime: LocalDateTime,
  val price: Int,
  val location: Location?,
  val bedrooms: Int?,
  val bathrooms: Int?,
  val pets: Pets?,
  val area: Int?,
    )

class AdvertBuilder(private val url: String, private val publicationTime: LocalDateTime, private val price: Int) {
    private var location: Location? = null
    private var bedrooms: Int? = null
    private var bathrooms: Int? = null
    private var pets: Pets? = null
    private var area: Int? = null

    fun build(): Advert {
        return Advert(url, publicationTime, price, location, bedrooms, bathrooms, pets, area)
    }

    fun location(location: Location): AdvertBuilder {
        this.location = location
        return this
    }

    fun bedrooms(bedrooms: Int): AdvertBuilder {
        this.bedrooms = bedrooms
        return this
    }

    fun bathrooms(bathrooms: Int): AdvertBuilder {
        this.bathrooms = bathrooms
        return this
    }

    fun pets(pets: Pets): AdvertBuilder {
        this.pets = pets
        return this
    }

    fun area(area: Int): AdvertBuilder {
        this.area = area
        return this
    }
}

enum class Location(private val string: String) {
    PAPHOS(SearchCriteriaConstants.paphos),
    PAPHOS_DISTRICT(SearchCriteriaConstants.paphosDistrict),
    LIMASSOL(SearchCriteriaConstants.limassol),
    LIMASSOL_DISTRICT(SearchCriteriaConstants.limassolDistrict),
    NICOSIA(SearchCriteriaConstants.nicosia),
    NICOSIA_DISTRICT(SearchCriteriaConstants.nicosiaDistrict),
    LARNACA(SearchCriteriaConstants.larnaca),
    LARNACA_DISTRICT(SearchCriteriaConstants.larnacaDistrict),
    FAMAGUSTA(SearchCriteriaConstants.famagusta),
    FAMAGUSTA_DISTRICT(SearchCriteriaConstants.famagustaDistrict);

    override fun toString(): String {
        return string
    }
}

enum class Pets {
    ALLOWED,
    NOT_ALLOWED;
}
