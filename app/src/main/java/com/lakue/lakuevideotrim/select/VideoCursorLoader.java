package com.lakue.lakuevideotrim.select;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.lakue.lakuevideotrim.callback.SimpleCallback;

public class VideoCursorLoader implements LoaderManager.LoaderCallbacks<Cursor>, ILoader {

    private Context mContext;
    private SimpleCallback mSimpleCallback;

    @Override public void load(final Context context, final SimpleCallback listener) {
        mContext = context;
        mSimpleCallback = listener;
        ((FragmentActivity)context).getSupportLoaderManager().initLoader(1, null, this);
    }

    @NonNull
    @Override public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(
                mContext,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                PROJECTION,
                SELECTION,
                SELECTION_ARGS,
                ORDER_BY
        );
    }

    @Override public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (mSimpleCallback != null && cursor != null) {
            mSimpleCallback.success(cursor);
        }
    }

    @Override public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
