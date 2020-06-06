package com.kpler.sts.challenge.serializer

import java.util

import argonaut.Argonaut._
import com.kpler.sts.challenge.model.Sts
import org.apache.kafka.common.serialization.Deserializer

class StsDeserializer extends Deserializer[Sts] {
  override def configure(map: util.Map[String, _], b: Boolean): Unit = {
  }

  override def deserialize(s: String, bytes: Array[Byte]): Sts = {
    new String(bytes).decodeOption[Sts].get // TODO FIX
  }

  override def close(): Unit = {
  }
}
