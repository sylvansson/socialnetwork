package com.github.sylvansson.socialnetwork

import com.github.sylvansson.socialnetwork.Endpoints._
import com.twitter.finagle.Http
import com.twitter.util.Await

object Service extends App {
  Await.ready(
    Http.serve(":8080", service)
  )
}
