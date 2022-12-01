package fi.lipp.bazarakibot.model

import fi.lipp.bazarakibot.repository.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class User(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    var chatId by Users.chatId
    var gender by Users.gender
    var notifications by Users.notifications
    var searchCriteria by Users.searchCriteria
}

enum class Gender {
    MALE,
    FEMALE,
}