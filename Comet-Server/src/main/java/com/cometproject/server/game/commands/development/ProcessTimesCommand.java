package com.cometproject.server.game.commands.development;

import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.types.RoomInstance;
import com.cometproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.cometproject.server.network.sessions.Session;

import java.util.ArrayList;

public class ProcessTimesCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final StringBuilder processTimesBuilder = new StringBuilder();

        RoomInstance room = client.getPlayer().getEntity().getRoom();

        if (room.getProcess().getProcessTimes() == null) {
            room.getProcess().setProcessTimes(new ArrayList<>());

            client.send(new AlertMessageComposer("Process times for this room are now being recorded. (Max: 30)"));
            return;
        }

        for(Long processTime : room.getProcess().getProcessTimes()) {
            processTimesBuilder.append(processTime + "\n");
        }

        client.send(new AlertMessageComposer("<b>Process Times</b><br><br>" + processTimesBuilder.toString()));

        room.getProcess().getProcessTimes().clear();
        room.getProcess().setProcessTimes(null);
    }

    @Override
    public String getPermission() {
        return "dev";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}