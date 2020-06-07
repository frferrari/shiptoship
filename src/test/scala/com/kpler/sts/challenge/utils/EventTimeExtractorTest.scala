package com.kpler.sts.challenge.utils

import java.sql.Timestamp
import java.time.Instant

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.scalatest.{Matchers, WordSpec}

import java.time.OffsetDateTime
import java.time.ZoneOffset

class EventTimeExtractorTest extends WordSpec with Matchers {
  val ts: String = "2020-05-30 14:00:00"
  val tsEpochSecond: Long = 1590847200L
  val tsEpochMillisecond: Long = tsEpochSecond * 1000L

  "TimestampExtractor.toTimestamp" when {
    "given 2020-05-30 14:00:00" should {
      "return the proper timestamp" in {
        EventTimeExtractor.toTimestamp(ts) shouldEqual Option(Timestamp.from(Instant.ofEpochSecond(tsEpochSecond)))
      }
    }

    "given an invalid timestamp" should {
      "return a None" in {
        EventTimeExtractor.toTimestamp(":!:") shouldEqual None
      }
    }
  }
}
