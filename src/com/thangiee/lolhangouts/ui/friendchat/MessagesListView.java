package com.thangiee.lolhangouts.ui.friendchat;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.thangiee.lolhangouts.R;

/**
 * Created by ruenzuo on 29/05/14.
 */
public class MessagesListView extends ListView {

    Drawable recipientDrawable;
    Drawable senderDrawable;
    int recipientColor;
    int senderColor;
    int messageTextColor;
    int dateTextColor;

    public MessagesListView(Context context) {
        this(context, null);
    }

    public MessagesListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessagesListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final Resources res = getResources();
        final Drawable defaultTransparent = res.getDrawable(R.drawable.default_messages_list_view_transparent);
        final boolean defaultSound = res.getBoolean(R.bool.default_messages_list_view_sound);
        final int defaultBackgroundColor = res.getColor(R.color.default_messages_list_view_background);
        final int defaultRecipientColor = res.getColor(R.color.md_grey_100);
        final int defaultSenderColor = res.getColor(R.color.md_teal_100);
        final int defaultMessageTextColor = res.getColor(R.color.primary_text);
        final int defaultDateTextColor = res.getColor(R.color.secondary_text);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MessagesListView, defStyle, 0);
        recipientDrawable = a.getDrawable(R.styleable.MessagesListView_recipientDrawable);
        senderDrawable = a.getDrawable(R.styleable.MessagesListView_senderDrawable);
        recipientColor = a.getColor(R.styleable.MessagesListView_recipientColor, defaultRecipientColor);
        senderColor = a.getColor(R.styleable.MessagesListView_senderColor, defaultSenderColor);
        messageTextColor = a.getColor(R.styleable.MessagesListView_messageTextColor, defaultMessageTextColor);
        dateTextColor = a.getColor(R.styleable.MessagesListView_dateTextColor, defaultDateTextColor);

        setBackgroundColor(defaultBackgroundColor);
        setDivider(defaultTransparent);
        setSelector(defaultTransparent);
        setSoundEffectsEnabled(defaultSound);
        setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        a.recycle();
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        MessageAdapter listAdapter = (MessageAdapter) adapter;
        listAdapter.setRecipientColor(recipientColor);
        listAdapter.setSenderColor(senderColor);
        listAdapter.setMessageTextColor(messageTextColor);
        listAdapter.setDateTextColor(dateTextColor);
    }

}
