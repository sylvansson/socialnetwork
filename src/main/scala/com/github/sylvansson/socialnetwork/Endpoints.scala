package com.github.sylvansson.socialnetwork

import java.util.UUID

import com.github.sylvansson.socialnetwork.Responses._
import com.twitter.util.Future
import com.typesafe.config.Config
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._
import io.finch.syntax._
import pdi.jwt._

object Endpoints {
  def service(implicit config: Config) =
    (friendships :+: posts)
      .handle {
        case e: Exception => BadRequest(e)
      }
      .toService

  /**
   * Validate the caller's token and extract their user id.
   * @return The user's id.
   */
  def authorize(implicit config: Config): Endpoint[UUID] = {
    header("Authorization").mapOutput({
      case header if header.startsWith("Bearer") =>
        val _ :: token :: Nil = header.split(" ").toList
        val key = config.getString("jwt.secret")
        JwtCirce.decodeJson(token, key, Seq(JwtAlgorithm.HS256))
          .toOption
          .map(_.hcursor.get[UUID]("userId")) match {
            case Some(Right(userId)) => Ok(userId)
            case _ => BadRequest(new Exception("Invalid Bearer token."))
          }
    })
  }

  private def friendships(implicit config: Config) = {
    val listAcceptedFriendships: Endpoint[Success[Seq[Friendship]]] =
      get("friendships.accepted" :: authorize) { callerId: UUID =>
        Future(Friendship.findAccepted(callerId))
          .map(fs => Ok(Success("acceptedFriendships", fs)))
      }

    val listPendingFriendships: Endpoint[Success[Seq[Friendship]]] =
      get("friendships.pending" :: authorize) { callerId: UUID =>
        Future(Friendship.findPending(callerId))
          .map(fs => Ok(Success("pendingFriendships", fs)))
      }

    val acceptFriendship: Endpoint[EmptySuccess] =
      post("friendships.accept" :: param[UUID]("requester") :: authorize) {
        (requesterId: UUID, callerId: UUID) =>
          Future(Friendship.accept(requesterId, callerId))
            .map(_ => Ok(EmptySuccess()))
      }

    val requestFriendship: Endpoint[EmptySuccess] =
      post("friendships.request" :: param[UUID]("requestee") :: authorize) {
        (requesteeId: UUID, callerId: UUID) =>
          Future(Friendship.request(callerId, requesteeId))
            .map(_ => Ok(EmptySuccess()))
      }

    listAcceptedFriendships :+:
    listPendingFriendships :+:
    acceptFriendship :+:
    requestFriendship
  }

  private def posts(implicit config: Config) = {
    val createPost: Endpoint[Success[Post]] =
      post("posts.create" :: param[String]("content") :: authorize) {
        (content: String, callerId: UUID) =>
          Future(Post.create(callerId, content))
            .map(p => Ok(Success("post", p)))
      }

    createPost
  }
}
