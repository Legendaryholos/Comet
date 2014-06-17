package com.cometproject.server.game.rooms;

import com.cometproject.server.boot.Comet;
import com.cometproject.server.game.CometManager;
import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.RoomData;
import com.cometproject.server.tasks.CometTask;
import com.cometproject.server.tasks.CometThreadManagement;
import com.cometproject.server.utilities.TimeSpan;
import com.sun.deploy.cache.CacheEntry;
import org.apache.hadoop.ipc.RetryCache;
import org.apache.log4j.Logger;
import org.apache.solr.search.LRUCache;
import org.apache.solr.util.ConcurrentLRUCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RoomCycle implements CometTask {
    private ScheduledFuture myFuture;
    private boolean active;
    private Logger log = Logger.getLogger(RoomCycle.class.getName());

    public RoomCycle(CometThreadManagement mgr) {
        this.myFuture = mgr.executePeriodic(this, 500, 500, TimeUnit.MILLISECONDS);
        active = true;
    }

    @Override
    public void run() {
        int flag = 450;

        try {
            if (!this.isActive()) {
                return;
            }

            long start = System.currentTimeMillis();

            if (CometManager.getRooms() == null) {
                return;
            }

            List<Integer> unusedRoomData = new ArrayList<>();
            List<Integer> roomsToDispose = new ArrayList<>();

            for (Room room : CometManager.getRooms().getRoomInstances().values()) {
                if (room == null) continue;

                try {
                    if(room.isDisposed()) continue;

                    if(room.needsDispose()) {
                        roomsToDispose.add(room.getId());
                        continue;
                    }

                    room.tick();
                } catch (Exception e) {
                    log.error("Error while cycling room: " + room.getData().getId() + ", " + room.getData().getName(), e);
                }
            }

            for(int roomId : roomsToDispose) {
                CometManager.getRooms().removeInstance(roomId);
            }

            for(int roomDataId : unusedRoomData) {
                CometManager.getRooms().removeData(roomDataId);
            }

            roomsToDispose.clear();
            unusedRoomData.clear();

            TimeSpan span = new TimeSpan(start, System.currentTimeMillis());

            if (span.toMilliseconds() > flag) {
                log.warn("Global room processing took: " + span.toMilliseconds() + "ms to execute.");
            }
        } catch (Exception e) {
            log.error("Error while cycling rooms", e);
        }
    }

    public void stop() {
        this.setActive(false);
        this.myFuture.cancel(false);
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
