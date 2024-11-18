package com.example.relayservice.entity;

public enum MethodType {
    GET, POST, PUT, PATCH, DELETE;

    public static boolean isMatch(String input) {
        for (MethodType value : MethodType.values()) {
            if (value.name().equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }

    public static MethodType getEnumFromString(String input) {
        for (MethodType value : MethodType.values()) {
            if (value.name().equalsIgnoreCase(input)) {
                return value;
            }
        }
        return null;
    }
    }
