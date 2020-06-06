package com.kpler.sts.challenge.model

import java.sql.Timestamp

import argonaut.Argonaut._
import argonaut.CodecJson
import com.kpler.sts.challenge.StsConsumer.{EventId, EventTime, HarborId, Heading, Latitude, Longitude, Speed, VesselId}

case class Sts(harborId: HarborId,
               eventId: EventId,
               vesselId: VesselId,
               latitude: Latitude,
               longitude: Longitude,
               eventTime: EventTime,
               speed: Speed,
               heading: Heading) {
  override def toString: String =
    s"<<ID=${vesselId} SP=${speed} HD=${heading} E=${eventId} T=${eventTime}>>"

  override def equals(any: Any): Boolean = any match {
    case that: Sts => this.harborId == that.harborId && this.eventId == that.eventId
    case _ => false
  }
}

object Sts {
  def apply(aisEvent: AisEvent): Sts = {
    new Sts(
      aisEvent.harborId,
      aisEvent.eventId,
      aisEvent.vesselId,
      aisEvent.latitude,
      aisEvent.longitude,
      aisEvent.eventTime,
      aisEvent.speed,
      aisEvent.heading)
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

  implicit def StsCodecJson: CodecJson[Sts] =
    casecodec8(Sts.apply, Sts.unapply)("harborId", "eventId", "vesselId", "latitude", "longitude", "eventTime", "speed", "heading")
}
