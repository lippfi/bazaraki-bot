package fi.lipp.bazarakibot.service

import fi.lipp.bazarakibot.ApplicationProperties
import fi.lipp.bazarakibot.model.Gender
import fi.lipp.bazarakibot.model.Location
import fi.lipp.bazarakibot.model.SearchCriteria
import fi.lipp.bazarakibot.model.User
import fi.lipp.bazarakibot.repository.SearchCriterias
import fi.lipp.bazarakibot.repository.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserServiceImpl : UserService {
  init {
    Database.connect(ApplicationProperties.dbURL, ApplicationProperties.dbDriver)
    transaction {
      SchemaUtils.create(Users, SearchCriterias)
    }
  }

  override fun getUsers(): Collection<User> {
    return transaction {
      User.all().toList()
    }
  }

  override fun getActiveUsers(): Collection<User> {
    return transaction { Users.select( Users.notifications eq true).map { User.wrapRow(it) } }
  }

  override fun enableAdvertNotifications(chatId: Long) {
    transaction {
      val user = getUserByChatId(chatId)
      user?.let { it.notifications = true }
    }
  }

  override fun disableAdvertNotifications(chatId: Long) {
    transaction {
      val user = getUserByChatId(chatId)
      user?.let { it.notifications = false }
    }
  }

  override fun removeUser(chatId: Long) {
    transaction {
      User.find { Users.chatId eq chatId }.firstOrNull()?.delete()
    }
  }

  override fun registerUser(chatId: Long, gender: Gender) {
    transaction {
      val searchCriteria = SearchCriteria.new {
        location = mutableSetOf(Location.PAPHOS, Location.PAPHOS_DISTRICT, Location.LIMASSOL, Location.LIMASSOL_DISTRICT, Location.NICOSIA, Location.NICOSIA_DISTRICT, Location.LARNACA, Location.LARNACA_DISTRICT, Location.FAMAGUSTA, Location.FAMAGUSTA_DISTRICT)
      }

      User.new {
        this.chatId = chatId
        this.gender = gender
        this.notifications = true
        this.searchCriteria = searchCriteria.id
      }
    }
  }

  override fun ifUserExists(chatId: Long): Boolean {
    return getUserByChatId(chatId) != null
  }

  override fun getUserByChatId(chatId: Long): User? {
    return transaction {
      User.find { Users.chatId eq chatId }.firstOrNull()
    }
  }
}