package com.mikiller.mkglidelib.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

//import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
//import com.bumptech.glide.load.resource.drawable.GlideDrawable;

/**
 * Created by KasoGG on 2016/6/30.
 */
interface ImageLoadListener<T> {
        void onLoadSuccess(T drawable, View imageView);
        void onLoadFailed(View imageView);
}
