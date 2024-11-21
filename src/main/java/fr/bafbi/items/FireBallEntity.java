package fr.bafbi.items;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Instance;

public class FireBallEntity extends Entity {



    public FireBallEntity() {
        super(EntityType.FIREBALL);
    }

    @Override
    public void update(long time) {
        // remove if to old
        if (getAliveTicks() > 20 * 5) {
            remove();
            return;
        }
        // remove if bounding box is in a block
        BoundingBox boundingBox = getBoundingBox().expand(0.2f, 0.2f, 0.2f).withOffset(new Vec(-0.6, -0.1, -0.6));
        Instance instance = getInstance();
        boolean inBlock = false;
        for (BoundingBox.PointIterator it = boundingBox.getBlocks(getPosition()); it.hasNext(); ) {
            var point = it.next();
            if (!instance.getBlock(point.blockX(), point.blockY(), point.blockZ()).isAir()) {
                inBlock = true;
                remove();
                break;
            }
        }
        if (inBlock) {
            // give velocity to the players around
            for (var player : instance.getPlayers()) {
                if (player.getDistance(getPosition()) < 10) {
                    // get the direction of the player to the fireball
                    Vec direction = getPosition().sub(player.getPosition()).direction();
                    // the closer the player is, the more the player is pushed
                    player.setVelocity(player.getVelocity().add(direction.mul(-20)));
                }
            }
        }

    }
}
