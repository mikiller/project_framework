package com.uilib.mxgallery.listeners;

import android.net.Uri;

import com.uilib.mxgallery.models.ItemModel;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Created by Mikiller on 2017/5/18.
 */

public interface OnBottomBarListener {
    void onPreView(boolean isPreview);
    void onConfirm(List<ItemModel> fileList);
}
