package com.kpler.sts.challenge.serdes

import com.kpler.sts.challenge.model.AisEvent
import com.kpler.sts.challenge.serializer._
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.kstream.Materialized
import org.apache.kafka.streams.scala.{ByteArrayKeyValueStore, Serdes}

object AisEventSerdes {
  import org.apache.kafka.common.serialization.{Serdes => JSerdes}

  val aisEventSerdes: Serde[AisEvent] = JSerdes.serdeFrom(new AisEventSerializer, new AisEventDeserializer)

  implicit val aisEventMaterializer: Materialized[Long, AisEvent, ByteArrayKeyValueStore] =
    Materialized.`with`[Long, AisEvent, ByteArrayKeyValueStore](Serdes.Long, aisEventSerdes)
}
