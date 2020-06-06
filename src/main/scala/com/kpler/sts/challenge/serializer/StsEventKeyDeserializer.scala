package com.kpler.sts.challenge.serializer

import java.util

import argonaut.Argonaut._
import com.kpler.sts.challenge.model.StsEventKey
import org.apache.kafka.common.serialization.Deserializer

class StsEventKeyDeserializer extends Deserializer[StsEventKey] {
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {
  }

  override def deserialize(s: String, bytes: Array[Byte]): StsEventKey = {
    new String(bytes).decodeOption[StsEventKey].get // TODO FIX
  }

  override def close(): Unit = {
  }
}
