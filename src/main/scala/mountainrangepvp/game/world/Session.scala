package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.{EventBus, Log}

/**
 * State that doesn't change between maps.
 */
class Session(log: Log, eventBus: EventBus,
              localId: ClientId,
              val playerManager: PlayerManager, val chatManager: ChatManager) {
  private var snapshot: Snapshot = null
  private var terrain: Terrain = null
  private var camera: Camera = new Camera(new Vector2(0, 0))

  eventBus.subscribe((e: SnapshotEvent) => {
    snapshot = e.snapshot
    if (terrain == null || terrain.getSeed != snapshot.seed) {
      val heightMap: HeightMap = new HillsHeightMap(snapshot.seed)
      terrain = new Terrain(heightMap)
    }
  })


  def getSnapshot: Snapshot = {
    if (snapshot == null) throw new IllegalStateException("No snapshot available")
    snapshot
  }

  def hasSnapshot: Boolean = {
    snapshot != null
  }

  def getTerrain: Terrain = {
    if (terrain == null) throw new IllegalStateException("No terrain available")
    terrain
  }

  def getCameraCentre: Vector2 = camera.centre

  private def localPlayer: Option[PlayerEntity] = snapshot.getPlayerEntity(localId)

  def update(dt: Float) = {
    localPlayer.foreach { p => camera = camera.centreOn(p) }
  }
}
