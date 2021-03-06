package unknowndomain.engine.player;

import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.entity.Player;
import unknowndomain.engine.event.Cancellable;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.Chunk;
import unknowndomain.engine.world.World;

public class PlayerImpl implements Player {
    private Data data;
    private World world;
    private Entity mounting;

    public PlayerImpl(Data data, World world, Entity mounting) {
        this.data = data;
        this.world = world;
        this.mounting = mounting;
    }

    public void enter(Chunk.Store chunkStore) {
        int radius = this.data.trackingChunkRadius;
        Entity entity = this.getMountingEntity();
        BlockPos pos = BlockPos.of(entity.getPosition());
        for (int i = -radius; i <= radius; i++) {
            if (i <= 0) {
                int zOff = i + radius;
                for (int j = -zOff; j <= zOff; j++)
                    chunkStore.touchChunk(pos.add(i * 16, 0, j * 16));
            } else {
                int zOff = radius - i;
                for (int j = -zOff; j <= zOff; j++)
                    chunkStore.touchChunk(pos.add(i * 16, 0, j * 16));
            }
        }
    }

    @NonNull
    @Override
    public Entity getMountingEntity() {
        return mounting;
    }

    @NonNull
    @Override
    public Entity mountEntity(Entity entity) {
        Entity old = mounting;
        if (entity == null) {
            // create phantom entity if is god, else create default player
        }
        mounting = entity;
        // fire event;
        return old;
    }

    @Override
    public Player.Data getData() {
        return data;
    }

    @NonNull
    @Override
    public World getWorld() {
        return world;
    }

    public static class PlayerPlaceBlockEvent implements Event, Cancellable {
        public final PlayerImpl player;
        public final BlockPos position;
        private boolean cancelled;

        public PlayerPlaceBlockEvent(PlayerImpl player, BlockPos position) {
            this.player = player;
            this.position = position;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }
}
