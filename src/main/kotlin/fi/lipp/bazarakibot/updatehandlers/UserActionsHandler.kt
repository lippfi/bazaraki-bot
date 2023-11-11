package fi.lipp.bazarakibot.updatehandlers

import fi.lipp.bazarakibot.ApplicationProperties
import fi.lipp.bazarakibot.model.Gender
import fi.lipp.bazarakibot.constants.MessageConstants
import fi.lipp.bazarakibot.constants.SearchCriteriaConstants
import fi.lipp.bazarakibot.constants.UserConstants
import fi.lipp.bazarakibot.service.MessageService
import fi.lipp.bazarakibot.service.SearchCriteriaService
import fi.lipp.bazarakibot.service.UserService
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

class UserActionsHandler(private val userService: UserService, private val messageService: MessageService, private val searchCriteriaService: SearchCriteriaService) : UpdateHandler {
  private val chatsInProcessOfRegistration = mutableSetOf<Long>()

  override fun handleUpdate(update: Update): Boolean {
    return if (update.hasCallbackQuery()) {
      handleCallback(update.callbackQuery)
    } else if (update.hasMessage()) {
      handleMessage(update.message)
    } else {
      return false
    }
  }

  private fun handleCallback(callbackQuery: CallbackQuery): Boolean {
    val chatId = callbackQuery.message.chatId
    return if (userService.ifUserExists(chatId)) {
      handleRegisteredCallback()
    } else {
      handleUnregisteredCallback(chatId, callbackQuery)
    }
  }

  private fun handleMessage(message: Message): Boolean {
    val chatId = message.chatId
    val text = message.text
    return if (userService.ifUserExists(chatId)) {
      handleRegisteredText(chatId, text)
    } else {
      handleUnregisteredText(chatId)
    }
  }

  private fun handleUnregisteredCallback(chatId: Long, query: CallbackQuery): Boolean {
    val data = query.data
    if (data == "register_male" || data == "register_female") {
      val gender = if (data == "register_male") Gender.MALE else Gender.FEMALE
      chatsInProcessOfRegistration.remove(chatId)
      userService.registerUser(chatId, gender)
      messageService.answerCallback(query)
      messageService.message(chatId, MessageConstants.registrationSuccess)
      return true
    }
    return false
  }

  private fun handleRegisteredCallback(): Boolean {
    return false
  }

  private fun handleUnregisteredText(chatId: Long): Boolean {
    if (!chatsInProcessOfRegistration.contains(chatId)) {
      messageService.message(chatId, MessageConstants.greeting)
    }

    val genderChoiceMessage = SendMessage()
    genderChoiceMessage.chatId = chatId.toString()
    genderChoiceMessage.text = MessageConstants.chooseGender

    val maleButton = InlineKeyboardButton(UserConstants.male)
    maleButton.callbackData = "register_male"
    val femaleButton = InlineKeyboardButton(UserConstants.female)
    femaleButton.callbackData = "register_female"
    val buttons = listOf(listOf(maleButton, femaleButton))
    val replyMarkup = InlineKeyboardMarkup()
    replyMarkup.keyboard = buttons
    genderChoiceMessage.replyMarkup = replyMarkup
    chatsInProcessOfRegistration.add(chatId)
    messageService.message(genderChoiceMessage)
    return true
  }

  private fun handleRegisteredText(chatId: Long, text: String): Boolean {
    val isOwner = ApplicationProperties.ownerChatId == chatId
    when {
      text == "/help" -> messageService.message(chatId, MessageConstants.help)
      text == "/thank_you" -> messageService.message(chatId, MessageConstants.thankAuthors)
      text == "/enable_notifications" -> {
        userService.enableAdvertNotifications(chatId)
        val messageText = MessageConstants.notificationsEnabled + "\n\n" +
            SearchCriteriaConstants.currentCriteria + ":\n" +
            searchCriteriaService.getSearchCriteriaString(chatId)
        messageService.message(chatId, messageText)
      }
      text == "/disable_notifications" -> {
        userService.disableAdvertNotifications(chatId)
        messageService.message(chatId, MessageConstants.notificationsDisabled)
      }
      text == "/statistic" && isOwner -> {
        val users = userService.getUsers()
        val messageText = "Hey! There are " + users.size + " users, " + users.count { it.notifications } + " of them are have notifications on"
        messageService.messageBotOwner(messageText)
      }
      text.startsWith("/global!") && isOwner -> {
        val messageText = text.replaceFirst("/global!", "")
        messageService.messageAll(messageText, false)
      }
      text.startsWith("/global") && isOwner -> {
        val messageText = text.replaceFirst("/global", "")
        messageService.messageAll(messageText, true)
      }
      else -> return false
    }
    return true
  }
}