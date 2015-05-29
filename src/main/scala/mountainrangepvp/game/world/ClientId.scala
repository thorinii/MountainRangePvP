package mountainrangepvp.game.world

/**
 * Unique identifiers for connected clients.
 */
object ClientId {
  val Invalid = ClientId(-1)
}

case class ClientId(id: Long) {
  def isValid = id >= 0

  override def toString = "@" + id
}
