package mountainrangepvp.net

/**
 * Unique identifiers for connected clients.
 */
case class ClientId(id: Long) {
  override def toString = "@(" + id + ')'
}
