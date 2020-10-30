package com.lakue.lakuevideotrim.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.lakue.lakuevideotrim.R;
import com.lakue.lakuevideotrim.viewholder.VideoGridViewHolder;

public class VideoSelectAdapter extends CursorAdapter {


    public VideoSelectAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.video_select_gridview_item, null);
        VideoGridViewHolder holder = new VideoGridViewHolder(itemView);
        itemView.setTag(holder);
        return itemView;
    }

    @Override public void bindView(View view, Context context, final Cursor cursor) {
        final VideoGridViewHolder holder = (VideoGridViewHolder) view.getTag();
        ((VideoGridViewHolder) holder).onBind(cursor, context);
    }
}
