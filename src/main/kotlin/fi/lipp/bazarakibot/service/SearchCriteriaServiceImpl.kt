package fi.lipp.bazarakibot.service

import fi.lipp.bazarakibot.constants.SearchCriteriaConstants
import fi.lipp.bazarakibot.model.Advert
import fi.lipp.bazarakibot.model.Pets
import fi.lipp.bazarakibot.model.SearchCriteria
import fi.lipp.bazarakibot.model.User
import fi.lipp.bazarakibot.repository.Users
import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction

class SearchCriteriaServiceImpl : SearchCriteriaService {
  private val logger = KotlinLogging.logger {}

  override fun getSearchCriteriaString(chatId: Long): String {
    val searchCriteriaString = getSearchCriteria(chatId).toString()
    if (searchCriteriaString.isNotEmpty()) {
      return searchCriteriaString
    }
    return SearchCriteriaConstants.filterIsEmpty
  }

  override fun matches(advert: Advert, searchCriteria: SearchCriteria): Boolean {
    if (searchCriteria.minPrice != null && searchCriteria.minPrice!! > advert.price) {
      return false
    }
    if (searchCriteria.maxPrice != null && searchCriteria.maxPrice!! < advert.price) {
      return false
    }
    if (searchCriteria.location.isNotEmpty() && !searchCriteria.location.contains(advert.location)) {
      return false
    }
    if (searchCriteria.minBedrooms != null && advert.bedrooms != null && searchCriteria.minBedrooms!! > advert.bedrooms) {
      return false
    }
    if (searchCriteria.maxBedrooms != null && advert.bedrooms != null && searchCriteria.maxBedrooms!! < advert.bedrooms) {
      return false
    }
    if (searchCriteria.minBathrooms != null && advert.bathrooms != null && searchCriteria.minBathrooms!! > advert.bathrooms) {
      return false
    }
    if (searchCriteria.maxBathrooms != null && advert.bathrooms != null && searchCriteria.maxBathrooms!! < advert.bathrooms) {
      return false
    }
    if (searchCriteria.pets != null && advert.pets != null && searchCriteria.pets != advert.pets) {
      return false
    }
    if (searchCriteria.minArea != null && advert.area != null && searchCriteria.minArea!! > advert.area) {
      return false
    }
    if (searchCriteria.maxArea != null && advert.area != null && searchCriteria.maxArea!! < advert.area) {
      return false
    }
    return true
  }

  override fun setMinPrice(chatId: Long, price: Int?) {
    val searchCriteria = getSearchCriteria(chatId)
    transaction {
      searchCriteria.minPrice = price
    }
  }

  override fun setMaxPrice(chatId: Long, price: Int?) {
    val searchCriteria = getSearchCriteria(chatId)
    transaction {
      searchCriteria.maxPrice = price
    }
  }

  override fun setMinBedrooms(chatId: Long, bedrooms: Int?) {
    val searchCriteria = getSearchCriteria(chatId)
    transaction {
      searchCriteria.minBedrooms = bedrooms
    }
  }

  override fun setMaxBedrooms(chatId: Long, bedrooms: Int?) {
    val searchCriteria = getSearchCriteria(chatId)
    transaction {
      searchCriteria.maxBedrooms = bedrooms
    }
  }

  override fun setMinBathrooms(chatId: Long, bathrooms: Int?) {
    val searchCriteria = getSearchCriteria(chatId)
    transaction {
      searchCriteria.minBathrooms = bathrooms
    }
  }

  override fun setMaxBathrooms(chatId: Long, bathrooms: Int?) {
    val searchCriteria = getSearchCriteria(chatId)
    transaction {
      searchCriteria.maxBathrooms = bathrooms
    }
  }

  override fun setMinArea(chatId: Long, area: Int?) {
    val searchCriteria = getSearchCriteria(chatId)
    transaction {
      searchCriteria.minArea = area
    }
  }

  override fun setMaxArea(chatId: Long, area: Int?) {
    val searchCriteria = getSearchCriteria(chatId)
    transaction {
      searchCriteria.maxArea = area
    }
  }

  override fun setPets(chatId: Long, pets: Pets?) {
    val searchCriteria = getSearchCriteria(chatId)
    transaction {
      searchCriteria.pets = pets
    }
  }

  override fun getSearchCriteria(chatId: Long): SearchCriteria {
    val searchCriteria = transaction {
      SearchCriteria.findById(
        User.find { Users.chatId eq chatId }.first().searchCriteria
      )
    }
    if (searchCriteria == null) {
      logger.error("Failed to find search criteria for user $chatId")
      throw RuntimeException()
    }
    return searchCriteria
  }
}