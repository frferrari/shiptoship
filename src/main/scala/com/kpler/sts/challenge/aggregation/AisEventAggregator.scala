package com.kpler.sts.challenge.aggregation

import com.kpler.sts.challenge.StsConsumer.{HarborId, VesselId}
import com.kpler.sts.challenge.model.{AisEvent, AisEventAggregate, Sts}

import scala.collection.immutable

case class AisEventAggregator(maxSpeedKnot: Double = 5.0,
                              maxDistanceMeter: Int = 100,
                              headingGap: Double = 5.0,
                              speedGap: Double = 0.3,
                              timeGap: Long = 300L) {

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

  /**
   * Goes through all ther opvided events and identifies STS events
   *
   * @param aisEventsPerVessel
   * @return
   */
  def findSts(aisEventsPerVessel: Map[VesselId, List[AisEvent]]): List[(Sts, Sts)] = {
    val closestEvents: immutable.Iterable[List[(Sts, Sts)]] =
      for {
        (vesselIdA, aisEventsA) <- aisEventsPerVessel
        (vesselIdB, aisEventsB) <- aisEventsPerVessel.filterNot(_._1 == vesselIdA)
      } yield findStsEvents(aisEventsA, aisEventsB)

    closestEvents
      .flatten
      .filter { case (l, r) => l.vesselId < r.vesselId }
      .toList
  }

  /**
   * Find the events that can be matched to produce STS event
   *
   * @param aisEventsA
   * @param aisEventsB
   * @return
   */
  def findStsEvents(aisEventsA: List[AisEvent], aisEventsB: List[AisEvent]): List[(Sts, Sts)] = {
    for {
      aisEventA <- aisEventsA
      aisEventB <- aisEventsB
      if (isStsEvent(aisEventA, aisEventB))
    } yield (Sts(aisEventA), Sts(aisEventB))
  }

  /**
   * Checks whether 2 events can be considered an STS event, based on a combination of conditions :
   *
   * @param aisEventA
   * @param aisEventB
   * @return true if STS event reported, false otherwise
   */
  def isStsEvent(aisEventA: AisEvent, aisEventB: AisEvent): Boolean = {
    isShortDistance(aisEventA, aisEventB) &&
      isSimilarHeading(aisEventA, aisEventB) &&
      isSimilarSpeed(aisEventA, aisEventB) &&
      isBelowMaxSpeed(aisEventA) &&
      isBelowMaxSpeed(aisEventB) &&
      isSimilarTimeSlot(aisEventA, aisEventB)
  }

  /**
   * Checks if the provided event has a speed that is lower than maxSpeedKnot parameter
   *
   * @param aisEvent
   * @return true is the event has a speed lower than maxSpeedKnow, false otherwise
   */
  def isBelowMaxSpeed(aisEvent: AisEvent): Boolean =
    aisEvent.speed <= maxSpeedKnot

  /**
   * Checks if the 2 provided events are close in distance based on maxDistanceMeter parameter
   *
   * @param aisEventA
   * @param aisEventB
   * @return true if the 2 events are close in distance, false otherwise
   */
  def isShortDistance(aisEventA: AisEvent, aisEventB: AisEvent): Boolean = {
    computeDistanceInMeter(aisEventA, aisEventB) <= maxDistanceMeter
  }

  /**
   * Checks if the 2 provided events are for headings that are close to each other based on headingGap
   *
   * @param aisEventA
   * @param aisEventB
   * @return true if the 2 events are close in heading, false otherwise
   */
  def isSimilarHeading(aisEventA: AisEvent, aisEventB: AisEvent): Boolean = {
    Math.abs(aisEventA.heading - aisEventB.heading) <= headingGap
  }

  /**
   * Checks if the 2 events are for speeds that are close to each other based on speedGap parameter
   *
   * @param aisEventA
   * @param aisEventB
   * @return true is the 2 events are close in speed, false otherwise
   */
  def isSimilarSpeed(aisEventA: AisEvent, aisEventB: AisEvent): Boolean = {
    Math.abs(aisEventA.speed - aisEventB.speed) <= speedGap
  }

  /**
   * Checks if 2 AIS events are close in time. How close the events are to each other depends on timeGap
   *
   * @param aisEventA
   * @param aisEventB
   * @return true is the 2 events are close in time, false otherwise
   */
  def isSimilarTimeSlot(aisEventA: AisEvent, aisEventB: AisEvent): Boolean = {
    (Math.abs(aisEventA.eventTime.getTime - aisEventB.eventTime.getTime) / 1000) <= timeGap
  }

  /**
   * Computes the distance in meters between two AIS events
   *
   * @param aisEventA first event
   * @param aisEventB second event
   * @return The distance in meter separating the provided events
   */
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
