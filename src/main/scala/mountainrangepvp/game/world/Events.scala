package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.Event


case class ConnectedEvent(localId: ClientId) extends Event


case class SnapshotEvent(snapshot: Snapshot) extends Event


case class InputCommandEvent(command: InputCommand) extends Event


case object ServerDisconnect extends Event



case class InputCommand(fire: Boolean, aimDirection: Vector2)
