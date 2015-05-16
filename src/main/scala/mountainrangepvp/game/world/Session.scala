package mountainrangepvp.game.world

import com.badlogic.gdx.math.Vector2
import mountainrangepvp.engine.util.{EventBus, Log}

/**
 * State that doesn't change between maps.
 */
class Session(log: Log, eventBus: EventBus,
              localId: ClientId, baseSnapshot: Snapshot,
              val playerManager: PlayerManager, val chatManager: ChatManager) {
  private var snapshot = baseSnapshot
  private var camera = new Camera(new Vector2(0, 0))
  private var terrain: Terrain = null

  eventBus.subscribe((e: SnapshotEvent) => updateSnapshot(e.snapshot))
  updateSnapshot(baseSnapshot)

  private def updateSnapshot(s: Snapshot) = {
    snapshot = s
    if (terrain == null || terrain.getSeed != s.seed) {
      val heightMap: HeightMap = new HillsHeightMap(s.seed)
      terrain = new Terrain(heightMap)
    }
  }

  def getSnapshot = snapshot

  def getTerrain = terrain

  def getCameraCentre = camera.centre

  def localPlayerEntity: Option[PlayerEntity] = snapshot.getPlayerEntity(localId)

  def update(dt: Float) = {
    localPlayerEntity.foreach { p => camera = camera.centreOn(p) }
  }
}
