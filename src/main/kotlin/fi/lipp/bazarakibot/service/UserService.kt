package fi.lipp.bazarakibot.service

import fi.lipp.bazarakibot.model.Gender
import fi.lipp.bazarakibot.model.User

interface UserService {
    /** Checks if user already registered */
    fun ifUserExists(chatId: Long): Boolean
    /** Registers user (saves info about user to database) */
    fun registerUser(chatId: Long, gender: Gender)

    /** Gets user by chat id */
    fun getUserByChatId(chatId: Long): User?
    /** Gets all users */
    fun getUsers(): Collection<User>
    /** Gets all users with notifications turned on */
    fun getActiveUsers(): Collection<User>

    /** Enables notifications for user by chat id */
    fun enableAdvertNotifications(chatId: Long)
    /** Disables notifications for user by chat id */
    fun disableAdvertNotifications(chatId: Long)

    /**
     * For those who ~~teared me apart~~ blocked the bot
     */
    fun removeUser(chatId: Long)
}