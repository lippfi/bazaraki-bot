package fi.lipp.bazarakibot.constants

class MessageConstants {
  companion object {
    val greeting = """
      Hi! This bot will notify you about all new rentals on Bazaraki as soon as you sign up.
      If you're having trouble or encounter a bug, please contact @lippfi.
      Special thanks to @sofya_povarova, who helped to parse Bazaraki ads, as Bazaraki has no API. And thanks to ChatGPT for generating cringey message templates you will see.
      Good luck with your apartment search!
      """.trimIndent()

    const val help = "If you only want to be notified about certain advertisements, for example, those less than 1000 euros a month, or only pet-friendly apartments, then run the /edit command and adjust your search criteria.\nIf something appears to be broken, or if you need any other help, feel free to contact @lippfi."

    val thankAuthors = """
      If this bot has been helpful, you can contribute to its improvement and make it better for other people.
      The repository is at https://github.com/lippfi/bazaraki-bot.
      """.trimIndent()
    const val error = "Failed to process the command."

    const val chooseGender = "To get started, please specify your gender (this only affects the manner of addressing in messages)."
    const val registrationSuccess = "Congrats, you are now registered and subscribed to the ad updates. Run the /edit command to personalize your search filter."

    const val notificationsEnabled = "Notifications turned on. You will be notified about new adverts matching your search criteria."
    const val notificationsDisabled = "You will no longer receive notifications about new adverts."

    const val editSearchCriteria = "Let's edit your search criteria."

    const val locationIsSet = "Location selection was successful."

    const val setMinPrice= "Send the minimum price as a message."
    const val setMaxPrice= "Send the maximum price as a message."
    const val setMinBedrooms= "Send the minimum number of bedrooms as a message."
    const val setMaxBedrooms= "Send the maximum number of bedrooms as a message."
    const val setMinBathrooms= "Send the minimum number of bathrooms as a message."
    const val setMaxBathrooms= "Send the maximum number of bathrooms as a message."
    const val setMinArea= "Send the minimum area (in square meters) as a message."
    const val setMaxArea= "Send the maximum area (in square meters) as a message."
    const val setLocation = "Choose the apartment location."
    const val setPets = "Specify if pets are allowed."
    
    const val minPriceIsSet= "Minimum price was set successfully."
    const val maxPriceIsSet= "Maximum price was set successfully."
    const val minBedroomsIsSet= "Minimum number of bedrooms was set successfully."
    const val maxBedroomsIsSet= "Maximum number of bedrooms was set successfully."
    const val minBathroomsIsSet= "Minimum number of bathrooms was set successfully."
    const val maxBathroomsIsSet= "Maximum number of bathrooms was set successfully."
    const val minAreaIsSet= "Minimum area was set successfully."
    const val maxAreaIsSet= "Maximum area was set successfully."
    const val petsIsSet= "Pet preference was set successfully."
    const val petsIsReset= "Pet preference was reset successfully."
    const val criterionIsReset= "Criterion was reset successfully."
    const val criteriaIsSet= "Filter editing was completed successfully."

    const val failedToParseNumber = "Failed to parse number. Please try again."
  }
}