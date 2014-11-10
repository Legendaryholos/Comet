package com.cometproject.server.network.messages.outgoing.room.avatar;

import com.cometproject.server.game.rooms.objects.entities.GenericEntity;
import com.cometproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.cometproject.server.network.messages.headers.Composers;
import com.cometproject.server.network.messages.types.Composer;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AvatarUpdateMessageComposer {
    public static Composer compose(int count, Collection<GenericEntity> list) {
        Composer msg = new Composer(Composers.UpdateUserStatusMessageComposer);

        msg.writeInt(count);

        for (GenericEntity entity : list) {
            composeEntity(msg, entity);
        }

        return msg;
    }

    public static Composer compose(List<GenericEntity> list) {
        return compose(list.size(), list);
    }

    public static Composer compose(GenericEntity entity) {
        Composer msg = new Composer(Composers.UpdateUserStatusMessageComposer);

        msg.writeInt(1);

        composeEntity(msg, entity);

        return msg;
    }

    private static void composeEntity(Composer msg, GenericEntity entity) {
        if (!entity.isVisible()) return;

        msg.writeInt(entity.getId());

        msg.writeInt(entity.getPosition().getX());
        msg.writeInt(entity.getPosition().getY());
        msg.writeString(String.valueOf(entity.getPosition().getZ()));

        msg.writeInt(entity.getHeadRotation());
        msg.writeInt(entity.getBodyRotation());

        StringBuilder statusString = new StringBuilder();
        statusString.append("/");

        for (Map.Entry<RoomEntityStatus, String> status : entity.getStatuses().entrySet()) {

            statusString.append(status.getKey().getStatusCode());

            if (!status.getValue().isEmpty()) {
                statusString.append(" ");
                statusString.append(status.getValue());
            }

            statusString.append("/");
        }

        statusString.append("/");

        msg.writeString(statusString.toString());
    }
}
