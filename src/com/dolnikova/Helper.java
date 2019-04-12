package com.dolnikova;

import java.util.UUID;

public class Helper {

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

}
