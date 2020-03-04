package com.github.sylvansson.socialnetwork

import com.github.sylvansson.socialnetwork.Endpoints._
import com.twitter.finagle.Http
import com.twitter.util.Await
import com.typesafe.config.ConfigFactory

object Service extends App {
  implicit val config = ConfigFactory.load

  Await.ready(
    Http.serve(":8080", service)
  )
}
