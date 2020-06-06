package com.kpler.sts.challenge.model

import argonaut.Argonaut.casecodec3
import argonaut.CodecJson
import com.kpler.sts.challenge.StsConsumer.{EventId, HarborId}

case class StsEventKey(harbordId: HarborId, eventId1: EventId, eventId2: EventId)

object StsEventKey {
  implicit def StseEventKeyCodecJson: CodecJson[StsEventKey] =
    casecodec3(StsEventKey.apply, StsEventKey.unapply)("harborId", "eventId1", "eventId2")
}
