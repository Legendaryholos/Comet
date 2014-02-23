package com.cometproject.server.game.items.interactions.football;

import com.cometproject.server.game.items.interactions.Interactor;
import com.cometproject.server.game.rooms.avatars.misc.Position3D;
import com.cometproject.server.game.rooms.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.items.FloorItem;
import com.cometproject.server.game.rooms.items.RoomItem;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.utilities.DistanceCalculator;
import com.cometproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import javolution.util.FastList;

import java.util.List;

public class BallInteraction extends Interactor {
    @Override
    public boolean onWalk(boolean state, RoomItem item, PlayerEntity avatar) {
        FloorItem floorItem = (FloorItem) item;

        if(((FloorItem) item).isRolling())
            return false;

        Position3D currentPosition = new Position3D(floorItem.getX(), floorItem.getY(), floorItem.getHeight());

        Position3D newPosition = calculatePosition(floorItem.getX(), floorItem.getY(), avatar.getBodyRotation());
        newPosition.setZ(floorItem.getHeight());

        roll(floorItem, currentPosition, newPosition, avatar.getRoom());

        floorItem.setX(newPosition.getX());
        floorItem.setY(newPosition.getY());

        floorItem.setNeedsUpdate(true);
        return false;
    }

    @Override
    public boolean onPreWalk(RoomItem item, PlayerEntity avatar) {
        if (!(item instanceof FloorItem)) {
            return false;
        }

        if(((FloorItem) item).isRolling())
            return false;

        FloorItem floorItem = (FloorItem) item;
        Position3D currentPosition = new Position3D(floorItem.getX(), floorItem.getY(), floorItem.getHeight());

        Position3D newPosition = calculatePosition(floorItem.getX(), floorItem.getY(), avatar.getBodyRotation());
        newPosition.setZ(floorItem.getHeight());

        roll(floorItem, currentPosition, newPosition, avatar.getRoom());

        floorItem.setRotation(avatar.getBodyRotation());
        floorItem.setX(newPosition.getX());
        floorItem.setY(newPosition.getY());

        floorItem.setNeedsUpdate(true);
        return false;
    }

    public static void roll(FloorItem item, Position3D from, Position3D to, Room room) {
        room.getEntities().broadcastMessage(
                SlideObjectBundleMessageComposer.compose(
                        from,
                        to, item.getId(), 0, item.getId())
        );
    }

    private Position3D calculatePosition(int x, int y, int playerRotation) {
        switch(playerRotation) {
            case 0:
                y--;
                break;

            case 1:
                x++;
                y--;
                break;

            case 2:
                x++;
                break;

            case 3:
                x++;
                y++;
                break;

            case 4:
                y++;
                break;

            case 5:
                x--;
                y++;
                break;

            case 6:
                x--;
                break;

            case 7:
                x--;
                y--;
                break;
        }

        return new Position3D(x, y, 0d);
    }

    public static final int KICK_POWER = 6;

    @Override
    public boolean onInteract(int request, RoomItem item, PlayerEntity avatar) {
        if (!(item instanceof FloorItem)) {
            return false;
        }

        if(((FloorItem) item).isRolling())
            return false;

        if(!DistanceCalculator.tilesTouching(item.getX(), item.getY(), avatar.getPosition().getX(), avatar.getPosition().getY()))
            return false;

        FloorItem floorItem = (FloorItem) item;
        List<Position3D> positions = new FastList<>();

        Position3D currentPosition = new Position3D(floorItem.getX(), floorItem.getY(), floorItem.getHeight());
        Position3D lastPosition = null;

        for(int i = 0; i < KICK_POWER; i++) {
            Position3D kickPosition;

            if(lastPosition == null) {
                kickPosition = calculatePosition(currentPosition.getX(), currentPosition.getY(), avatar.getBodyRotation());
            } else {
                kickPosition = calculatePosition(lastPosition.getX(), lastPosition.getY(), avatar.getBodyRotation());
            }

            positions.add(kickPosition);
            lastPosition = kickPosition;
        }

        floorItem.setRollingPositions(positions);

        return false;
    }

    @Override
    public boolean onPlace(RoomItem item, PlayerEntity avatar, Room room) {
        return false;
    }

    @Override
    public boolean onPickup(RoomItem item, PlayerEntity avatar, Room room) {
        return false;
    }

    @Override
    public boolean onTick(RoomItem item) {
        return false;
    }

    @Override
    public boolean requiresRights() {
        return false;
    }
}
