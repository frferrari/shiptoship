package com.kpler.sts.challenge.aggregation

import java.sql.Timestamp
import java.time.Instant

import com.kpler.sts.challenge.StsConsumer.HarborId
import com.kpler.sts.challenge.model.AisEvent
import org.scalatest.{Matchers, WordSpec}

class AisEventAggregatorTest extends WordSpec with Matchers {

  val aisEventAggregator: AisEventAggregator = AisEventAggregator()
  val now: Timestamp = Timestamp.from(Instant.now())

  val harborId: HarborId = "120090"
  val vesselA: Int = 4316
  val vesselB: Int = 5399
  val eventA: AisEvent = AisEvent(harborId, 200000000, 4316, 28.57775, -94.29173, now, aisEventAggregator.maxSpeedKnot, 172, 150, 21.2, 138, "T-AIS", "", "0")
  val eventB: AisEvent = AisEvent(harborId, 300000000, 5399, 28.57775, -94.29173, now, aisEventAggregator.maxSpeedKnot, 172, 150, 21.2, 138, "T-AIS", "", "0")

  /**
   * Similar Speed
   */
  "isSimilarSpeed" when {
    "given 2 events close to each other in speed" should {
      "return true" in {
        aisEventAggregator.isSimilarSpeed(eventA.copy(speed = 5.0), eventB.copy(speed = 5.0 + aisEventAggregator.speedGap / 2)) shouldBe true
      }
    }

    "given 2 events close to each other in speed (reversed)" should {
      "return true" in {
        aisEventAggregator.isSimilarSpeed(eventA.copy(speed = 5.0 + aisEventAggregator.speedGap / 2), eventB.copy(speed = 5.0)) shouldBe true
      }
    }

    "given 2 events of the same speed" should {
      "return true" in {
        aisEventAggregator.isSimilarSpeed(eventA.copy(speed = 5.0), eventB.copy(speed = 5.0)) shouldBe true
      }
    }

    "given 2 events too far from each other in speed" should {
      "return false" in {
        aisEventAggregator.isSimilarSpeed(eventA.copy(speed = 5.0), eventB.copy(speed = 5.0 + aisEventAggregator.speedGap * 2)) shouldBe false
      }
    }
  }

  /**
   * Similar Heading
   */
  "isSimilarHeading" when {
    "given 2 events close to each other in heading" should {
      "return true" in {
        aisEventAggregator.isSimilarHeading(eventA.copy(heading = 90), eventB.copy(heading = 90 + aisEventAggregator.headingGap / 2)) shouldBe true
      }
    }

    "given 2 events close to each other in heading (reversed)" should {
      "return true" in {
        aisEventAggregator.isSimilarHeading(eventA.copy(heading = 90 + aisEventAggregator.headingGap / 2), eventB.copy(heading = 90)) shouldBe true
      }
    }

    "given 2 events of the same heading" should {
      "return true" in {
        aisEventAggregator.isSimilarHeading(eventA.copy(heading = 90), eventB.copy(heading = 90)) shouldBe true
      }
    }

    "given 2 events too far from each other in heading" should {
      "return false" in {
        aisEventAggregator.isSimilarHeading(eventA.copy(heading = 90), eventB.copy(heading = 90 + aisEventAggregator.headingGap * 2)) shouldBe false
      }
    }
  }

  /**
   * Short distance
   */
  "isShortDistance" when {
    "given 2 events close to each other in distance" should {
      "return true" in {
        aisEventAggregator.isShortDistance(eventA, eventA.copy(latitude = eventA.latitude + 0.0001, longitude = eventA.longitude + 0.0001)) shouldBe true
      }
    }

    "given 2 events close to each other in distance (reversed)" should {
      "return true" in {
        aisEventAggregator.isShortDistance(eventA.copy(latitude = eventA.latitude + 0.0001, longitude = eventA.longitude + 0.0001), eventA) shouldBe true
      }
    }

    "given 2 events of the same position" should {
      "return true" in {
        aisEventAggregator.isShortDistance(eventA, eventA) shouldBe true
      }
    }

    "given 2 events too far from each other in distance" should {
      "return false" in {
        aisEventAggregator.isShortDistance(eventA, eventA.copy(latitude = eventA.latitude + 1.0, longitude = eventA.longitude + 1.0)) shouldBe false
      }
    }
  }

  /**
   * Below maximum speed
   */
  "isBelowMaxSpeed" when {
    "given 1 even whose speed is below maxSpeed" should {
      "return true" in {
        aisEventAggregator.isBelowMaxSpeed(eventA.copy(speed = aisEventAggregator.maxSpeedKnot - 1)) shouldBe true
      }
    }

    "given 1 even whose speed is above maxSpeed" should {
      "return false" in {
        aisEventAggregator.isBelowMaxSpeed(eventA.copy(speed = aisEventAggregator.maxSpeedKnot + 1)) shouldBe false
      }
    }
  }

  /**
   * Identify an STS event
   */
  "isStsEvent" when {
    "given 2 events close to each other in speed, distance and heading" should {
      "return true" in {
        val eventB: AisEvent = eventA.copy(
          speed = eventA.speed - aisEventAggregator.speedGap / 2,
          heading = eventA.heading - aisEventAggregator.headingGap / 2,
          latitude = eventA.latitude + 0.0001,
          longitude = eventA.longitude + 0.0001)

        aisEventAggregator.isStsEvent(eventA, eventB) shouldBe true
      }
    }

    "given 2 events with the same speed, distance and heading" should {
      "return false" in {
        val eventB: AisEvent = eventA.copy(
          heading = eventA.heading + aisEventAggregator.headingGap + 1)

        aisEventAggregator.isStsEvent(eventA, eventB) shouldBe false
      }
    }
  }
}
