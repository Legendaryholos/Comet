package com.cometproject.server.game.commands.vip;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Pathfinder;
import com.cometproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.cometproject.server.game.rooms.models.RoomModel;
import com.cometproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.cometproject.server.network.sessions.Session;

import java.util.List;

public class PushCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length == 0) {
            sendChat(Locale.get("command.push.invalidusername"), client);
            return;
        }

        String username = params[0];
        Session user = Comet.getServer().getNetwork().getSessions().getByPlayerUsername(username);

        if (user == null) {
            return;
        }

        if (user.getPlayer().getEntity() == null) {
            return;
        }

        if (user == client) {
            sendChat(Locale.get("command.push.playerhimself"), client);
            return;
        }

        int posX = user.getPlayer().getEntity().getPosition().getX();
        int posY = user.getPlayer().getEntity().getPosition().getY();
        int playerX = client.getPlayer().getEntity().getPosition().getX();
        int playerY = client.getPlayer().getEntity().getPosition().getY();
        int rot = client.getPlayer().getEntity().getBodyRotation();

        if (!((Math.abs((posX - playerX)) >= 2) || (Math.abs(posY - playerY) >= 2))) {
            switch (rot) {
                case 4:
                    posY += 1;
                    break;

                case 0:
                    posY -= 1;
                    break;

                case 6:
                    posX -= 1;
                    break;

                case 2:
                    posX += 1;
                    break;

                case 3:
                    posX += 1;
                    posY += 1;
                    break;

                case 1:
                    posX += 1;
                    posY -= 1;
                    break;

                case 7:
                    posX -= 1;
                    posY -= 1;
                    break;

                case 5:
                    posX -= 1;
                    posY += 1;
                    break;
            }

            RoomModel model = client.getPlayer().getEntity().getRoom().getModel();

            if (model.getDoorX() == posX && model.getDoorY() == posY) {
                sendChat(Locale.get(""), client);
                return;
            }

            user.getPlayer().getEntity().setWalkingGoal(posX, posY);

            List<Square> path = Pathfinder.getInstance().makePath(user.getPlayer().getEntity());
            user.getPlayer().getEntity().unIdle();

            if (user.getPlayer().getEntity().getWalkingPath() != null)
                user.getPlayer().getEntity().getWalkingPath().clear();

            user.getPlayer().getEntity().setWalkingPath(path);

            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(
                    TalkMessageComposer.compose(client.getPlayer().getEntity().getId(), Locale.get("command.push.message").replace("%playername%", user.getPlayer().getData().getUsername()), 0, 0)
            );
        }
    }

    @Override
    public String getPermission() {
        return "push_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.push.description");
    }
}
