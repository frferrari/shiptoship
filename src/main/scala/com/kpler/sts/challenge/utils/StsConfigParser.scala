package com.kpler.sts.challenge.utils

import scopt.OptionParser

class StsConfigParser {
  val parser: OptionParser[StsConfig] =
    new scopt.OptionParser[StsConfig]("scopt") {
      head("scopt", "3.x")

      opt[String]('i', "inputTopic").action((x, c) =>
        c.copy(inputTopic = x)).text("inputTopic like ais")

      opt[String]('o', "outputTopic").action((x, c) =>
        c.copy(outputTopic = x)).text("outputTopic like sts")

      opt[Double]('s', "maxSpeed").action((x, c) =>
        c.copy(maxSpeedKnot = x)).text("The maximum speed in knots of vessels operating STS")

      opt[Int]('d', "maxDistance").action((x, c) =>
        c.copy(maxDistanceMeter = x)).text("The maximum distance in meters of 2 vessels operating STS")

      opt[Double]('h', "headingGap").action((x, c) =>
        c.copy(headingGap = x)).text("The maximum heading difference of 2 vessels operating STS")

      opt[Double]('g', "speedGap").action((x, c) =>
        c.copy(speedGap = x)).text("The maximum speed difference of 2 vessels operating STS")

      opt[Int]('t', "timeGap").action((x, c) =>
        c.copy(timeGap = x)).text("The maximum event time difference of 2 vessels operating STS (in seconds)")

      opt[Int]('w', "window").action((x, c) =>
        c.copy(windowSize = x)).text("The window size in which AIS events should appear to capture STS events (in minutes)")

      opt[Int]('v', "overlap").action((x, c) =>
        c.copy(windowOverlap = x)).text("The window overlap (in minutes)")

      opt[Int]('c', "grace").action((x, c) =>
        c.copy(windowGrace = x)).text("The window grace period (in minutes)")
    }
}
