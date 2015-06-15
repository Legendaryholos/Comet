package com.cometproject.server.network.messages.incoming.room.action;

import com.cometproject.server.game.quests.QuestType;
import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;
import com.cometproject.server.network.messages.types.MessageEvent;
import com.cometproject.server.network.sessions.Session;


public class ApplyActionMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer() != null && client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
            int actionId = msg.readInt();

            if (actionId == 5) {
                client.getPlayer().getEntity().setIdle();
            } else {
                client.getPlayer().getEntity().unIdle();
            }

            if (actionId == 1) {
                client.getPlayer().getQuests().progressQuest(QuestType.SOCIAL_WAVE);
            }

            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new ActionMessageComposer(client.getPlayer().getEntity().getId(), actionId));
        }
    }
}
