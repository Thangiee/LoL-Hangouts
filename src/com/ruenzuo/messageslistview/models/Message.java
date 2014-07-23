package com.ruenzuo.messageslistview.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by ruenzuo on 27/05/14.
 */
@Table(name = "Message")
public class Message extends Model {

    @Column private Date date;
    @Column private String text;
    @Column private int msgType;
    @Column private String name;
    private MessageType type;

    public Message() {
        super();
    }

    public Message(MessageBuilder messageBuilder) {
        this.date = messageBuilder.date;
        this.text = messageBuilder.text;
        this.type = messageBuilder.type;
        this.name = messageBuilder.name;
        this.msgType = messageBuilder.msgType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessageType getType() {
        return msgType == 0 ? MessageType.MESSAGE_TYPE_SENT : MessageType.MESSAGE_TYPE_RECEIVED;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public static class MessageBuilder {

        private final MessageType type;
        private Date date;
        private String text;
        private int msgType;
        private String name;

        public MessageBuilder(MessageType type) {
            this.type = type;
            this.msgType = type.toInt();
        }

        public MessageBuilder date(Date date) {
            this.date = date;
            return this;
        }

        public MessageBuilder text(String text) {
            this.text = text;
            return this;
        }

        public MessageBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Message build() {
            return new Message(this);
        }

    }

}
