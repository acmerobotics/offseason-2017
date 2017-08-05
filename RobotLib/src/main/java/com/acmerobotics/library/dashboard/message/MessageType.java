package com.acmerobotics.library.dashboard.message;

import com.google.gson.annotations.SerializedName;

/**
 * @author Ryan
 */

public enum MessageType {
    @SerializedName("ping")
    PING,

    @SerializedName("pong")
    PONG,

    @SerializedName("get")
    GET,

    @SerializedName("update")
    UPDATE;
}
