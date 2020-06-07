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

  "aisEventAggregator" when {
    "given 2 events close to each other in time" should {
      "return true" in {
        val instantNow: Instant = Instant.now()
        val instantAfterNow = instantNow.plusSeconds(aisEventAggregator.timeGap - 5)
        aisEventAggregator.isSimilarTimeSlot(eventA.copy(eventTime = Timestamp.from(instantNow)), eventB.copy(eventTime = Timestamp.from(instantAfterNow))) shouldBe true
      }
    }

    "given 2 events close to each other in time (reversed)" should {
      "return true" in {
        val instantNow: Instant = Instant.now()
        val instantAfterNow = instantNow.plusSeconds(aisEventAggregator.timeGap - 5)
        aisEventAggregator.isSimilarTimeSlot(eventA.copy(eventTime = Timestamp.from(instantAfterNow)), eventB.copy(eventTime = Timestamp.from(instantNow))) shouldBe true
      }
    }

    "given 2 events of the same time" should {
      "return true" in {
        aisEventAggregator.isSimilarTimeSlot(eventA.copy(eventTime = now), eventB.copy(eventTime = now)) shouldBe true
      }
    }

    "given 2 events too far from each other in time" should {
      "return false" in {
        val instantNow: Instant = Instant.now()
        val instantAfterNow = instantNow.plusSeconds(aisEventAggregator.timeGap + 5)
        aisEventAggregator.isSimilarTimeSlot(eventA.copy(eventTime = Timestamp.from(instantNow)), eventB.copy(eventTime = Timestamp.from(instantAfterNow))) shouldBe false
      }
    }
  }
}
