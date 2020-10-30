package com.lakue.lakuevideotrim.viewholder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lakue.lakuevideotrim.R;
import com.lakue.lakuevideotrim.activity.VideoTrimActivity;
import com.lakue.lakuevideotrim.util.DateUtil;
import com.lakue.lakuevideotrim.util.DeviceUtil;

import java.io.File;

public class VideoGridViewHolder {

    private MediaMetadataRetriever mMetadataRetriever;
    private int videoCoverSize = DeviceUtil.getDeviceWidth() / 3;
    ImageView videoCover;
    View videoItemView;
    TextView durationTv;

    public VideoGridViewHolder(View itemView){
        videoCover = itemView.findViewById(R.id.cover_image);
        videoItemView = itemView.findViewById(R.id.video_view);
        durationTv = itemView.findViewById(R.id.video_duration);

        mMetadataRetriever = new MediaMetadataRetriever();

    }

    public void onBind(Cursor cursor, Context context){
        final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        if (!checkDataValid(cursor)) {
            return;
        }
        final String duration = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        durationTv.setText(DateUtil.convertSecondsToTime(Integer.parseInt(duration) / 1000));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoCover.getLayoutParams();
        params.width = videoCoverSize;
        params.height = videoCoverSize;
        videoCover.setLayoutParams(params);
        Glide.with(context)
                .load(getVideoUri(cursor))
                .centerCrop()
                .override(videoCoverSize, videoCoverSize)
                .into(videoCover);
        videoItemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(context, VideoTrimActivity.class);
                intent.putExtra("EXTRA_PATH",path);
                context.startActivity(intent);

//                VideoTrimmerActivity.call((FragmentActivity) mContext, path);
            }
        });
    }

    private boolean checkDataValid(final Cursor cursor) {
        final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
            return false;
        }
        try {
            mMetadataRetriever.setDataSource(path);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        final String duration = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return !TextUtils.isEmpty(duration);
    }

    private Uri getVideoUri(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
        return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
    }



}
