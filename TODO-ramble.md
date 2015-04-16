Separate game and engine. Actually split into two separate packages.
  split InputHandler into raw interface, action mapping, and game actions

Use in-game message pipe for cohosted server/client.

ClientGame.java should go. An in-game pipe should facilitate this.

# Use EventBus for ShotManager. Rework protocol to handle Input -> Server -> ShotManager
  (then do some prediction)

Merge Client/ServerPlayerManager

Record old video

Split Player into player and player entity

Player and shot should be an Entity (manual component based)

Calculate network lag.
