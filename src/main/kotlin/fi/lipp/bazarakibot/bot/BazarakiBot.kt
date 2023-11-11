package fi.lipp.bazarakibot.bot

import fi.lipp.bazarakibot.ApplicationProperties
import fi.lipp.bazarakibot.constants.MessageConstants
import fi.lipp.bazarakibot.service.MessageService
import fi.lipp.bazarakibot.updatehandlers.MasterHandler
import mu.KotlinLogging
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import javax.inject.Inject

class BazarakiBot(private val botApi: TelegramBotsApi): TelegramLongPollingBot() {
  private val logger = KotlinLogging.logger {}
  @Inject lateinit var masterHandler: MasterHandler
  @Inject lateinit var messageService: MessageService

  override fun getBotUsername(): String {
    return ApplicationProperties.botUsername
  }

  override fun getBotToken(): String {
    return ApplicationProperties.botToken
  }

  override fun onUpdateReceived(update: Update?) {
    if (update != null) {
      val isHandled = masterHandler.handleUpdate(update)
      if (!isHandled) {
        val chatId = getChatId(update)
        if (chatId != null) {
          messageService.message(chatId, MessageConstants.error)
        } else {
          logger.error("Failed to retrieve chat id from update")
        }
      }
    } else {
      logger.error("Received Update object is null")
    }
  }

  fun start() {
    botApi.registerBot(this)
  }

  private fun getChatId(update: Update): Long? {
    return if (update.hasMessage()) {
      update.message.chatId
    } else if (update.hasCallbackQuery()) {
      update.callbackQuery.message.chatId
    } else {
      null
    }
  }
}