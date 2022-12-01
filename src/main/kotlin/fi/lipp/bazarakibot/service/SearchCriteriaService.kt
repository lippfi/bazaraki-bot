package fi.lipp.bazarakibot.service

import fi.lipp.bazarakibot.model.Advert
import fi.lipp.bazarakibot.model.Pets
import fi.lipp.bazarakibot.model.SearchCriteria

interface SearchCriteriaService {
  /** Checks if advert matches search criteria */
  fun matches(advert: Advert, searchCriteria: SearchCriteria): Boolean

  fun getSearchCriteria(chatId: Long): SearchCriteria
  fun getSearchCriteriaString(chatId: Long): String

  fun setMinPrice(chatId: Long, price: Int?)
  fun setMaxPrice(chatId: Long, price: Int?)
  fun setMinBedrooms(chatId: Long, bedrooms: Int?)
  fun setMaxBedrooms(chatId: Long, bedrooms: Int?)
  fun setMinBathrooms(chatId: Long, bathrooms: Int?)
  fun setMaxBathrooms(chatId: Long, bathrooms: Int?)
  fun setMinArea(chatId: Long, area: Int?)
  fun setMaxArea(chatId: Long, area: Int?)
  fun setPets(chatId: Long, pets: Pets?)
}