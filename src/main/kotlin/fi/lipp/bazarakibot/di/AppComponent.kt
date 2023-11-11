package fi.lipp.bazarakibot.di

import dagger.Component
import fi.lipp.bazarakibot.bot.BazarakiBot
import fi.lipp.bazarakibot.service.MessageService
import fi.lipp.bazarakibot.updatehandlers.MasterHandler
import fi.lipp.bazarakibot.watchers.BazarakiWatcher
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
  fun getBot(): BazarakiBot
  fun getWatcher(): BazarakiWatcher

  fun getMasterHandler(): MasterHandler
  fun getMessageService(): MessageService

  fun inject(masterHandler: MasterHandler)
  fun inject(messageService: MessageService)
}