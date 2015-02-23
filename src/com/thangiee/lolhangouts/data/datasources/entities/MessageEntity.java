package com.thangiee.lolhangouts.data.datasources.entities;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

@Table(name = "MessageEntity")
public class MessageEntity extends Model {
    @Column public String username;
    @Column public String friendName;
    @Column public String text;
    @Column private int isSentByUser;
    @Column private int isRead;
    @Column public Date date;

    public MessageEntity() {
        super();
    }

    public MessageEntity(String username, String friendName, String text, boolean isSentByUser, boolean isRead, Date date) {
        this.username = username;
        this.friendName = friendName;
        this.text = text;
        this.isSentByUser = isSentByUser ? 1 : 0;
        this.isRead = isRead ? 1 : 0;
        this.date = date;
    }

    public MessageEntity setRead(boolean isRead) {
        this.isRead = isRead ? 1 : 0;
        return this;
    }

    public boolean isRead() {
        return isRead == 1;
    }

    public boolean isSentByUser() {
        return isSentByUser == 1;
    }
}
