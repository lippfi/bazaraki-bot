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
      logger.debug { "Checking for new adverts....." }
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
    logger.debug { "Notifying users about new adverts" }
    for (user in userService.getActiveUsers()) {
      adverts.filter { it.wantedByUser(user) }.forEach { messageService.message(user.chatId, buildMessage(user, it)) }
    }
  }

  private fun Advert.wantedByUser(user: User): Boolean {
    val result = transaction {
      val searchCriteria = fi.lipp.bazarakibot.model.SearchCriteria.findById(user.searchCriteria)
      if (searchCriteria == null) {
        logger.error("Failed to get search criteria for user with id ${user.chatId}")
        return@transaction false
      }
      searchCriteriaService.matches(this@wantedByUser, searchCriteria)
    }
    return result
  }

  private val commonMessages = listOf(
    "It's not IdeaVim, but still okay\n",
    "Guess what? There is a new bazaraki advert\n",
    "Psst, I've got something for you\n",
    "Get ready to call first and get in line to watch\n",
    "Looks like a good apartment. Not as good as you, though\n",
    "Beep boop, incoming rad apartment alert. Get ready to pack your stuff!\n",
    "Hold on to your socks, because this apartment might just knock 'em off\n",
    "Life is a journey. Your next stop? This awesome apartment\n",
    "You're just a call away from maybe finding your 'home sweet home'\n",
    "Alert! Another apartment dropped in. Let's go hunting\n",
    "Apartment hunt level up! Here’s a place you might love\n",
    "You miss 100% of the apartments you don't check. Here's one for you\n",
    "New apartment on the block! Get your viewing glasses on\n"
  )
  private val maleMessages = listOf(
    "Yo, stud, there's a fresh apartment for you\n",
    "Hello sir, this apartment might be interesting for you\n",
    "Hey champ, lace up your sneakers, it's time for an apartment tour\n",
    "Comrade, looking for a fortress? Check this new apartment out!\n",
    "Bro, pack your comic books, we found your new secret lair\n",
    "Sir, we've scouted a new territory that's worth your attention\n",
  ) + commonMessages
  private val femaleMessages = listOf(
    "Madam, a new advertisement has come in\n",
    "What could be more beautiful than you, gyros, or air conditioning when it' s hot? How about this apartment?\n",
    "Lady, we've got a fabulous new apartment for your royal self\n",
    "Picture this: You, a cup of tea, and this stunning new apartment. Perfect, right?\n",
    "Who needs a fairy godmother when you have me? Your dream apartment awaits\n",
    ) + commonMessages

  private fun buildMessage(user: User, advert: Advert): String {
    val messages = when (user.gender) {
      Gender.MALE -> maleMessages
      Gender.FEMALE -> femaleMessages
    }
    return messages.random() + advert.url
  }
}