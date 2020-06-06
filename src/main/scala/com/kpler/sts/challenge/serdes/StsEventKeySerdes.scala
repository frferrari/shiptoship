package com.kpler.sts.challenge.serdes

import com.kpler.sts.challenge.model.{StsEvent, StsEventKey}
import com.kpler.sts.challenge.serdes.StsEventSerdes.stsEventSerdes
import com.kpler.sts.challenge.serializer.{StsEventKeyDeserializer, StsEventKeySerializer}
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.{ByteArrayKeyValueStore, Serdes}
import org.apache.kafka.streams.scala.kstream.Materialized

object StsEventKeySerdes {
  import org.apache.kafka.common.serialization.{Serdes => JSerdes}

  val stsEventKeySerdes: Serde[StsEventKey] = JSerdes.serdeFrom(new StsEventKeySerializer, new StsEventKeyDeserializer)

  implicit val stsEventKeyMaterializer: Materialized[StsEventKey, StsEvent, ByteArrayKeyValueStore] =
    Materialized.`with`[StsEventKey, StsEvent, ByteArrayKeyValueStore](stsEventKeySerdes, stsEventSerdes)
}
