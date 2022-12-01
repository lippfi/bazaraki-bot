package fi.lipp.bazarakibot.constants

class MessageConstants {
  companion object {
    val greeting = """
      Hi! This bot will notify you about all new rentals on bazaraki as soon as you sign up
      If you're having trouble or find a bug, please contact @lippfi
      Special thanks to @sofya_povarova, who helped to parse Bazaraki ads, since Bazaraki has no API
      Good luck with your apartment search!
      """.trimIndent()
    const val help = "If you only want to be notified about certain advertisements, for example, less than 1000 euros a month, or only pet friendly apartments, then run the /edit command and adjust your search criteria.\nIf you need something seems broken, or you need any other help, feel free to contact @lippfi"
    val thankAuthors = """
      If the bot helped you, you can contribute to the bot and make it better for other people
      The repository is https://github.com/lippfi/bazaraki-bot
      (even small changes like fixing my English mistakes or adding more funny message templates are highly appreciated)
      You can also star the repository and close my github gestalt 👉👈
      """.trimIndent()
    const val error = "Failed to process command"

    const val chooseGender = "To get started, please specify your gender (it affects only the addressing in messages)"
    const val registrationSuccess = "Congrats, you are now registered and subscribed to the ad updates\nRun /edit command to personalize your search filter"

    const val notificationsEnabled = "Notifications turned on. You will be notified about new adverts matching your search criteria"
    const val notificationsDisabled = "You will no longer be notified about new adverts"

    const val editSearchCriteria = "Let's edit your search criteria"

    const val locationIsSet = "Location selection successful"

    const val setMinPrice= "Send the minimum price with a message"
    const val setMaxPrice= "Send the maximum price with a message"
    const val setMinBedrooms= "Send the minimum number of bedrooms with a message"
    const val setMaxBedrooms= "Send the maximum number of bedrooms with a message"
    const val setMinBathrooms= "Send the minimum number of bathrooms with a message"
    const val setMaxBathrooms= "Send the maximum number of bathrooms with a message"
    const val setMinArea= "Send the minimum area (square meters) with a message"
    const val setMaxArea= "Send the maximum area (square meters) with a message"
    const val setLocation = "Choose apartment location"
    const val setPets = "Choose if pets are allowed"


    const val minPriceIsSet= "Minimum price set successfully"
    const val maxPriceIsSet= "Maximum price set successfully"
    const val minBedroomsIsSet= "Minimum number of bedrooms set successfully"
    const val maxBedroomsIsSet= "Maximum number of bedrooms set successfully"
    const val minBathroomsIsSet= "Minimum number of bathrooms set successfully"
    const val maxBathroomsIsSet= "Maximum number of bathrooms set successfully"
    const val minAreaIsSet= "Minimum area set successfully"
    const val maxAreaIsSet= "Maximum area set successfully"
    const val petsIsSet= "Pet criterion selection successful"
    const val petsIsReset= "Pet criterion reset successful"
    const val criterionIsReset= "Criterion reset successful"
    const val criteriaIsSet= "Filter editing completed successfully"

    const val failedToParseNumber = "Failed to parse number. Please try again"
  }
}