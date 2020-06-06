package com.kpler.sts.challenge.serializer

import java.util

import argonaut.Argonaut._
import com.kpler.sts.challenge.model.{AisEvent, AisEventAggregate}
import org.apache.kafka.common.serialization.Deserializer

class StsDetectorDeserializer extends Deserializer[AisEventAggregate] {
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {
  }

  override def deserialize(s: String, bytes: Array[Byte]): AisEventAggregate = {
    new String(bytes).decodeOption[AisEventAggregate].get // TODO FIX
  }

  override def close(): Unit = {
  }
}
