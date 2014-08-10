package com.cometproject.server.network.messages.incoming.group;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.CometManager;
import com.cometproject.server.game.groups.types.Group;
import com.cometproject.server.game.groups.types.GroupMember;
import com.cometproject.server.network.messages.incoming.IEvent;
import com.cometproject.server.network.messages.outgoing.group.GroupMembersMessageComposer;
import com.cometproject.server.network.messages.types.Event;
import com.cometproject.server.network.sessions.Session;

import java.util.ArrayList;

public class RevokeMembershipMessageEvent implements IEvent {
    @Override
    public void handle(Session client, Event msg) throws Exception {
        int groupId = msg.readInt();
        int playerId = msg.readInt();

        Group group = CometManager.getGroups().get(groupId);

        if (group == null)
            return;

        if (playerId == group.getData().getOwnerId())
            return;

        GroupMember groupMember = group.getMembershipComponent().getMembers().get(client.getPlayer().getId());

        if(!groupMember.getAccessLevel().isAdmin() && playerId != client.getPlayer().getId())
            return;

        group.getMembershipComponent().removeMembership(playerId);

        if (playerId == client.getPlayer().getId()) {
            if(client.getPlayer().getData().getFavouriteGroup() == groupId) {
                client.getPlayer().getData().setFavouriteGroup(0);
                client.getPlayer().getData().save();
            }

            if(client.getPlayer().getGroups().contains(groupId)) {
                client.getPlayer().getGroups().remove(client.getPlayer().getGroups().indexOf(groupId));
                client.send(group.composeInformation(true, client.getPlayer().getId()));
            }
        } else {
            if (CometManager.getPlayers().isOnline(playerId)) {
                Session session = Comet.getServer().getNetwork().getSessions().getByPlayerId(playerId);

                if (session != null) {
                    session.getPlayer().getGroups().remove(session.getPlayer().getGroups().indexOf(groupId));
                }
            }

            client.send(GroupMembersMessageComposer.compose(group.getData(), 0, new ArrayList<>(group.getMembershipComponent().getMembersAsList()), 0, "", client.getPlayer().getId() == group.getData().getOwnerId()));
        }
    }
}