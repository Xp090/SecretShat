package me.xp090.secretshat.DataModels;

import java.util.Date;

/**
 * Created by Xp090 on 02/12/2017.
 */

public class ChatMessage {

    // a class to represent a model of a chat message

    public static final String MESSEAGES_KEY = "messages";

    private String messageText;
    private String messageUser;
    private String messageUserID;
    private long messageTime;

    public ChatMessage() {}
    public ChatMessage(String messageText, String messageUser, String messageUserID) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageUserID = messageUserID;
        this.messageTime = new Date().getTime();
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

    public String getMessageUserID() {
        return messageUserID;
    }

    public void setMessageUserID(String messageUserID) {
        this.messageUserID = messageUserID;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }



}
