package com.krld.steamapi;

/**
 * Created by Andrey on 9/2/2014.
 */
public class Utils {
    public static int nvlInt(Object value, int altValue) {
        return (value == null ? -1 : anInt(value));
    }

    public static int anInt(Object value) {
        return ((Double) value).intValue();
    }
}
