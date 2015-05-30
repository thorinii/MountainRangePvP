package mountainrangepvp.core

object ChatLine {
  val OLD_TIME = 8000
}

case class ChatLine(player: Option[ClientId], text: String, time: Long) {

  def isOld =
    System.currentTimeMillis - time > ChatLine.OLD_TIME

  override def toString =
    player + ": " + text

}
