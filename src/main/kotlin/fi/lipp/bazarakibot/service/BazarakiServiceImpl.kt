package fi.lipp.bazarakibot.service

import fi.lipp.bazarakibot.model.Advert
import fi.lipp.bazarakibot.model.AdvertBuilder
import fi.lipp.bazarakibot.model.Location
import fi.lipp.bazarakibot.model.Pets
import mu.KotlinLogging

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

/**
 * @author Sofia Povarova (https://github.com/SofyaPovarova)
 * Class to fetch new adverts, parses data and converts it into entities.
 */
class BazarakiServiceImpl : BazarakiService {
  private val logger = KotlinLogging.logger {}
  private val BAZARAKI_URL = "https://www.bazaraki.com"

  override fun fetchNewAdverts(lastAdvertTime: LocalDateTime): List<Advert> {
    logger.debug { "Fetching new adverts" }
    return (fetchNewApartmentAdverts(lastAdvertTime) + fetchNewHousesAdverts(lastAdvertTime))
      .sortedBy { it.publicationTime }
  }

  override fun fetchNewApartmentAdverts(lastAdvertTime: LocalDateTime): List<Advert> {
    val addsURL = "$BAZARAKI_URL/real-estate-to-rent/apartments-flats/?ordering=newest"
    return fetchAdverts(lastAdvertTime, addsURL)
  }

  override fun fetchNewHousesAdverts(lastAdvertTime: LocalDateTime): List<Advert> {
    val addsURL = "$BAZARAKI_URL/real-estate-to-rent/houses/?ordering=newest"
    return fetchAdverts(lastAdvertTime, addsURL)
  }

  private fun fetchAdverts(lastAdvertTime: LocalDateTime, addsURL: String): List<Advert> {
    val responsePage = try {
      Jsoup.connect(addsURL)
        .method(Connection.Method.GET)
        .execute()
    } catch (e: Exception) {
      logger.error { "Failed to connect to page $e" }
      return listOf()
    }
    val doc: Document = responsePage.parse()
    val otherAdverts = doc.getElementsByClass("list-simple__output")[0]
    val announcements = otherAdverts.getElementsByClass("list-announcement-block")

    val result = mutableListOf<Advert>()
    for (announcement in announcements) {
      val publicationDate = retrievePublicationTime(announcement)
      val advertUrl = retrieveUrl(announcement)
      if (publicationDate <= lastAdvertTime) break

      val fullAdvert = try {
        Jsoup.connect(advertUrl)
          .userAgent("Chrome")
          .method(Connection.Method.GET)
          .execute()
          .parse()
      } catch (e: Exception) {
        logger.error("Failed to connect or parse advert", e)
        continue
      }

      buildAdvertFromHtml(fullAdvert, advertUrl)?.let { result.add(it) }
    }
    logger.debug { "Fetched ${result.size} new adverts" }
    return result
  }

  private fun retrieveUrl(announcement: Element): String {
    return BAZARAKI_URL + announcement.getElementsByTag("a").attr("href")
  }

  private fun buildAdvertFromHtml(announcement: Element, advertUrl: String): Advert? =
    try {
      val publicationTime = retrievePublicationTimeFull(announcement)
      val price = retrievePrice(announcement)

      val advertBuilder = AdvertBuilder(advertUrl, publicationTime, price)

      retrieveLocation(announcement)?.let { advertBuilder.location(it) }
      retrieveBedrooms(announcement)?.let { advertBuilder.bedrooms(it) }
      retrieveBathrooms(announcement)?.let { advertBuilder.bathrooms(it) }
      retrievePets(announcement)?.let { advertBuilder.pets(it) }
      retrieveArea(announcement)?.let { advertBuilder.area(it) }

      advertBuilder.build()
    } catch (e: Exception) {
      logger.error("Failed to build add $advertUrl\n", e)
      null
    }

  private fun retrieveArea(announcement: Element): Int? =
    announcement.getElementsMatchingOwnText("Area:").parents().first()?.child(1)
      .let { if (it !== null) it.text() else "0" }
      .let { ("\\d+".toRegex()).find(it)?.groups?.get(0)?.value?.toInt() }

  private fun retrievePets(announcement: Element): Pets? =
    tryToRetrieve("Pets") {
      when (getPropertyText(announcement, "Pets")) {
        "Allowed" -> Pets.ALLOWED
        "Not allowed" -> Pets.NOT_ALLOWED
        else -> null
      }
    }

  private fun retrieveBathrooms(announcement: Element): Int? =
    tryToRetrieve("Bathrooms") { getPropertyText(announcement, "Bathrooms")?.toInt() }

  private fun retrieveBedrooms(announcement: Element): Int? =
    tryToRetrieve("Bedrooms") {
      getPropertyText(announcement, "Bedrooms")?.let { if (it == "Studio") 0 else it.toInt() }
    }

  private fun retrieveLocation(announcement: Element): Location? =
    tryToRetrieve("Location") {
      val location = announcement.select("span[itemprop=address]").text()
      val splittedLocation = location.split(",")
      convertToLocation(splittedLocation[0], if (splittedLocation.size > 1) splittedLocation[1].trimStart() else null)
    }

  private fun convertToLocation(generalLocation: String, districtLocation: String?): Location? {
    return when (generalLocation) {
      "Paphos", "Paphos district" -> {
        if (districtLocation != null && (districtLocation.contains("Paphos")
              || districtLocation.lowercase(Locale.getDefault()).contains("tombs of the kings"))
        ) {
          Location.PAPHOS
        } else {
          Location.PAPHOS_DISTRICT
        }
      }
      "Limassol", "Limassol district" -> if (districtLocation != null && districtLocation.contains("Limassol")) Location.LIMASSOL else Location.LIMASSOL_DISTRICT
      "Nicosia", "Lefkosia", "Lefkosia (Nicosia) district" -> if (districtLocation != null && districtLocation.contains("Nicosia")) Location.NICOSIA else Location.NICOSIA_DISTRICT
      "Larnaca", "Larnaca district" -> if (districtLocation != null && districtLocation.contains("Larnaca")) Location.LARNACA else Location.LARNACA_DISTRICT
      "Famagusta", "Famagusta district" -> if (districtLocation != null && districtLocation.contains("Famagusta")) Location.FAMAGUSTA else Location.FAMAGUSTA_DISTRICT
      else -> {
        logger.error { "Failed to convert location $generalLocation : $districtLocation" }
        null
      }
    }
  }

  private fun retrievePublicationTime(announcement: Element): LocalDateTime =
    tryToRetrieve("Publication time") {
      announcement.getElementsByClass("announcement-block__date")
        .first()?.text()?.split(',')?.first()?.let { formatDate(it) }
    }.let { it ?: LocalDateTime.MIN }

  private fun retrievePublicationTimeFull(announcement: Element): LocalDateTime =
    tryToRetrieve("Full publication time") {
      announcement.getElementsByClass("date-meta").first()?.text()?.let { formatDate(it.split("Posted: ")[1]) }
    }.let { it ?: LocalDateTime.MIN }

  private fun retrievePrice(announcement: Element): Int =
    tryToRetrieve("Price") {
      announcement.getElementsByClass("announcement-price__cost")
        .select("meta[itemprop=price]").attr("content").toDouble().roundToInt()
    }.let { it ?: 0 }

  private fun formatDate(dateAndTime: String): LocalDateTime {
    var dateAndTimeUnified =
      dateAndTime.replace("Today", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.M.yyyy")))
    dateAndTimeUnified = dateAndTimeUnified.replace(
      "Yesterday",
      LocalDate.now().minus(Period.ofDays(1)).format(DateTimeFormatter.ofPattern("dd.M.yyyy"))
    )
    val formatter = DateTimeFormatter.ofPattern("dd.M.yyyy HH:mm")

    return LocalDateTime.parse(dateAndTimeUnified, formatter)
  }

  private fun <T> tryToRetrieve(propertyName: String, retriever: () -> T?): T? = try {
    retriever()
  } catch (e: Exception) {
    logger.error { "Failed to retrieve $propertyName.\n" + "ERROR: " + e.message }
    null
  }

  private fun getPropertyText(announcement: Element, propertyName: String): String? =
    announcement.select("meta[name=keywords]").first()?.attr("content")
      ?.let { keyWords -> "$propertyName (\\w+)".toRegex().find(keyWords) }
      ?.let { it.groups[1]?.value }
}
