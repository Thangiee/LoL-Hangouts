package com.thangiee.LoLHangouts.data.entities;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

@Table(name = "MessageEntity")
public class MessageEntity extends Model {
    @Column public String username;
    @Column public String friendName;
    @Column public String text;
    @Column public boolean isSentByUser;
    @Column public boolean isRead;
    @Column public Date date;

    public MessageEntity() {
        super();
    }

    public MessageEntity(String username, String friendName, String text, boolean isSentByUser, boolean isRead, Date date) {
        this.username = username;
        this.friendName = friendName;
        this.text = text;
        this.isSentByUser = isSentByUser;
        this.isRead = isRead;
        this.date = date;
    }

    public MessageEntity setRead(boolean isRead) {
        this.isRead = isRead;
        return this;
    }
}
