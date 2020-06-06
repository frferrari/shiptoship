package com.kpler.sts.challenge.serializer

import java.util

import argonaut.Argonaut._
import com.kpler.sts.challenge.model.AisEvent
import org.apache.kafka.common.serialization.Deserializer

class AisEventDeserializer extends Deserializer[AisEvent] {
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {
  }

  override def deserialize(s: String, bytes: Array[Byte]): AisEvent = {
    new String(bytes).decodeOption[AisEvent].get // TODO FIX
  }

  override def close(): Unit = {
  }
}
