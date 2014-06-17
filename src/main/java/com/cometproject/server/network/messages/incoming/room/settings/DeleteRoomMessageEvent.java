package com.cometproject.server.network.messages.incoming.room.settings;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.CometManager;
import com.cometproject.server.game.players.components.types.InventoryBot;
import com.cometproject.server.game.rooms.entities.types.BotEntity;
import com.cometproject.server.game.rooms.entities.types.PetEntity;
import com.cometproject.server.game.rooms.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.items.RoomItem;
import com.cometproject.server.game.rooms.items.RoomItemFloor;
import com.cometproject.server.game.rooms.items.RoomItemWall;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.network.messages.incoming.IEvent;
import com.cometproject.server.network.messages.outgoing.user.inventory.BotInventoryMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.PetInventoryMessageComposer;
import com.cometproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.cometproject.server.network.messages.types.Event;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.storage.queries.bots.RoomBotDao;
import com.cometproject.server.storage.queries.pets.RoomPetDao;
import com.cometproject.server.storage.queries.rooms.RoomDao;

import java.util.ArrayList;
import java.util.List;

public class DeleteRoomMessageEvent implements IEvent {

    @Override
    public void handle(Session client, Event msg) throws Exception {
        PlayerEntity entity = client.getPlayer().getEntity();

        if (entity == null)
            return;

        Room room = entity.getRoom();

        if (room == null || (room.getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().hasPermission("room_full_control"))) {
            return;
        }

        List<RoomItem> itemsToRemove = new ArrayList<>();
        itemsToRemove.addAll(room.getItems().getFloorItems());
        itemsToRemove.addAll(room.getItems().getWallItems());

        for (RoomItem item : itemsToRemove) {
            if (item instanceof RoomItemFloor) {
                room.getItems().removeItem((RoomItemFloor) item, client);
            } else if (item instanceof RoomItemWall) {
                room.getItems().removeItem((RoomItemWall) item, client);
            }
        }

        for (BotEntity bot : room.getEntities().getBotEntities()) {
            InventoryBot inventoryBot = new InventoryBot(bot.getBotId(), bot.getData().getOwnerId(), bot.getData().getOwnerName(), bot.getUsername(), bot.getFigure(), bot.getGender(), bot.getMotto());
            client.getPlayer().getBots().addBot(inventoryBot);

            RoomBotDao.setRoomId(0, inventoryBot.getId());
        }

        for (PetEntity pet : room.getEntities().getPetEntities()) {
            client.getPlayer().getPets().addPet(pet.getData());

            RoomPetDao.updatePet(0, 0, 0, pet.getData().getId());
        }

        // Manually unload room!
        CometManager.getRooms().removeInstance(room.getId());

        if (CometManager.getPlayers().isOnline(room.getData().getOwnerId())) {
            Session owner = Comet.getServer().getNetwork().getSessions().getByPlayerId(room.getData().getOwnerId());

            if (owner.getPlayer() != null && owner.getPlayer().getRooms() != null) {
                if (owner.getPlayer().getRooms().contains(room.getId())) {
                    owner.getPlayer().getRooms().remove(owner.getPlayer().getRooms().indexOf(room.getId()));
                }
            }
        }

        CometManager.getLogger().info("Room deleted: " + room.getId() + " by " + client.getPlayer().getId() + " / " + client.getPlayer().getData().getUsername());
        RoomDao.deleteRoom(room.getId());

        client.send(UpdateInventoryMessageComposer.compose());
        client.send(PetInventoryMessageComposer.compose(client.getPlayer().getPets().getPets()));
        client.send(BotInventoryMessageComposer.compose(client.getPlayer().getBots().getBots()));
    }
}
