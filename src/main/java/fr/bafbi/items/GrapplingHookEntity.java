package fr.bafbi.items;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Instance;

public class GrapplingHookEntity extends Entity {

    private boolean isHooked = false;

    public GrapplingHookEntity() {
        super(EntityType.SLIME);
        setInvisible(true);
        setInvisible(true);
        setGlowing(true);
    }

    @Override
    public void update(long time) {
        // remove if to old
        if (getAliveTicks() > 20 * 5) {
            remove();
            return;
        }

        if (!isHooked) {
            // stop if colliding with a block
            BoundingBox baseBoundingBox = getBoundingBox();
            BoundingBox boundingBox = baseBoundingBox.expand(0.2f, 0.2f, 0.2f).withOffset(new Vec(baseBoundingBox.minX() - 0.1, baseBoundingBox.minY() - 0.1, baseBoundingBox.minZ() - 0.1));
            Instance instance = getInstance();

            for (BoundingBox.PointIterator it = boundingBox.getBlocks(getPosition()); it.hasNext(); ) {
                var point = it.next();
                if (!instance.getBlock(point.blockX(), point.blockY(), point.blockZ()).isAir()) {
                    setVelocity(Vec.ZERO);
                    setNoGravity(true);
                    isHooked = true;
                    break;
                }
            }
        } else {
            // give velocity to the players around
            final Entity player = getLeashHolder();
            assert player != null;
            Vec hookPosition = getPosition().asVec();
            Vec playerPosition = player.getPosition().asVec();
            Vec direction = playerPosition.sub(hookPosition).normalize();
            double distance = playerPosition.distance(hookPosition);

            // Calculate the pendulum force
            double pendulumForce = -0.1 * distance; // Adjust the constant to control the force
            Vec force = direction.mul(pendulumForce);

            // Apply the force to the player
            player.setVelocity(force);
        }
    }
}
