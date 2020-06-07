package com.kpler.sts.challenge

import java.nio.file.Files
import java.sql.Timestamp
import java.time.Duration
import java.util.Properties

import com.kpler.sts.challenge.aggregation.AisEventAggregator
import com.kpler.sts.challenge.model.{AisEvent, AisEventAggregate, StsEvent, StsEventKey}
import com.kpler.sts.challenge.utils.{EventTimeExtractor, StsConfig, StsConfigParser}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig
import org.apache.kafka.streams.kstream.{Suppressed, TimeWindows}
import org.apache.kafka.streams.scala.kstream.{KStream, Produced}
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder, kstream}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.slf4j.{Logger, LoggerFactory}

object StsConsumer {

  type HarborId = String
  type EventId = Long
  type VesselId = Long
  type PositionId = Long
  type Latitude = Double
  type Longitude = Double
  type EventTime = Timestamp
  type Speed = Double
  type Course = Double
  type Heading = Int
  type Draught = Double

  def main(args: Array[String]): Unit = {
    import com.kpler.sts.challenge.serdes.AisEventSerdes._
    import com.kpler.sts.challenge.serdes.StsEventKeySerdes._
    import com.kpler.sts.challenge.serdes.StsEventSerdes._

    val logger: Logger = LoggerFactory.getLogger(StsConsumer.getClass)
    val config: Config = ConfigFactory.load().getConfig("dev")

    val stsConfigParser: StsConfigParser = new StsConfigParser
    val stsConfig: StsConfig =
      stsConfigParser
        .parser
        .parse(args, StsConfig())
        .getOrElse {
          val inputTopic: String = config.getString("inputTopic")
          val outputTopic: String = config.getString("outputTopic")

          StsConfig(inputTopic, outputTopic)
        }

    val props: Properties = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "StsApp")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("bootstrap.server"))
    props.put(StreamsConfig.CLIENT_ID_CONFIG, "StsConsumer")
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass)
    props.put(StreamsConfig.STATE_DIR_CONFIG, Files.createTempDirectory("sts-state").toAbsolutePath.toString)
    props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, classOf[EventTimeExtractor])

    // Stream builder
    val builder: StreamsBuilder = new StreamsBuilder
    val aisEvents: KStream[HarborId, String] =
      builder.stream[HarborId, String](stsConfig.inputTopic)(kstream.Consumed.`with`(Serdes.String, Serdes.String))

    // Windows
    val windowDuration: Duration = java.time.Duration.ofMinutes(stsConfig.windowSize)
    val overlapDuration: Duration = java.time.Duration.ofMinutes(stsConfig.windowOverlap)
    val graceDuration: Duration = java.time.Duration.ofMinutes(stsConfig.windowGrace)
    val hoppingWindow: TimeWindows = TimeWindows.of(windowDuration).advanceBy(overlapDuration).grace(graceDuration)

    // Aggregator
    val aisEventAggregator: AisEventAggregator =
      new AisEventAggregator(stsConfig.maxSpeedKnot, stsConfig.maxDistanceMeter, stsConfig.headingGap, stsConfig.speedGap, stsConfig.timeGap)

    val aggregator = (harborId: HarborId, aisEvent: AisEvent, stsDetector: AisEventAggregate) => {
      aisEventAggregator.aggregate(harborId, aisEvent, stsDetector)
    }

    aisEvents
      .flatMapValues((harborId, record) => AisEvent(harborId, record))
      .groupByKey(kstream.Grouped.`with`(Serdes.String, aisEventSerdes))
      .windowedBy(hoppingWindow)
      .aggregate(AisEventAggregate())(aggregator)
      .toStream
      .flatMapValues(stsDetector => stsDetector.stsEvents.map(e => StsEvent(e._1, e._2)))
      .groupBy((wk, stsEvent) => StsEventKey(wk.key(), stsEvent.sts1.eventId, stsEvent.sts2.eventId))(kstream.Grouped.`with`(stsEventKeySerdes, stsEventSerdes))
      .reduce((l, _) => l)
      .suppress(Suppressed.untilTimeLimit(Duration.ofMinutes(2), BufferConfig.unbounded()))
      .toStream
      .peek((k, v) => println(s"k=$k v=$v"))
      .to(stsConfig.outputTopic)(Produced.`with`(stsEventKeySerdes, stsEventSerdes))

    val streams = new KafkaStreams(builder.build(), props)
    streams.start()

    sys.ShutdownHookThread {
      streams.close(Duration.ofSeconds(10))
    }
  }
}
