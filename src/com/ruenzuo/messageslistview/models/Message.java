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
    @Column private String otherPerson;
    @Column private String thisPerson;
    @Column boolean isRead;
    private MessageType type;

    public Message() {
        super();
    }

    public Message(MessageBuilder messageBuilder) {
        this.date = messageBuilder.date;
        this.text = messageBuilder.text;
        this.type = messageBuilder.type;
        this.otherPerson = messageBuilder.otherPerson;
        this.thisPerson = messageBuilder.thisPerson;
        this.msgType = messageBuilder.msgType;
        this.isRead = messageBuilder.isRead;
    }

    public Date getDate() {
        return date;
    }

    public Message setRead(boolean read) {
        this.isRead = read;
        return this;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getText() {
        return text;
    }

    public Message setText(String text) {
        this.text = text;
        return this;
    }

    public String getOtherPerson() { return otherPerson; }

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
        private String otherPerson;
        private String thisPerson;
        private boolean isRead;

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

        public MessageBuilder otherPerson(String name) {
            this.otherPerson = name;
            return this;
        }

        public MessageBuilder thisPerson(String name) {
            this.thisPerson = name;
            return this;
        }

        public MessageBuilder isRead(boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public Message build() {
            return new Message(this);
        }

    }

}
