package unknowndomain.engine.entity;

import com.google.common.collect.Maps;
import org.joml.AABBd;
import org.joml.Vector3f;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.item.Item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class EntityImpl implements Entity {
    private int id;
    private Vector3f position, rotation;
    private Vector3f motion;
    private AABBd boundingBox;
    private Map<String, Object> behaviors;

    public EntityImpl(int id, Vector3f position, Vector3f rotation, Vector3f motion, AABBd boundingBox) {
        this.id = id;
        this.position = position;
        this.rotation = rotation;
        this.motion = motion;
        this.boundingBox = boundingBox;
        behaviors = Maps.newHashMap();
    }

    public Vector3f getPosition() {
        return position;
    }

    @Override
    public AABBd getBoundingBox() {
        return boundingBox;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void tick(GameContext context) {
//        UnknownDomain.getEngine().getWorld().getBlock();
//        camera.move(motion.x, motion.y, motion.z); // this should not be here...
    }

    @Override
    public Vector3f getRotation() {
        return rotation;
    }

    @Override
    public void destroy() {
    }

    @Override
    public Vector3f getMotion() {
        return motion;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull String name) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getBehavior(Class<T> type) {
        return (T) behaviors.get(type);
    }

    public static class TwoHandImpl implements TwoHands {
        private Item mainHand, offHand;

        public Item getMainHand() {
            return mainHand;
        }

        @Override
        public void setMainHand(Item mainHand) {
            this.mainHand = mainHand;
        }

        @Override
        public Item getOffHand() {
            return offHand;
        }

        @Override
        public void setOffHand(Item offHand) {
            this.offHand = offHand;
        }
    }
}
