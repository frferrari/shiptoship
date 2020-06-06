package com.kpler.sts.challenge.model

import argonaut.Argonaut.casecodec2
import argonaut.CodecJson
import com.kpler.sts.challenge.StsConsumer.{EventId, HarborId}
import com.kpler.sts.challenge.serializer.{StsDetectorDeserializer, StsDetectorSerializer}
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.kstream.Materialized
import org.apache.kafka.streams.scala.{ByteArrayWindowStore, Serdes}

case class AisEventAggregate(aisEvents: List[AisEvent] = List.empty[AisEvent],
                             stsEvents: List[(Sts, Sts)] = List.empty[(Sts, Sts)])

object AisEventAggregate {
  import org.apache.kafka.common.serialization.{Serdes => JSerdes}

  val stsDetectorSerdes: Serde[AisEventAggregate] = JSerdes.serdeFrom(new StsDetectorSerializer, new StsDetectorDeserializer)

  implicit val stsDetectorMaterializer: Materialized[HarborId, AisEventAggregate, ByteArrayWindowStore] =
    Materialized.`with`[HarborId, AisEventAggregate, ByteArrayWindowStore](Serdes.String, stsDetectorSerdes)

  implicit def StsDetectorCodecJson: CodecJson[AisEventAggregate] =
    casecodec2(AisEventAggregate.apply, AisEventAggregate.unapply)("aisEvents", "stsEvents")
}
