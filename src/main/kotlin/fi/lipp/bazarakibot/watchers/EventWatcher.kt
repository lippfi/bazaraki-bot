package fi.lipp.bazarakibot.watchers

import kotlinx.coroutines.*

/**
 * Some object that runs in an infinite loops and checks for some event every updateInterval time
 */
abstract class EventWatcher {
  abstract var updateInterval: Long

  fun start(): Job {
    return CoroutineScope(Dispatchers.Default).launch {
      while (true) {
        if (checkForEvent()) {
          handleEvent()
        }
        delay(updateInterval)
      }
    }
  }

  /**
   * @return true if event happened
   */
  abstract fun checkForEvent(): Boolean
  abstract fun handleEvent()
}