package com.kpler.sts.challenge.serializer

import java.util

import argonaut.Argonaut._
import com.kpler.sts.challenge.model.Sts
import org.apache.kafka.common.serialization.Serializer

class StsSerializer extends Serializer[Sts] {
  override def configure(map: util.Map[String, _], b: Boolean): Unit = ()

  override def serialize(s: String, t: Sts): Array[Byte] = {
    if (t == null)
      null
    else {
      t.asJson.toString().getBytes
    }
  }

  override def close(): Unit = {
  }
}
