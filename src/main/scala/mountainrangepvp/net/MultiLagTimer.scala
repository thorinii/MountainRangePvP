package mountainrangepvp.net

import java.time.Duration

import mountainrangepvp.core.ClientId

/**
 * Calculates the network ping time for several clients.
 */
object MultiLagTimer {
  def apply() = new MultiLagTimer(Map.empty)
}

class MultiLagTimer(private val timers: Map[ClientId, LagTimer]) {

  def lagFor(id: ClientId): Option[Duration] = timers.get(id).flatMap(_.lag)

  def start(id: ClientId, now: Long)(startFunction: Int => Unit): MultiLagTimer =
    withTimer(id, _.start(now, startFunction))

  def stop(clientId: ClientId, pingId: Int, now: Long)(stopFunction: Duration => Unit): MultiLagTimer =
    withTimer(clientId, _.stop(pingId, now, stopFunction))

  private def withTimer(id: ClientId, f: LagTimer => LagTimer): MultiLagTimer = {
    val timer = timers.getOrElse(id, LagTimer())
    val newTimer = f(timer)
    new MultiLagTimer(timers + (id -> newTimer))
  }
}
