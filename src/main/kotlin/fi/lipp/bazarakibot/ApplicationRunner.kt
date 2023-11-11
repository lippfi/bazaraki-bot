package fi.lipp.bazarakibot

import fi.lipp.bazarakibot.di.DaggerAppComponent
import java.util.Properties

// todo handle blocking bot
// todo try to catch exceptions during editing buttons
object ApplicationProperties {
  private val properties = Properties()
  init {
    val applicationIS = javaClass.getResourceAsStream("/application.properties")
    properties.load(applicationIS)
  }

  val ownerChatId = properties.getProperty("bot.owner.chatId").toLong()
  val dbURL: String = properties.getProperty("bot.users.db.url")
  val dbDriver: String = properties.getProperty("bot.users.db.driver")
  val botUsername: String = properties.getProperty("bot.username")
  val botToken: String = properties.getProperty("bot.token")
}

fun main() {
  val appComponent = DaggerAppComponent.create()
  appComponent.getBot().masterHandler = appComponent.getMasterHandler()
  appComponent.getBot().messageService = appComponent.getMessageService()

  appComponent.getBot().start()
  appComponent.getWatcher().start()
}
