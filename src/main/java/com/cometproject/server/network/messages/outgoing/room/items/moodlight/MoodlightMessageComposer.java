package com.cometproject.server.network.messages.outgoing.room.items.moodlight;

import com.cometproject.server.game.rooms.items.data.MoodlightPresetData;
import com.cometproject.server.game.rooms.items.types.wall.MoodlightWallItem;
import com.cometproject.server.network.messages.headers.Composers;
import com.cometproject.server.network.messages.types.Composer;

public class MoodlightMessageComposer {
    public static Composer compose(MoodlightWallItem moodlight) {
        Composer msg = new Composer(Composers.MoodlightMessageComposer);

        msg.writeInt(moodlight.getMoodlightData().getPresets().size());
        msg.writeInt(moodlight.getMoodlightData().getActivePreset());

        int id = 0;

        for (MoodlightPresetData data : moodlight.getMoodlightData().getPresets()) {
            msg.writeInt(id++);
            msg.writeInt(data.backgroundOnly ? 1 : 2);
            msg.writeString(data.colour);
            msg.writeInt(data.intensity);
        }

        return msg;
    }
}
