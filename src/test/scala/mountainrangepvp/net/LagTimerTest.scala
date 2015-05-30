package mountainrangepvp.net

import java.time.Duration

import mountainrangepvp.server.LagTimer
import org.junit.Test

class LagTimerTest {

  @Test
  def firstStartGivesId() = {
    var expected: Option[Int] = None
    LagTimer().start(0, id => expected = Some(id))

    assert(expected.isDefined)
  }

  @Test
  def secondStartDoesNothing() = {
    var expected: Option[Int] = None
    LagTimer().start(0, _ => {}).start(0, id => expected = Some(id))

    assert(expected.isEmpty)
  }

  @Test
  def firstStopGivesTime() = {
    var expected: Option[Duration] = None
    withStarted { case (t, id) =>
      t.stop(id, 1, time => expected = Some(time))
    }

    assert(expected == Some(Duration.ofMillis(1)))
  }

  @Test
  def stoppingAnUnstartedTimerDoesNothing() = {
    var expected: Option[Duration] = None
    LagTimer().stop(0, 1, time => expected = Some(time))

    assert(expected.isEmpty)
  }

  @Test
  def stoppingAStoppedTimerDoesNothing() = {
    var expected: Option[Duration] = None
    withStarted { case (t, id) =>
      t.stop(id, 1, _ => {})
       .stop(id, 1, time => expected = Some(time))
    }

    assert(expected.isEmpty)
  }

  private def withStarted(f: (LagTimer, Int) => LagTimer): LagTimer = {
    var i: Int = 0
    val t = LagTimer().start(0, id => i = id)
    f(t, i)
  }
}
