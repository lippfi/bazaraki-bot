package fi.lipp.bazarakibot.watchers

import fi.lipp.bazarakibot.model.Advert
import fi.lipp.bazarakibot.model.Gender
import fi.lipp.bazarakibot.model.User
import fi.lipp.bazarakibot.service.BazarakiService
import fi.lipp.bazarakibot.service.MessageService
import fi.lipp.bazarakibot.service.SearchCriteriaService
import fi.lipp.bazarakibot.service.UserService
import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class BazarakiWatcher(private val userService: UserService, private val messageService: MessageService, private val bazarakiService: BazarakiService, private val searchCriteriaService: SearchCriteriaService) : EventWatcher() {
  private val logger = KotlinLogging.logger {}
  override var updateInterval: Long = 90_000L
  private var lastAdvertTime: LocalDateTime = LocalDateTime.now()
  private var lastAdverts: List<Advert> = emptyList()

  override fun checkForEvent(): Boolean {
    lastAdverts = try {
      bazarakiService.fetchNewAdverts(lastAdvertTime)
    } catch (exception: Exception) {
      logger.error("Failed to fetch new adverts", exception)
      emptyList()
    }
    if (lastAdverts.isEmpty()) return false
    lastAdvertTime = lastAdverts.maxOf { it.publicationTime }
    return lastAdverts.isNotEmpty()
  }

  override fun handleEvent() {
    notifyAboutAdverts(lastAdverts)
  }

  private fun notifyAboutAdverts(adverts: List<Advert>) {
    for (user in userService.getActiveUsers()) {
      adverts.filter { it.wantedByUser(user) }.forEach { messageService.message(user.chatId, buildMessage(user, it)) }
    }
  }

  private fun Advert.wantedByUser(user: User): Boolean {
    return transaction {
      val searchCriteria = fi.lipp.bazarakibot.model.SearchCriteria.findById(user.searchCriteria)
      if (searchCriteria == null) {
        logger.error("Failed to get search criteria for user with id ${user.chatId}")
        return@transaction false
      }
      searchCriteriaService.matches(this@wantedByUser, searchCriteria)
    }
  }

  private val commonMessages = listOf(
    "It's not IdeaVim, but still okay\n",
    "Guess what? There is a new bazaraki advert\n",
    "Psst, I've got something for you\n",
    "Get ready to call first and get in line to watch\n",
    "Looks like a good apartment. Not as good as you, though\n",
  )
  private val maleMessages = listOf(
    "Yo, stud, there's a fresh apartment for you\n",
    "Hello sir, this apartment might be interesting for you\n",
  ) + commonMessages
  private val femaleMessages = listOf(
    "Madam, a new advertisement has come in\n",
    "What could be more beautiful than you, gyros, or air conditioning when it' s hot? How about this apartment?\n",
  ) + commonMessages

  private fun buildMessage(user: User, advert: Advert): String {
    val messages = when (user.gender) {
      Gender.MALE -> maleMessages
      Gender.FEMALE -> femaleMessages
    }
    return messages.random() + advert.url
  }
}