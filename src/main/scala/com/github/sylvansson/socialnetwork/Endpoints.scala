package com.github.sylvansson.socialnetwork
import java.util.UUID

import com.github.sylvansson.socialnetwork.Responses._
import com.twitter.util.Future
import io.circe.generic.auto._
import io.finch.circe._
import io.finch.syntax.{get, post}
import io.finch.{Endpoint, Ok, param, _}

object Endpoints {
  def service = friendships.toService

  private def friendships = {
    val listAcceptedFriendships: Endpoint[Success[Seq[Friendship]]] =
      get("friendships.accepted" :: param[UUID]("user")) { userId: UUID =>
        Future(Friendship.findAccepted(userId))
          .map(fs => Ok(Success("acceptedFriendships", fs)))
      }

    val listPendingFriendships: Endpoint[Success[Seq[Friendship]]] =
      get("friendships.pending" :: param[UUID]("user")) { userId: UUID =>
        Future(Friendship.findPending(userId))
          .map(fs => Ok(Success("pendingFriendships", fs)))
      }

    // TODO: Ensure that only the requestee can accept a friendship,
    //   once authentication has been implemented.
    val acceptFriendship: Endpoint[EmptySuccess] =
      post("friendships.accept" :: param[UUID]("requester") :: param[UUID]("requestee")) {
        (requesterId: UUID, requesteeId: UUID) =>
          Future(Friendship.accept(requesterId, requesteeId))
            .map(_ => Ok(EmptySuccess()))
      }

    listAcceptedFriendships :+:
    listPendingFriendships :+:
    acceptFriendship
  }
}
