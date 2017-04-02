package com.encrpyt.whatsapp.whatsappencrypt;

public class Message {
    private String Name, Chat, Time, Direction, Number, Count;

    public Message(String time, String name, String chat, String direction, String number,String count) {
        this.Name = name;
        this.Chat = chat;
        this.Time = time;
        this.Direction = direction;
        this.Number = number;
        this.Count = count;
    }

    public String getName() {
        return Name;
    }

    public String getChat() {
        return Chat;
    }

    public String getTime() {
        return Time;
    }

    public String getDirection() {
        return Direction;
    }

    public String getNumber() {
        return Number;
    }

    public String getCount() {
        return Count;
    }
}
