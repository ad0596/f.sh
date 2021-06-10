package com.client.app.FshClient.Util;

import org.springframework.context.ApplicationEvent;

public class UserConnectionEvent extends ApplicationEvent {

    private boolean isConnected;

    public UserConnectionEvent(Object source, boolean isConnected) {
        super(source);
        this.isConnected = isConnected;
    }

    public boolean getConnectionStatus() {
        return this.isConnected;
    }
}
