package com.lakue.lakuevideotrim.activity;

import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;

import com.lakue.lakuevideotrim.common.BaseActivity;
import com.lakue.lakuevideotrim.R;
import com.lakue.lakuevideotrim.callback.SimpleCallback;
import com.lakue.lakuevideotrim.adapter.VideoSelectAdapter;
import com.lakue.lakuevideotrim.databinding.ActivityVideoSelectBinding;
import com.lakue.lakuevideotrim.select.VideoCursorLoader;
import com.lakue.lakuevideotrim.select.VideoLoadManager;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class VideoSelectActivity extends BaseActivity {

    private ActivityVideoSelectBinding mBinding;
    private VideoSelectAdapter mVideoSelectAdapter;
    private VideoLoadManager mVideoLoadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_select);

        mVideoLoadManager = new VideoLoadManager();
        mVideoLoadManager.setLoader(new VideoCursorLoader());


        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(granted -> {
            if (granted) { // Always true pre-M
                mVideoLoadManager.load(this, new SimpleCallback() {
                    @Override public void success(Object obj) {
                        if (mVideoSelectAdapter == null) {
                            mVideoSelectAdapter = new VideoSelectAdapter(VideoSelectActivity.this, (Cursor) obj);
                        } else {
                            mVideoSelectAdapter.swapCursor((Cursor) obj);
                        }
                        if (mBinding.videoGridview.getAdapter() == null) {
                            mBinding.videoGridview.setAdapter(mVideoSelectAdapter);
                        }
                        mVideoSelectAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                finish();
            }
        });
    }
}