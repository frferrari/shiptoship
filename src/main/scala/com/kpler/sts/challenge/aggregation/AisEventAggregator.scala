package com.kpler.sts.challenge.aggregation

import com.kpler.sts.challenge.StsConsumer.{HarborId, VesselId}
import com.kpler.sts.challenge.model.{AisEvent, AisEventAggregate, Sts}

import scala.collection.immutable

class AisEventAggregator(maxSpeedKnot: Double = 5.0,
                         maxDistanceMeter: Int = 100,
                         headingGap: Double = 5.0,
                         speedGap: Double = 0.3) {

  private val AVERAGE_RADIUS_OF_EARTH_METER = 6371000

  def aggregate(harborId: HarborId, aisEvent: AisEvent, stsDetector: AisEventAggregate): AisEventAggregate = {
    val newStsDetector: AisEventAggregate = stsDetector.copy(aisEvents = stsDetector.aisEvents :+ aisEvent)

    val aisEventsPerVessel: Map[VesselId, List[AisEvent]] =
      newStsDetector
        .aisEvents
        .groupBy(_.vesselId)

    val stsEvents: List[(Sts, Sts)] = findSts(aisEventsPerVessel)

    stsEvents
      .zipWithIndex
      .foreach { case ((stsA, stsB), idx) =>
        if (!newStsDetector.stsEvents.contains((stsA, stsB)))
          println(s"STS($idx) detected between $stsA and $stsB")
      }

    newStsDetector.copy(stsEvents = newStsDetector.stsEvents ++ stsEvents)
  }

  def findSts(aisEventsPerVessel: Map[VesselId, List[AisEvent]]): List[(Sts, Sts)] = {
    val closestEvents: immutable.Iterable[List[(Sts, Sts)]] =
      for {
        (vesselIdA, aisEventsA) <- aisEventsPerVessel
        (vesselIdB, aisEventsB) <- aisEventsPerVessel.filterNot(_._1 == vesselIdA)
      } yield findClosestVessels(aisEventsA, aisEventsB)

    closestEvents
      .flatten
      .filter { case (l, r) => l.vesselId < r.vesselId }
      .toList
  }

  def findClosestVessels(aisEventsA: List[AisEvent], aisEventsB: List[AisEvent]): List[(Sts, Sts)] = {
    for {
      aisEventA <- aisEventsA
      aisEventB <- aisEventsB
      if (isStsEvent(aisEventA, aisEventB))
    } yield (Sts(aisEventA), Sts(aisEventB))
  }

  def isStsEvent(aisEventA: AisEvent, aisEventB: AisEvent): Boolean = {
    isShortDistance(aisEventA, aisEventB) &&
      isSimilarHeading(aisEventA, aisEventB) &&
      isSimilarSpeed(aisEventA, aisEventB) &&
      isBelowMaxSpeed(aisEventA) &&
      isBelowMaxSpeed(aisEventB)
  }

  def isBelowMaxSpeed(aisEvent: AisEvent): Boolean =
    aisEvent.speed <= maxSpeedKnot

  def isShortDistance(aisEventA: AisEvent, aisEventB: AisEvent): Boolean = {
    computeDistanceInMeter(aisEventA, aisEventB) <= maxDistanceMeter
  }

  def isSimilarHeading(aisEventA: AisEvent, aisEventB: AisEvent): Boolean = {
    Math.abs(aisEventA.heading - aisEventB.heading) <= headingGap
  }

  def isSimilarSpeed(aisEventA: AisEvent, aisEventB: AisEvent): Boolean = {
    Math.abs(aisEventA.speed - aisEventB.speed) <= speedGap
  }

  def computeDistanceInMeter(aisEventA: AisEvent, aisEventB: AisEvent): Int = {

    val latDistance: Double = Math.toRadians(aisEventA.latitude - aisEventB.latitude)
    val lngDistance: Double = Math.toRadians(aisEventA.longitude - aisEventB.longitude)

    val sinLat: Double = Math.sin(latDistance / 2)
    val sinLng: Double = Math.sin(lngDistance / 2)

    val a = sinLat * sinLat + (
      Math.cos(Math.toRadians(aisEventA.latitude)) *
        Math.cos(Math.toRadians(aisEventB.latitude)) *
        sinLng * sinLng)

    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    (AVERAGE_RADIUS_OF_EARTH_METER * c).toInt
  }
}
