package com.kpler.sts.challenge.utils

case class StsConfig(inputTopic: String = "",
                     outputTopic: String = "",
                     maxSpeedKnot: Double = 5.0,
                     maxDistanceMeter: Int = 100,
                     headingGap: Double = 5.0,
                     speedGap: Double = 0.3,
                     timeGap: Int = 300,
                     windowSize: Int = 10,
                     windowOverlap: Int = 2,
                     windowGrace: Int = 2)
