package com.kpler.sts.challenge.model

import argonaut.Argonaut.casecodec2
import argonaut.CodecJson

case class StsEvent(sts1: Sts, sts2: Sts)

object StsEvent {
  implicit def StseEventCodecJson: CodecJson[StsEvent] =
    casecodec2(StsEvent.apply, StsEvent.unapply)("sts1", "sts2")
}

