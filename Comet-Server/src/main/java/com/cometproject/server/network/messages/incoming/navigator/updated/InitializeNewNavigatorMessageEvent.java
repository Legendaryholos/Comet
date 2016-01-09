package com.cometproject.server.network.messages.incoming.navigator.updated;

import com.cometproject.server.network.messages.incoming.Event;
import com.cometproject.server.network.messages.outgoing.navigator.NavigatorMetaDataMessageComposer;
import com.cometproject.server.network.messages.outgoing.navigator.updated.NavigatorPreferencesMessageComposer;
import com.cometproject.server.network.sessions.Session;
import com.cometproject.server.protocol.messages.MessageEvent;

public class InitializeNewNavigatorMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.sendQueue(new NavigatorPreferencesMessageComposer())
                .sendQueue(new NavigatorMetaDataMessageComposer());

        client.flush();
    }
}
