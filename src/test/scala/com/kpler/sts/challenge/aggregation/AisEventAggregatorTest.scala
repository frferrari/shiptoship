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
  val eventA: AisEvent = AisEvent(harborId, 200000000, 4316, 28.57775, -94.29173, now, 0, 172, 150, 21.2, 138, "T-AIS", "", "0")
  val eventB: AisEvent = AisEvent(harborId, 300000000, 5399, 28.57775, -94.29173, now, 0, 172, 150, 21.2, 138, "T-AIS", "", "0")

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
}
