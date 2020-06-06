package com.kpler.sts.challenge.serdes

import com.kpler.sts.challenge.model.Sts
import com.kpler.sts.challenge.serializer._
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.kstream.Materialized
import org.apache.kafka.streams.scala.{ByteArrayKeyValueStore, Serdes}

object StsSerdes {
  import org.apache.kafka.common.serialization.{Serdes => JSerdes}

  val stsSerdes: Serde[Sts] = JSerdes.serdeFrom(new StsSerializer, new StsDeserializer)

  implicit val stsMaterializer: Materialized[Int, Sts, ByteArrayKeyValueStore] =
    Materialized.`with`[Int, Sts, ByteArrayKeyValueStore](Serdes.Integer, stsSerdes)
}
