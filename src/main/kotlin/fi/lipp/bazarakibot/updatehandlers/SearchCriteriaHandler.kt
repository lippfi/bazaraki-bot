package fi.lipp.bazarakibot.updatehandlers

import fi.lipp.bazarakibot.model.Location
import fi.lipp.bazarakibot.constants.MessageConstants
import fi.lipp.bazarakibot.constants.SearchCriteriaConstants
import fi.lipp.bazarakibot.model.Pets
import fi.lipp.bazarakibot.service.MessageService
import fi.lipp.bazarakibot.service.SearchCriteriaService
import org.jetbrains.exposed.sql.transactions.transaction
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

class SearchCriteriaHandler(private val messageService: MessageService, private val searchCriteriaService: SearchCriteriaService): UpdateHandler {
  private val userToState = mutableMapOf<Long, State?>()
  enum class State {
    SETTING_MIN_PRICE,
    SETTING_MAX_PRICE,
    SETTING_MIN_BEDROOMS,
    SETTING_MAX_BEDROOMS,
    SETTING_MIN_BATHROOMS,
    SETTING_MAX_BATHROOMS,
    SETTING_MIN_AREA,
    SETTING_MAX_AREA,
    SETTING_PETS,
    SETTING_LOCATION,
  }

  override fun handleUpdate(update: Update): Boolean {
     val message = SendMessage()
    if (update.hasCallbackQuery()) {
      val chatId = update.callbackQuery.message.chatId
      message.chatId = chatId.toString()

      var sendMessage = true
      when (update.callbackQuery.data) {
        "set_min_price" -> {
          userToState[chatId] = State.SETTING_MIN_PRICE
          message.text = MessageConstants.setMinPrice
          addExitButton(message)
        }
        "set_max_price" -> {
          userToState[chatId] = State.SETTING_MAX_PRICE
          message.text = MessageConstants.setMaxPrice
          addExitButton(message)
        }
        "set_min_bedrooms" -> {
          userToState[chatId] = State.SETTING_MIN_BEDROOMS
          message.text = MessageConstants.setMinBedrooms
          addExitButton(message)
        }
        "set_max_bedrooms" -> {
          userToState[chatId] = State.SETTING_MAX_BEDROOMS
          message.text = MessageConstants.setMaxBedrooms
          addExitButton(message)
        }
        "set_min_bathrooms" -> {
          userToState[chatId] = State.SETTING_MIN_BATHROOMS
          message.text = MessageConstants.setMinBathrooms
          addExitButton(message)
        }
        "set_max_bathrooms" -> {
          userToState[chatId] = State.SETTING_MAX_BATHROOMS
          message.text = MessageConstants.setMaxBathrooms
          addExitButton(message)
        }
        "set_min_area" -> {
          userToState[chatId] = State.SETTING_MIN_AREA
          message.text = MessageConstants.setMinArea
          addExitButton(message)
        }
        "set_max_area" -> {
          userToState[chatId] = State.SETTING_MAX_AREA
          message.text = MessageConstants.setMaxArea
          addExitButton(message)
        }
        "set_pets" -> {
          userToState[chatId] = State.SETTING_PETS
          createSetPetMessage(message)
        }
        "set_pets_allowed" -> {
          userToState.remove(chatId)
          searchCriteriaService.setPets(chatId, Pets.ALLOWED)
          message.text = MessageConstants.petsIsSet + "\n\n" + SearchCriteriaConstants.currentCriteria + "\n" + searchCriteriaService.getSearchCriteriaString(chatId)
          addEditingButtons(message)
        }
        "set_pets_not_allowed" -> {
          userToState.remove(chatId)
          searchCriteriaService.setPets(chatId, Pets.NOT_ALLOWED)
          message.text = MessageConstants.petsIsSet + "\n\n" + SearchCriteriaConstants.currentCriteria + "\n" + searchCriteriaService.getSearchCriteriaString(chatId)
          addEditingButtons(message)
        }
        "set_pets_any" -> {
          userToState.remove(chatId)
          searchCriteriaService.setPets(chatId, null)
          message.text = MessageConstants.petsIsReset + "\n\n" + SearchCriteriaConstants.currentCriteria + "\n" + searchCriteriaService.getSearchCriteriaString(chatId)
          addEditingButtons(message)
        }
        "set_location" -> {
          val searchCriteria = searchCriteriaService.getSearchCriteria(chatId)
          if (searchCriteria.location.size == 10) {
            transaction {
              searchCriteria.location = emptySet()
            }
          }
          userToState[chatId] = State.SETTING_LOCATION
          createSetLocationMessage(chatId, message, true)
        }
        "set_location_all" -> {
          selectAllLocations(chatId, message, update)
          sendMessage = false
        }
        "set_location_paphos" -> {
          toggleLocation(chatId, message, update, Location.PAPHOS)
          sendMessage = false
        }
        "set_location_paphos_district" -> {
          toggleLocation(chatId, message, update, Location.PAPHOS_DISTRICT)
          sendMessage = false
        }
        "set_location_limassol" -> {
          toggleLocation(chatId, message, update, Location.LIMASSOL)
          sendMessage = false
        }
        "set_location_limassol_district" -> {
          toggleLocation(chatId, message, update, Location.LIMASSOL_DISTRICT)
          sendMessage = false
        }
        "set_location_nicosia" -> {
          toggleLocation(chatId, message, update, Location.NICOSIA)
          sendMessage = false
        }
        "set_location_nicosia_district" -> {
          toggleLocation(chatId, message, update, Location.NICOSIA_DISTRICT)
          sendMessage = false
        }
        "set_location_larnaca" -> {
          toggleLocation(chatId, message, update, Location.LARNACA)
          sendMessage = false
        }
        "set_location_larnaca_district" -> {
          toggleLocation(chatId, message, update, Location.LARNACA_DISTRICT)
          sendMessage = false
        }
        "set_location_famagusta" -> {
          toggleLocation(chatId, message, update, Location.FAMAGUSTA)
          sendMessage = false
        }
        "set_location_famagusta_district" -> {
          toggleLocation(chatId, message, update, Location.FAMAGUSTA_DISTRICT)
          sendMessage = false
        }
        "set_location_done" -> {
          userToState.remove(chatId)
          message.text = MessageConstants.locationIsSet + "\n\n" + SearchCriteriaConstants.currentCriteria + "\n" + searchCriteriaService.getSearchCriteriaString(chatId)
          addEditingButtons(message)
        }
        "reset_filter" -> {
          when (userToState[chatId]) {
            State.SETTING_MIN_PRICE -> searchCriteriaService.setMinPrice(chatId, null)
            State.SETTING_MAX_PRICE -> searchCriteriaService.setMaxPrice(chatId, null)
            State.SETTING_MIN_BEDROOMS -> searchCriteriaService.setMinBedrooms(chatId, null)
            State.SETTING_MAX_BEDROOMS -> searchCriteriaService.setMaxBedrooms(chatId, null)
            State.SETTING_MIN_BATHROOMS -> searchCriteriaService.setMinBathrooms(chatId, null)
            State.SETTING_MAX_BATHROOMS -> searchCriteriaService.setMaxBathrooms(chatId, null)
            State.SETTING_MIN_AREA -> searchCriteriaService.setMinArea(chatId, null)
            State.SETTING_MAX_AREA -> searchCriteriaService.setMaxArea(chatId, null)
            State.SETTING_PETS -> searchCriteriaService.setPets(chatId, null)
            State.SETTING_LOCATION -> {
              val searchCriteria = searchCriteriaService.getSearchCriteria(chatId)
              transaction {
                searchCriteria.location = setOf(Location.PAPHOS, Location.PAPHOS_DISTRICT, Location.LIMASSOL, Location.LIMASSOL_DISTRICT, Location.NICOSIA, Location.NICOSIA_DISTRICT, Location.LARNACA, Location.LARNACA_DISTRICT, Location.FAMAGUSTA, Location.FAMAGUSTA_DISTRICT)
              }
            }
            else -> {
            }
          }
          message.text = MessageConstants.criterionIsReset + "\n\n" + SearchCriteriaConstants.currentCriteria + "\n" + searchCriteriaService.getSearchCriteriaString(chatId)
          userToState.remove(chatId)
        }

        "exit" -> {
          userToState.remove(chatId)
          message.text = MessageConstants.criteriaIsSet
        }
        else -> return false
      }
      messageService.answerCallback(update.callbackQuery)

      if (sendMessage) {
        messageService.message(message)
      }
      return true
    }

    if (update.hasMessage()) {
      val chatId = update.message.chatId
      message.chatId = chatId.toString()

      val messageText = update.message.text

      if (messageText == "/edit") {
        message.text = MessageConstants.editSearchCriteria + "\n\n" +
                SearchCriteriaConstants.currentCriteria + ":\n" +
                searchCriteriaService.getSearchCriteriaString(chatId)
        addEditingButtons(message)
        messageService.message(message)
        return true
      }

      when (userToState[chatId]) {
        State.SETTING_MIN_PRICE -> {
          setIntValue(chatId, message, messageText, MessageConstants.minPriceIsSet + "\n\n")
          { price -> searchCriteriaService.setMinPrice(chatId, price) }
        }
        State.SETTING_MAX_PRICE -> {
          setIntValue(chatId, message, messageText, MessageConstants.maxPriceIsSet + "\n\n")
          { price -> searchCriteriaService.setMaxPrice(chatId, price) }
        }
        State.SETTING_MIN_BEDROOMS -> {
          setIntValue(chatId, message, messageText, MessageConstants.minBedroomsIsSet + "\n\n")
          { price -> searchCriteriaService.setMinBedrooms(chatId, price) }
        }
        State.SETTING_MAX_BEDROOMS -> {
          setIntValue(chatId, message, messageText, MessageConstants.maxBedroomsIsSet + "\n\n")
          { price -> searchCriteriaService.setMaxBedrooms(chatId, price) }
        }
        State.SETTING_MIN_BATHROOMS -> {
          setIntValue(chatId, message, messageText, MessageConstants.minBathroomsIsSet + "\n\n")
          { price -> searchCriteriaService.setMinBathrooms(chatId, price) }
        }
        State.SETTING_MAX_BATHROOMS -> {
          setIntValue(chatId, message, messageText, MessageConstants.maxBathroomsIsSet + "\n\n")
          { price -> searchCriteriaService.setMaxBathrooms(chatId, price) }
        }
        State.SETTING_MIN_AREA -> {
          setIntValue(chatId, message, messageText, MessageConstants.minAreaIsSet + "\n\n")
          { price -> searchCriteriaService.setMinArea(chatId, price) }
        }
        State.SETTING_MAX_AREA -> {
          setIntValue(chatId, message, messageText, MessageConstants.maxAreaIsSet + "\n\n")
          { price -> searchCriteriaService.setMaxArea(chatId, price) }
        }
        State.SETTING_PETS -> {
          createSetPetMessage(message)
        }
        else -> return false
      }

      messageService.message(message)
      return true
    }
    return false
  }

  private fun selectAllLocations(chatId: Long, message: SendMessage, update: Update) {
    val searchCriteria = searchCriteriaService.getSearchCriteria(chatId)
    transaction {
      searchCriteria.location = setOf(Location.PAPHOS, Location.PAPHOS_DISTRICT, Location.LIMASSOL, Location.LIMASSOL_DISTRICT, Location.NICOSIA, Location.NICOSIA_DISTRICT, Location.LARNACA, Location.LARNACA_DISTRICT, Location.FAMAGUSTA, Location.FAMAGUSTA_DISTRICT)
    }
    updateSetLocationMessage(chatId, message, update)
  }

  private fun toggleLocation(chatId: Long, message: SendMessage, update: Update, location: Location) {
    val searchCriteria = searchCriteriaService.getSearchCriteria(chatId)
    transaction {
      if (searchCriteria.location.contains(location)) {
        searchCriteria.location = searchCriteria.location - location
      } else {
        searchCriteria.location = searchCriteria.location + location
      }
    }
    updateSetLocationMessage(chatId, message, update)
  }

  private fun updateSetLocationMessage(chatId: Long, message: SendMessage, update: Update) {
    createSetLocationMessage(chatId, message, true)
    val editMessage = EditMessageReplyMarkup()
    editMessage.chatId = chatId.toString()
    editMessage.replyMarkup = message.replyMarkup as InlineKeyboardMarkup
    editMessage.messageId = update.callbackQuery.message.messageId
    messageService.editMessage(editMessage)
  }

  private fun setIntValue(chatId: Long, message: SendMessage, value: String, response: String, settingFunction: (Int) -> Unit) {
    val intValue = value.toIntOrNull()
    if (intValue == null) {
      message.text = MessageConstants.failedToParseNumber
    } else {
      settingFunction(intValue)
      message.text = response + SearchCriteriaConstants.currentCriteria + "\n" +
          searchCriteriaService.getSearchCriteriaString(chatId)
      addEditingButtons(message)
      userToState.remove(chatId)
    }
  }

  private fun createSetLocationMessage(chatId: Long, message: SendMessage, addExitButton: Boolean = true) {
    message.text = MessageConstants.setLocation
    val currentLocations = searchCriteriaService.getSearchCriteria(chatId).location

    val paphos = InlineKeyboardButton(buildHasString(Location.PAPHOS, currentLocations) + " " + SearchCriteriaConstants.paphos)
    paphos.callbackData = "set_location_paphos"
    val paphosDistrict = InlineKeyboardButton(buildHasString(Location.PAPHOS_DISTRICT, currentLocations) + " " + SearchCriteriaConstants.paphosDistrict)
    paphosDistrict.callbackData = "set_location_paphos_district"

    val limassol = InlineKeyboardButton(buildHasString(Location.LIMASSOL, currentLocations) + " " + SearchCriteriaConstants.limassol)
    limassol.callbackData = "set_location_limassol"
    val limassolDistrict = InlineKeyboardButton(buildHasString(Location.LIMASSOL_DISTRICT, currentLocations) + " " + SearchCriteriaConstants.limassolDistrict)
    limassolDistrict.callbackData = "set_location_limassol_district"

    val nicosia = InlineKeyboardButton(buildHasString(Location.NICOSIA, currentLocations) + " " + SearchCriteriaConstants.nicosia)
    nicosia.callbackData = "set_location_nicosia"
    val nicosiaDistrict = InlineKeyboardButton(buildHasString(Location.NICOSIA_DISTRICT, currentLocations) + " " + SearchCriteriaConstants.nicosiaDistrict)
    nicosiaDistrict.callbackData = "set_location_nicosia_district"

    val larnaca = InlineKeyboardButton(buildHasString(Location.LARNACA, currentLocations) + " " + SearchCriteriaConstants.larnaca)
    larnaca.callbackData = "set_location_larnaca"
    val larnacaDistrict = InlineKeyboardButton(buildHasString(Location.LARNACA_DISTRICT, currentLocations) + " " + SearchCriteriaConstants.larnacaDistrict)
    larnacaDistrict.callbackData = "set_location_larnaca_district"

    val famagusta = InlineKeyboardButton(buildHasString(Location.FAMAGUSTA, currentLocations) + " " + SearchCriteriaConstants.famagusta)
    famagusta.callbackData = "set_location_famagusta"
    val famagustaDistrict = InlineKeyboardButton(buildHasString(Location.FAMAGUSTA_DISTRICT, currentLocations) + " " + SearchCriteriaConstants.famagustaDistrict)
    famagustaDistrict.callbackData = "set_location_famagusta_district"

    val selectAll = InlineKeyboardButton(SearchCriteriaConstants.chooseAll)
    selectAll.callbackData = "set_location_all"

    val done = InlineKeyboardButton(SearchCriteriaConstants.submit)
    done.callbackData = "set_location_done"

    message.replyMarkup = InlineKeyboardMarkup()
    (message.replyMarkup as InlineKeyboardMarkup).keyboard = listOf(
      listOf(paphos),
      listOf(paphosDistrict),
      listOf(limassol),
      listOf(limassolDistrict),
      listOf(nicosia),
      listOf(nicosiaDistrict),
      listOf(larnaca),
      listOf(larnacaDistrict),
      listOf(famagusta),
      listOf(famagustaDistrict),
      listOf(selectAll),
      listOf(done),
    )
  }

  private fun createSetPetMessage(message: SendMessage) {
    message.text = MessageConstants.setPets

    val allowed = InlineKeyboardButton(SearchCriteriaConstants.petsAllowed)
    allowed.callbackData = "set_pets_allowed"
    val notAllowed = InlineKeyboardButton(SearchCriteriaConstants.petsNotAllowed)
    notAllowed.callbackData = "set_pets_not_allowed"
    val any = InlineKeyboardButton(SearchCriteriaConstants.petsAny)
    any.callbackData = "set_pets_any"
    message.replyMarkup = InlineKeyboardMarkup()
    (message.replyMarkup as InlineKeyboardMarkup).keyboard = listOf(listOf(allowed, notAllowed, any))

    addExitButton(message)
  }

  private fun buildHasString(value: Any, set: Set<Any>): String {
    return if (set.contains(value)) {
      "[+]"
    } else {
      "[   ]"
    }
  }

  private fun addEditingButtons(message: SendMessage) {
    val minPriceButton = InlineKeyboardButton(SearchCriteriaConstants.minPrice)
    minPriceButton.callbackData = "set_min_price"

    val maxPriceButton = InlineKeyboardButton(SearchCriteriaConstants.maxPrice)
    maxPriceButton.callbackData = "set_max_price"

    val minBedroomsButton = InlineKeyboardButton(SearchCriteriaConstants.minBedrooms)
    minBedroomsButton.callbackData = "set_min_bedrooms"

    val maxBedroomsButton = InlineKeyboardButton(SearchCriteriaConstants.maxBedrooms)
    maxBedroomsButton.callbackData = "set_max_bedrooms"

    val minBathroomsButton = InlineKeyboardButton(SearchCriteriaConstants.minBathrooms)
    minBathroomsButton.callbackData = "set_min_bathrooms"

    val maxBathroomsButton = InlineKeyboardButton(SearchCriteriaConstants.maxBathrooms)
    maxBathroomsButton.callbackData = "set_max_bathrooms"

    val minAreaButton = InlineKeyboardButton(SearchCriteriaConstants.minArea)
    minAreaButton.callbackData = "set_min_area"

    val maxAreaButton = InlineKeyboardButton(SearchCriteriaConstants.maxArea)
    maxAreaButton.callbackData = "set_max_area"

    val petsButton = InlineKeyboardButton(SearchCriteriaConstants.pets)
    petsButton.callbackData = "set_pets"

    val locationButton = InlineKeyboardButton(SearchCriteriaConstants.location)
    locationButton.callbackData = "set_location"

    val buttons = listOf(
      listOf(minPriceButton, maxPriceButton),
      listOf(minBedroomsButton, maxBedroomsButton),
      listOf(minBathroomsButton, maxBathroomsButton),
      listOf(minAreaButton, maxAreaButton),
      listOf(petsButton),
      listOf(locationButton),
    )

    var replyMarkup = message.replyMarkup
    if (replyMarkup == null) {
      replyMarkup = InlineKeyboardMarkup()
      message.replyMarkup = replyMarkup
    }

    replyMarkup as InlineKeyboardMarkup
    try {
      replyMarkup.keyboard = replyMarkup.keyboard + buttons
    } catch (exception: Exception) {
      replyMarkup.keyboard = buttons
    }
    addExitButton(message, addResetButton = false, isTopLevel = true)
  }

  private fun addExitButton(message: SendMessage, addResetButton: Boolean = true, isTopLevel: Boolean = false) {
    val exitButton = InlineKeyboardButton(if (isTopLevel) SearchCriteriaConstants.filterIsFinished else SearchCriteriaConstants.back)
    exitButton.callbackData = "exit"
    val resetFilterButton = InlineKeyboardButton(SearchCriteriaConstants.resetCriterion)
    resetFilterButton.callbackData = "reset_filter"

    var replyMarkup = message.replyMarkup
    if (replyMarkup == null) {
      replyMarkup = InlineKeyboardMarkup()
      message.replyMarkup = replyMarkup
    }

    replyMarkup as InlineKeyboardMarkup
    try {
      if (addResetButton) {
        replyMarkup.keyboard = replyMarkup.keyboard + listOf(listOf(resetFilterButton), listOf(exitButton))
      } else {
        replyMarkup.keyboard = replyMarkup.keyboard + listOf(listOf(exitButton))
      }
    } catch (exception: Exception) {
      if (addResetButton) {
        replyMarkup.keyboard = listOf(listOf(resetFilterButton), listOf(exitButton))
      } else {
        replyMarkup.keyboard = listOf(listOf(exitButton))
      }
    }
  }
}