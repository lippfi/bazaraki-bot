package fi.lipp.bazarakibot.service

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

interface MessageService {
  /** Sends message to the bot owner */
  fun messageBotOwner(text: String)

  /** Sends message to all users
   * @param respectNotifications
   */
  fun messageAll(text: String, respectNotifications: Boolean)

  fun message(chatId: Long, text: String)
  fun message(message: SendMessage)

  fun editMessage(editMessage: EditMessageReplyMarkup)
  fun answerCallback(callbackQuery: CallbackQuery)
}