package fi.lipp.bazarakibot.service

import fi.lipp.bazarakibot.ApplicationProperties
import fi.lipp.bazarakibot.bot.BazarakiBot
import mu.KotlinLogging
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException

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
    } catch (exception: TelegramApiRequestException) {
      if (exception.message?.contains("bot was blocked by the user") == true || exception.message?.contains("user is deactivated") == true) {
        userService.removeUser(message.chatId.toLong())
        logger.info("Removed user ${message.chatId} from the db")
      } else {
        logger.error(
          "Faced unknown telegram exception during sending message with text ${message.text} to user ${message.chatId}, reason:",
          exception
        )
      }
    } catch (exception: Exception) {
      logger.error("Failed to send message with text ${message.text} to user ${message.chatId}, reason:", exception)
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
