package com.kpler.sts.challenge.serializer

import java.util

import argonaut.Argonaut._
import com.kpler.sts.challenge.model.{AisEvent, AisEventAggregate}
import org.apache.kafka.common.serialization.Serializer

class StsDetectorSerializer extends Serializer[AisEventAggregate] {
  override def configure(map: util.Map[String, _], b: Boolean): Unit = ()

  override def serialize(s: String, t: AisEventAggregate): Array[Byte] = {
    if (t == null)
      null
    else {
      t.asJson.toString().getBytes
    }
  }

  override def close(): Unit = {
  }
}
