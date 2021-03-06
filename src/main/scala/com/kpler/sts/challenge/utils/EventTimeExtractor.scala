package com.kpler.sts.challenge.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId, ZoneOffset, ZonedDateTime}

import com.kpler.sts.challenge.EVENT_TIME_TIMESTAMP_FORMAT
import com.kpler.sts.challenge.AIS_FIELD_SEPARATOR
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.streams.processor.TimestampExtractor

import scala.util.Try

class EventTimeExtractor extends TimestampExtractor {
  override def extract(record: ConsumerRecord[AnyRef, AnyRef], previousTimestamp: Long): Long = {
    EventTimeExtractor.toTimestamp(record.value().toString.split(AIS_FIELD_SEPARATOR)(4)) match {
      case Some(ts) =>
        ts.toInstant.getEpochSecond * 1000 // milliseconds
      case None =>
        throw new RuntimeException("EventTimeExtractor could not extract the timestamp")
    }
  }
}

object EventTimeExtractor {
  def toTimestamp(ts: String, tsFormat: String = EVENT_TIME_TIMESTAMP_FORMAT): Option[Timestamp] =
    Try {
      val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern(tsFormat).withZone(ZoneId.of("UTC"))
      val zdt: ZonedDateTime = ZonedDateTime.parse(ts, dtf)
      Timestamp.from(Instant.ofEpochSecond(zdt.toEpochSecond))
    }.toOption
}
