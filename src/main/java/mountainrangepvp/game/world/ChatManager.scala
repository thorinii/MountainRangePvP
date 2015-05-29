package mountainrangepvp.game.world

class ChatManager {
  private var lines: List[ChatLine] = List.empty
  private var chatting: Boolean = false
  private var currentLine: String = null

  def getLinesHead(length: Int): List[ChatLine] = lines.take(length)

  def isChatting = chatting

  def getCurrentLine = currentLine
}
