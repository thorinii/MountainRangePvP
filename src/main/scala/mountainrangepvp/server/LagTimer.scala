package mountainrangepvp.server

import java.time.Duration

import scala.util.Random

/**
 * Calculates the network ping time for a client.
 */
object LagTimer {
  def apply() = new LagTimer(None, None, 0)
}

class LagTimer(val lag: Option[Duration],
               private val _startTime: Option[Long], private val _id: Int) {

  def start(now: Long, startFunction: Int => Unit): LagTimer = {
    if (_startTime.isEmpty || _startTime.get - now > 3000) {
      val id = Random.nextInt()
      startFunction(id)
      new LagTimer(lag, Some(now), id)
    } else
      this
  }

  def stop(idToStop: Int, now: Long, stopFunction: Duration => Unit): LagTimer = {
    if (idToStop == _id && _startTime.isDefined) {
      val lag = Duration.ofMillis(now - _startTime.get)
      stopFunction(lag)
      new LagTimer(Some(lag), None, 0)
    } else
      this
  }
}
