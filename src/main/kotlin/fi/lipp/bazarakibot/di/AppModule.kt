package fi.lipp.bazarakibot.di

import dagger.Module
import dagger.Provides
import fi.lipp.bazarakibot.bot.BazarakiBot
import fi.lipp.bazarakibot.service.*
import fi.lipp.bazarakibot.updatehandlers.MasterHandler
import fi.lipp.bazarakibot.updatehandlers.SearchCriteriaHandler
import fi.lipp.bazarakibot.updatehandlers.UserActionsHandler
import fi.lipp.bazarakibot.watchers.BazarakiWatcher
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import javax.inject.Singleton

@Module
class AppModule {
  @Provides
  @Singleton
  fun botApi(): TelegramBotsApi {
    return TelegramBotsApi(DefaultBotSession::class.java)
  }

  @Provides
  @Singleton
  fun bot(botApi: TelegramBotsApi): BazarakiBot {
    return BazarakiBot(botApi)
  }

  @Provides
  @Singleton
  fun bazarakiService(): BazarakiService {
    return BazarakiServiceImpl()
  }

  @Provides
  @Singleton
  fun userService(): UserService {
    return UserServiceImpl()
  }

  @Provides
  @Singleton
  fun messageService(bot: BazarakiBot, userService: UserService): MessageService {
    return MessageServiceImpl(bot, userService)
  }

  @Provides
  @Singleton
  fun searchCriteriaService(): SearchCriteriaService {
    return SearchCriteriaServiceImpl()
  }

  @Provides
  @Singleton
  fun userActionsHandler(userService: UserService, messageService: MessageService, searchCriteriaService: SearchCriteriaService): UserActionsHandler {
    return UserActionsHandler(userService, messageService, searchCriteriaService)
  }

  @Provides
  @Singleton
  fun searchCriteriaHandler(messageService: MessageService, searchCriteriaService: SearchCriteriaService): SearchCriteriaHandler {
    return SearchCriteriaHandler(messageService, searchCriteriaService)
  }

  @Provides
  @Singleton
  fun masterHandler(userActionsHandler: UserActionsHandler, searchCriteriaHandler: SearchCriteriaHandler): MasterHandler {
    return MasterHandler(userActionsHandler, searchCriteriaHandler)
  }

  @Provides
  @Singleton
  fun bazarakiWatcher(userService: UserService, messageService: MessageService, bazarakiService: BazarakiService, searchCriteriaService: SearchCriteriaService): BazarakiWatcher {
    return BazarakiWatcher(userService, messageService, bazarakiService, searchCriteriaService)
  }
}