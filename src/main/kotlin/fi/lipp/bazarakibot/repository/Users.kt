package fi.lipp.bazarakibot.repository

import fi.lipp.bazarakibot.model.Gender
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Users : IntIdTable() {
    var chatId: Column<Long> = long("chat_id")
    var gender = enumeration("gender", Gender::class)
    var notifications = bool("notifications")

    val searchCriteria = reference("search_criteria", SearchCriterias)
}