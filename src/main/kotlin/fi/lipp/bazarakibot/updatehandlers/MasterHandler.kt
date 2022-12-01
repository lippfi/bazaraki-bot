package fi.lipp.bazarakibot.updatehandlers

import org.telegram.telegrambots.meta.api.objects.Update

class MasterHandler(private val userActionsHandler: UserActionsHandler, private val searchCriteriaHandler: SearchCriteriaHandler): UpdateHandler {
  override fun handleUpdate(update: Update): Boolean {
    return userActionsHandler.handleUpdate(update) || searchCriteriaHandler.handleUpdate(update)
  }
}