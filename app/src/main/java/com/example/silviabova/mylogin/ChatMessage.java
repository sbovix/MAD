package com.example.silviabova.mylogin;

import java.util.Date;

/**
 * Created by silvia bova on 08/05/2018.
 */

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;
    private int type;

    public ChatMessage(String messageText, String messageUser, int type){
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.type = type;

        messageTime = new Date().getTime();
    }

    public ChatMessage(){
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
