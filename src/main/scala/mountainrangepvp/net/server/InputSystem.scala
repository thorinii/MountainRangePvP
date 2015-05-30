package mountainrangepvp.net.server

import com.badlogic.gdx.math.MathUtils
import mountainrangepvp.core.{ClientId, InputCommand, PlayerEntity, Snapshot}

/**
 * Created by lachlan on 21/05/15.
 */
class InputSystem(idGenerator: () => Long) {
  private var clientInputState: Map[ClientId, InputState] = Map.empty

  def join(id: ClientId) =
    clientInputState += id -> InputState()

  def leave(id: ClientId) =
    clientInputState -= id

  def applyCommand(id: ClientId, command: InputCommand) =
    clientInputState += id -> clientInputState(id).accumulate(command)


  def process(dt: Float, snapshot: Snapshot): Snapshot = {
    var nextSnapshot = snapshot

    clientInputState = clientInputState.map { case (id, state) =>
      val (snapshot, nextState) = processPlayer(dt, nextSnapshot, id, state)
      nextSnapshot = snapshot
      id -> nextState
    }

    nextSnapshot
  }

  private def processPlayer(dt: Float, snapshot: Snapshot, id: ClientId, state: InputState) = {
    var next = snapshot
    next = next.updatePlayer(id, e => processPlayerMovement(state, e))

    if (state.firing)
      next = next.addShot(idGenerator(), id, state.aimPoint.cpy().add(0, -PlayerEntity.GunHeight).nor())

    (next, state.nextFrame(dt))
  }

  private def processPlayerMovement(state: InputState, e: PlayerEntity): PlayerEntity = {
    val newVel = e.velocity.cpy()

    if (newVel.x.abs <= PlayerEntity.RunSpeed)
      newVel.x = MathUtils.lerp(newVel.x, state.run * PlayerEntity.RunSpeed, if (e.onGround) 0.5f else 0.1f)

    if (state.jump && e.onGround)
      newVel.y += PlayerEntity.JumpImpulse

    e.copy(aim = state.aimPoint.cpy().add(0, -PlayerEntity.GunHeight).nor(),
           velocity = newVel)
  }
}
