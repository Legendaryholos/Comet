package com.cometproject.server.network.messages.incoming.room.action;

import com.cometproject.server.game.rooms.objects.entities.RoomEntityType;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.network.messages.incoming.IEvent;
import com.cometproject.server.network.messages.outgoing.room.avatar.UpdateIgnoreStatusMessageComposer;
import com.cometproject.server.network.messages.types.Event;
import com.cometproject.server.network.sessions.Session;

public class UnignoreUserMessageEvent implements IEvent {
    @Override
    public void handle(Session client, Event msg) throws Exception {
        String username = msg.readString();

        if(client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        PlayerEntity playerEntity = (PlayerEntity) client.getPlayer().getEntity().getRoom().getEntities().getEntityByName(username, RoomEntityType.PLAYER);
        client.getPlayer().unignorePlayer(playerEntity.getPlayerId());
        client.send(UpdateIgnoreStatusMessageComposer.compose(3, username));
    }
}
