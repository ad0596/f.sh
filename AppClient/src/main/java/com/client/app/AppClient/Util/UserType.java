package com.client.app.AppClient.Util;

import java.util.HashMap;
import java.util.Map;

public enum UserType {
    SENDER(1), RECEIVER(0);

    private int value;
    private static Map map = new HashMap<>();

    private UserType(int value) {
        this.value = value;
    }

    static {
        for (UserType userType : UserType.values()) {
            map.put(userType.value, userType);
        }
    }

    public static UserType valueOf(int userType) {
        return (UserType) map.get(userType);
    }

    public int getValue() {
        return value;
    }
}
