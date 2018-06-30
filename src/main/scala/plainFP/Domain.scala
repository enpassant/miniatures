import java.time.Instant
import java.net.URI

case class Id[Entity](value: String) extends AnyVal

case class User(
  id: Id[User],
  updatedOn: Instant,
  image: URI,
  nickname: String,
  verified: Boolean,
  deleted: Boolean
)
case class Post(
  id: Id[Post],
  updatedOn: Instant,
  author: Id[User],
  text: String,
  image: URI,
  deleted: Boolean
)
case class Like(
  userId: Id[User],
  postId: Id[Post],
  updatedOn: Instant,
  unliked: Boolean
)
case class Comment(
  id: Id[Comment],
  postId: Id[Post],
  updatedOn: Instant,
  author: Id[User],
  text: String,
  deleted: Boolean
)

object Domain extends App {
  val userId = Id[User]("1")
  val postId = Id[Post]("1")
  val user = User(userId, Instant.now, new URI(""), "", false, false)
  val post = Post(postId, Instant.now, user.id, "", new URI(""), false)
}
