package com.lakue.lakuevideotrim.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.lakue.lakuevideotrim.R;
import com.lakue.lakuevideotrim.common.BaseActivity;
import com.lakue.lakuevideotrim.databinding.ActivityVideoTrimBinding;
import com.lakue.lakuevideotrim.trim.VideoTrimListener;

public class VideoTrimActivity extends BaseActivity implements VideoTrimListener {


    private ActivityVideoTrimBinding mBinding;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_trim);
        Bundle bd = getIntent().getExtras();
        String path = "";

        if (bd != null) path = bd.getString("EXTRA_PATH");
        if (mBinding.trimmerView != null) {
            mBinding.trimmerView.setOnTrimVideoListener(this);
            mBinding.trimmerView.initVideoByURI(Uri.parse(path));
        }
    }

    @Override public void onResume() {
        super.onResume();
    }

    @Override public void onPause() {
        super.onPause();
        mBinding.trimmerView.onVideoPause();
        mBinding.trimmerView.setRestoreState(true);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mBinding.trimmerView.onDestroy();
    }

    @Override public void onStartTrim() {
        buildDialog("자르는 중.").show();
    }

    @Override public void onFinishTrim(String in) {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        Toast.makeText(this, "완료되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override public void onCancel() {
        mBinding.trimmerView.onDestroy();
        finish();
    }

    private ProgressDialog buildDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "", msg);
        }
        mProgressDialog.setMessage(msg);
        return mProgressDialog;
    }
}