package com.github.sylvansson.socialnetwork

import io.circe.{Encoder, Json}
import io.circe.syntax._

/**
 * Case classes for responses. The response format is based on Slack's
 * Web API: https://api.slack.com/web
 */
object Responses {
  case class EmptySuccess()
  object EmptySuccess {
    implicit def encode: Encoder[EmptySuccess] = (_: EmptySuccess) =>
      Json.obj("ok" -> Json.fromBoolean(true))
  }

  case class Success[T](property: String, data: T)
  object Success {
    implicit def encode[T](implicit encodeT: Encoder[T]): Encoder[Success[T]] = (s: Success[T]) =>
      Json.obj(
        "ok" -> Json.fromBoolean(true),
        s.property -> s.data.asJson
      )
  }
}
