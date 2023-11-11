package fi.lipp.bazarakibot.service

import fi.lipp.bazarakibot.model.Advert
import java.time.LocalDateTime

interface BazarakiService {
  fun fetchNewAdverts(lastAdvertTime: LocalDateTime): List<Advert>
  fun fetchNewApartmentAdverts(lastAdvertTime: LocalDateTime): List<Advert>
  fun fetchNewHousesAdverts(lastAdvertTime: LocalDateTime): List<Advert>
}