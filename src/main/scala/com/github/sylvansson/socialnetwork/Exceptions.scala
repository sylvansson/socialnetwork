package com.github.sylvansson.socialnetwork

object Exceptions {
  class AuthenticationError extends Exception("invalid_token")
}
