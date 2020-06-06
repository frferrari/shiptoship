package com.kpler.sts.challenge.utils

import java.sql.Timestamp
import java.time.Instant

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.scalatest.{Matchers, WordSpec}

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

  /*
  "TimestampExtractor.extract" when {
    "given a valid AIS record" should {
      "return the proper timestamp" in {
        val aisEvent: String = s"232219611,8213,29.73835,-95.12553,2017-11-25 00:01:35,0,239,,9,73,T-AIS,2017-11-25 00:08:28,etl.pipelines.position._process_and_add_position,2017-11-25 00:19:10,sqlalchemy.sql.schema.<lambda>,false,0101000020E6100000AE2AFBAE08C857C069006F8104BD3D40,5"
        val consumerRecord: ConsumerRecord[AnyRef, AnyRef] = new ConsumerRecord[AnyRef, AnyRef]("topic", 0, 0, 1234, aisEvent)
        new EventTimeExtractor()
          .extract(consumerRecord, 0) shouldEqual(tsEpochMillisecond)
      }
    }

    "given an invalid AIS record" should {
      "throw an exception" in {
        val aisEvent: String = s"232219611,8213,29.73835,-95.12553,!:!,0,239,,9,73,T-AIS,2017-11-25 00:08:28,etl.pipelines.position._process_and_add_position,2017-11-25 00:19:10,sqlalchemy.sql.schema.<lambda>,false,0101000020E6100000AE2AFBAE08C857C069006F8104BD3D40,5"

        assertThrows[RuntimeException] {
          new EventTimeExtractor().extract(new ConsumerRecord("topic", 0, 0, 1234, aisEvent), 0)
        }
      }
    }
  }
   */
}
