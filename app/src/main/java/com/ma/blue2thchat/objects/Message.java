package com.ma.blue2thchat.objects;

public class Message {
    String message;
    boolean incoming;

    public Message(String message, boolean incoming) {
        this.message = message;
        this.incoming = incoming;
    }

    public String getMessage() {
        return message;
    }

    public boolean isIncoming() {
        return incoming;
    }
}
