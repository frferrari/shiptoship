package com.kpler.sts.challenge.serializer

import java.util

import argonaut.Argonaut._
import com.kpler.sts.challenge.model.StsEvent
import org.apache.kafka.common.serialization.Deserializer

class StsEventDeserializer extends Deserializer[StsEvent] {
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {
  }

  override def deserialize(s: String, bytes: Array[Byte]): StsEvent = {
    new String(bytes).decodeOption[StsEvent].get // TODO FIX
  }

  override def close(): Unit = {
  }
}
