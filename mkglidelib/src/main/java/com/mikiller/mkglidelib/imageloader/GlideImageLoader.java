package com.mikiller.mkglidelib.imageloader;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.bumptech.glide.BitmapRequestBuilder;
//import com.bumptech.glide.DrawableRequestBuilder;
//import com.bumptech.glide.DrawableTypeRequest;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
//import com.bumptech.glide.load.resource.drawable.GlideDrawable;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.animation.GlideAnimation;
//import com.bumptech.glide.request.animation.ViewPropertyAnimation;
//import com.bumptech.glide.request.target.BitmapImageViewTarget;
//import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
//import com.bumptech.glide.request.target.SimpleTarget;
//import com.bumptech.glide.request.target.Target;
//import com.bumptech.glide.signature.StringSignature;


/**
 * Created by KasoGG on 2016/6/30.
 */
public class GlideImageLoader {

    private GlideImageLoader() {
    }

    public static GlideImageLoader getInstance() {
        return Nested.instance;
    }

    private static class Nested {
        private static final GlideImageLoader instance = new GlideImageLoader();
    }

    public void loadImage(final Context context, String path, int placeholder, final ImageView target){
        loadImage(context, path, placeholder, target, null);
    }

    public void loadImage(final Context context, String path, int placeholder, final ImageView target, final ImageLoadListener listener){
        loadImage(context, path, placeholder, null, null, target, listener);
    }

    public void loadImage(final Context context, String path, int placeholder, RequestOptions requestOpts, final ImageView target, final ImageLoadListener listener){
        loadImage(context, path, placeholder, requestOpts, null, target, listener);
    }

    public void loadImage(final Context context, String path, int placeholder, TransitionOptions transOpts, final ImageView target, final ImageLoadListener listener){
        loadImage(context, path, placeholder, null, transOpts, target, listener);
    }

    public void loadImage(final Context context, String path, int placeholder, RequestOptions requestOpts, TransitionOptions transOpts,final ImageView target, final ImageLoadListener listener) {
        RequestBuilder request = Glide.with(context).load(path);
        if(requestOpts == null)
            requestOpts = new RequestOptions();
        requestOpts.placeholder(placeholder).error(placeholder);
        if (transOpts != null)
            request.transition(transOpts);
        loadImage(request.apply(requestOpts), target, listener);
    }

    public void loadImage(final Context context, String path, int placeholder, int[] resize, final ImageView target){
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(placeholder).error(placeholder).override(resize[0], resize[1]);
        RequestBuilder request = Glide.with(context).load(path).apply(requestOptions);
        loadImage(request, target, null);
//        loadImage(createGlideRequest(context, path, placeholder).override(resize[0], resize[1]), target, null);
    }

    public void loadImage(final Context context, String path, int placeholder, Size resize, final ImageView target){
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(placeholder).error(placeholder).override(resize.getWidth(), resize.getHeight());
        RequestBuilder request = Glide.with(context).load(path).apply(requestOptions);
        loadImage(request, target, null);
//        loadImage(createGlideRequest(context, path, placeholder).override(resize.getWidth(), resize.getHeight()), target, null);
    }
//    private GlideRequest createGlideRequest(final Context context, String path, int placeholder){
//        RequestOptions requestOptions = new RequestOptions().placeholder(placeholder);
//        RequestBuilder requestBuilder = Glide.with(context).load(path).apply(requestOptions);
//        return GlideApp.with(context).load(path).placeholder(placeholder);
//    }

    public <Z> void loadImage(RequestBuilder request, final ImageView target, final ImageLoadListener<Z> listener){
        request.into(new CustomViewTarget<ImageView, Z>(target) {
            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                target.setImageDrawable(errorDrawable);
                if (listener != null) {
                    listener.onLoadFailed(target);
                }
            }

            @Override
            public void onResourceReady(@NonNull Z resource, @Nullable Transition<? super Z> transition) {
                if (resource instanceof Drawable) {
                    target.setImageDrawable((Drawable) resource);
                } else if (resource instanceof Bitmap) {
                    target.setImageBitmap((Bitmap) resource);
                }
                if (listener != null) {
                    listener.onLoadSuccess(resource, target);
                }
            }
        });
    }
}
