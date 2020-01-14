package com.example.snowsoultrips;

public class Chat {
    String uid, messsage, imageurl, name, time, messageId, msgType;

    public Chat() {
    }

    @Override
    public String toString() {
        return "Chat{" +
                "uid='" + uid + '\'' +
                ", messsage='" + messsage + '\'' +
                ", imageurl='" + imageurl + '\'' +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", messageId='" + messageId + '\'' +
                ", msgType='" + msgType + '\'' +
                '}';
    }
}
