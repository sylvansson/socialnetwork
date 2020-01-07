package com.github.sylvansson

import java.time.LocalDateTime
import java.util.UUID

import io.getquill._

package object socialnetwork {
  val ctx = new PostgresJdbcContext(NamingStrategy(SnakeCase, PluralizedTableNames), "ctx")
  import ctx._

  case class User(id: UUID) {
    def friends = Friendship.findAccepted(id)
    def numFriends = friends.size
  }
  object User {
    val query = quote(ctx.query[User])

    def find(id: UUID): Option[User] =
      ctx.run(query.filter(_.id == lift(id))).headOption
  }

  /**
   * A friendship between two users.
   *
   * @param requesterId The id of the user who made the request.
   * @param requesteeId The id of the user who received the request.
   * @param since When the request was accepted, or None if the
   *              request is still pending.
   */
  case class Friendship(requesterId: UUID, requesteeId: UUID, since: Option[LocalDateTime])
  object Friendship {
    val query = quote(ctx.query[Friendship])

    private object Types extends Enumeration {
      val Pending, Accepted, All = Value
    }

    private def find(userId: UUID, `type`: Types.Value): Seq[Friendship] = {
      val base = quote(
        query.filter(f => f.requesterId == lift(userId) || f.requesteeId == lift(userId))
      )

      ctx.run(
        `type` match {
          case Types.Pending  => quote(base.filter(_.since.isEmpty))
          case Types.Accepted => quote(base.filter(_.since.isDefined))
          case Types.All      => base
        }
      )
    }

    def findPending(userId: UUID) = find(userId, Types.Pending)
    def findAccepted(userId: UUID) = find(userId, Types.Accepted)
    def findAll(userId: UUID) = find(userId, Types.All)
  }
}
