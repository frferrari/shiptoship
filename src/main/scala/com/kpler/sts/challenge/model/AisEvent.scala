package com.kpler.sts.challenge.model

import java.sql.Timestamp

import argonaut.Argonaut._
import argonaut.CodecJson
import com.kpler.sts.challenge.StsConsumer.{Course, Draught, EventId, EventTime, HarborId, Heading, Latitude, Longitude, Speed, VesselId}
import com.kpler.sts.challenge.utils.EventTimeExtractor
import com.kpler.sts.challenge.{AIS_FIELD_SEPARATOR, EVENT_TIME_TIMESTAMP_FORMAT}

import scala.util.Try

case class AisEvent(harborId: HarborId,
                    eventId: EventId,
                    vesselId: VesselId,
                    latitude: Latitude,
                    longitude: Longitude,
                    eventTime: EventTime,
                    speed: Speed,
                    course: Course,
                    heading: Heading,
                    draught: Draught,
                    providerId: Long,
                    aisType: String,
                    point: String,
                    newNavigationalStatus: String)

object AisEvent {
  /**
   * Converts a string containing the AIS event details to and AIS object
   *
   * @param harborId        The haborId where this AIS event was recorded
   * @param record          The record to decode
   * @param sep             The field separator for the fields in the record string
   * @param timestampFormat The format of the timestamp to decode the event time in the AIS record
   * @return An AIS object
   */
  def apply(harborId: HarborId, record: String, sep: Char = AIS_FIELD_SEPARATOR, timestampFormat: String = EVENT_TIME_TIMESTAMP_FORMAT): Option[AisEvent] = {
    record.split(sep) match {
      case Array(eventId, vesselId, latitude, longitude, eventTimeUtc, speed, course, heading, draught, providerId, aisType, _, _, _, _, _, point, newNavigationStatus) => {
        Try {
          EventTimeExtractor
            .toTimestamp(eventTimeUtc, timestampFormat)
            .map(new AisEvent(harborId, eventId.toLong, vesselId.toLong, latitude.toDouble, longitude.toDouble, _, speed.toDouble, course.toInt, heading.toInt, draught.toDouble, providerId.toLong, aisType, point, newNavigationStatus))
        }.toOption.flatten
      }
      case _ =>
        None
    }
  }

  implicit def EventTimeCodecJson: CodecJson[Timestamp] =
    CodecJson(
      (t: Timestamp) =>
        ("eventTime" := t.getTime) ->:
          jEmptyObject,
      l => for {
        timestamp <- (l --\ "eventTime").as[Long]
      } yield new Timestamp(timestamp)
    )

  implicit def AisEventCodecJson: CodecJson[AisEvent] =
    casecodec14(AisEvent.apply, AisEvent.unapply)("harborId", "eventId", "vesselId", "latitude", "longitude", "eventTime", "speed", "course", "heading", "draugh", "providerId", "aisType", "point", "newNavigationalStatus")
}
