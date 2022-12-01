package fi.lipp.bazarakibot.service

import fi.lipp.bazarakibot.ApplicationProperties
import fi.lipp.bazarakibot.bot.BazarakiBot
import fi.lipp.bazarakibot.constants.SearchCriteriaConstants
import mu.KotlinLogging
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import javax.inject.Inject

class MessageServiceImpl(private val bot: BazarakiBot, private val userService: UserService): MessageService {
  private val logger = KotlinLogging.logger {}

  override fun messageBotOwner(text: String) {
    message(ApplicationProperties.ownerChatId, text)
  }

  override fun messageAll(text: String, respectNotifications: Boolean) {
    val users = if (respectNotifications) userService.getActiveUsers() else userService.getUsers()
    users.forEach { message(it.chatId, text) }
  }

  override fun message(chatId: Long, text: String) {
    val sendMessage = SendMessage()
    sendMessage.chatId = chatId.toString()
    sendMessage.text = text
    message(sendMessage)
  }

  override fun message(message: SendMessage) {
    try {
      message.enableHtml(true)
      bot.execute(message)
    } catch (exception: Exception) {
      logger.error("Failed to send message", exception)
    }
  }

  override fun editMessage(editMessage: EditMessageReplyMarkup) {
    try {
      bot.execute(editMessage)
    } catch (exception: Exception) {
      logger.error("Failed to edit message", exception)
    }
  }

  override fun answerCallback(callbackQuery: CallbackQuery) {
    try {
      val answerCallbackQuery = AnswerCallbackQuery(callbackQuery.id)
      bot.execute(answerCallbackQuery)
    } catch (exception: Exception) {
      logger.error("Failed to process AnswerCallbackQuery", exception)
    }
  }
}