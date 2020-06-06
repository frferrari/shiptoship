package com.kpler.sts.challenge.serdes

import com.kpler.sts.challenge.model.{Sts, StsEvent}
import com.kpler.sts.challenge.serializer._
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.kstream.Materialized
import org.apache.kafka.streams.scala.{ByteArrayKeyValueStore, Serdes}

object StsEventSerdes {
  import org.apache.kafka.common.serialization.{Serdes => JSerdes}

  val stsEventSerdes: Serde[StsEvent] = JSerdes.serdeFrom(new StsEventSerializer, new StsEventDeserializer)

  implicit val stsEventMaterializer: Materialized[String, StsEvent, ByteArrayKeyValueStore] =
    Materialized.`with`[String, StsEvent, ByteArrayKeyValueStore](Serdes.String, stsEventSerdes)
}
