package com.cometproject.server.network.messages.outgoing.user.username;

import com.cometproject.server.network.messages.headers.Composers;
import com.cometproject.server.network.messages.types.Composer;

public class ChangeUsernameCheckMessageComposer {
    public static Composer compose(boolean isAvailable, String username) {
        Composer msg = new Composer(Composers.ChangeUsernameCheckMessageComposer);

        msg.writeInt(isAvailable ? 0 : 5);
        msg.writeString(username);
        msg.writeInt(1);


        return msg;
    }
}