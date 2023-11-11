package fi.lipp.bazarakibot.updatehandlers

import org.telegram.telegrambots.meta.api.objects.Update

interface UpdateHandler {
  /**
   * Tries to handle update (message)
   * @return true if handler succeeded to handle the update
   */
  fun handleUpdate(update: Update): Boolean
}