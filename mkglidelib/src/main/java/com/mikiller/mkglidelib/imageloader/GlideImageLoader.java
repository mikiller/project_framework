package com.mikiller.mkglidelib.imageloader;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ObbInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.mikiller.mkglidelib.R;

/**
 * Created by KasoGG on 2016/6/30.
 */
public class GlideImageLoader implements ImageLoader {

    private GlideImageLoader() {
    }

    public void loadImage(final Context context, final String path, final int defaultImg, final ImageView imageView, final int key, final long delay, final ImageLoadListener listener) {
        final SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                Log.e("image loader", "path: " + path + ", key: " + key);
                if (!imageView.getTag(key).equals(path))
                    return;
                if (listener == null) {
                    imageView.setImageBitmap(resource);
                    imageView.setTag(this);
                    imageView.setBackground(null);
                    if (delay > 0) {
                        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
                        animator.setDuration(delay);
                        animator.start();
                    }
                } else
                    listener.onLoadSuccess(resource, imageView);
//                    }
//                },delay);
            }
        };
        final BitmapRequestBuilder request = Glide.with(context).load(path).asBitmap().centerCrop().signature(new StringSignature(String.valueOf(key)));
        imageView.setTag(key, path);
        final LayoutChangeListener layoutChangeListener = new LayoutChangeListener(imageView, target, request, defaultImg, listener);
        imageView.addOnLayoutChangeListener(layoutChangeListener);

    }

    public void loadImage(final Context context, String path, final ImageView imageView, final long delay, final ImageLoadListener listener) {
        loadImage(context, path, View.NO_ID, imageView, 0x7f000000-1, delay, listener);
    }

    public void loadImage(final Context context, String path, int defaultImg, final ImageView imageView, final long delay) {
        loadImage(context, path, defaultImg, imageView, 0x7f000000-1, delay, null);
    }

    public void loadImage(final Context context, String path, int defaultImg, final ImageView imageView, int key, final long delay) {
        loadImage(context, path, defaultImg, imageView, key, delay, null);
    }

    public void loadImageWithBitmap(Context context, String path, ImageView imageView) {
        Glide.with(context).load(path).asBitmap().skipMemoryCache(true).into(imageView);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void loadLocalImage(Context context, Uri uri, int placeholder, Size reSize, ImageView target) {
        if (reSize != null && (reSize.getHeight() > 0 && reSize.getWidth() > 0))
            Glide.with(context).load(uri).asBitmap().placeholder(placeholder).override(reSize.getWidth(), reSize.getHeight()).centerCrop().into(target);
        else
            Glide.with(context).load(uri).placeholder(placeholder).centerCrop().into(target);
    }

    public void loadLocalImage(Context context, Uri uri, int[] reSize, int placeholder, ImageView target) {
        if (reSize != null && (reSize.length == 2) && (reSize[0] > 0) && (reSize[1] > 0))
            Glide.with(context).load(uri).asBitmap().placeholder(placeholder).override(reSize[0], reSize[1]).centerCrop().into(target);
        else
            Glide.with(context).load(uri).fitCenter().into(target);
    }

    public void loadLocalImage(Context context, Uri uri, int placeholder, ImageView target) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            loadLocalImage(context, uri, placeholder, null, target);
        else
            loadLocalImage(context, uri, null, placeholder, target);
    }

    public void clearImage(ImageView target, int placeholder) {
        target.setImageBitmap(null);
        if (target.getTag() != null && target.getTag() instanceof SimpleTarget) {
            Glide.clear((SimpleTarget<?>) target.getTag());
        }

        target.setImageBitmap(BitmapFactory.decodeResource(target.getResources(), placeholder ));
    }

    public static GlideImageLoader getInstance() {
        return Nested.instance;
    }

    private static class Nested {
        private static final GlideImageLoader instance = new GlideImageLoader();
    }

    private static class LayoutChangeListener implements View.OnLayoutChangeListener {
        private ImageView imageView;
        private SimpleTarget target;
        private BitmapRequestBuilder request;
        private int defaultImg;
        private ImageLoadListener listener;
        private RequestListener reqListener = new RequestListener() {

            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                if (e != null) {
                    imageView.removeOnLayoutChangeListener(LayoutChangeListener.this);
                }
                if (defaultImg != View.NO_ID) {
                    imageView.setImageResource(defaultImg);
                    imageView.setBackground(null);
                }
                if (listener != null)
                    listener.onLoadFailed(imageView);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }

        };

        public LayoutChangeListener(ImageView imageView, SimpleTarget target, BitmapRequestBuilder request, int defautImg, ImageLoadListener listener) {
            this.imageView = imageView;
            this.target = target;
            this.request = request;
            this.defaultImg = defautImg;
            this.listener = listener;
        }

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (defaultImg != View.NO_ID) {
                request = request.error(defaultImg).placeholder(defaultImg);
            }
//            if(imageView.getMeasuredWidth() > 0 && imageView.getMeasuredHeight() > 0){
//                request.override(imageView.getMeasuredWidth(), imageView.getMeasuredHeight()).listener(reqListener).into(target);
//            }else{
                request.listener(reqListener).into(target);
//            }
            imageView.removeOnLayoutChangeListener(this);
        }
    }
}



//package com.mikiller.mkglidelib.imageloader;
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.content.Context;
//import android.content.res.ObbInfo;

//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.TransitionDrawable;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Handler;
//import android.util.Log;
//import android.util.Size;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.bumptech.glide.BitmapRequestBuilder;
//import com.bumptech.glide.DrawableRequestBuilder;
//import com.bumptech.glide.DrawableTypeRequest;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
//import com.bumptech.glide.load.resource.drawable.GlideDrawable;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.animation.GlideAnimation;
//import com.bumptech.glide.request.target.BitmapImageViewTarget;
//import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
//import com.bumptech.glide.request.target.SimpleTarget;
//import com.bumptech.glide.request.target.Target;
//import com.mikiller.mkglidelib.R;
//
///**
// * Created by KasoGG on 2016/6/30.
// */
//public class GlideImageLoader implements ImageLoader {
//    private GlideImageLoader() {
//    }
//
////    @Override
////    public void loadImage(Context context, String path, final ImageView imageView, final ImageLoadListener listener) {
////        Glide.with(context).load(path).skipMemoryCache(true).into(new GlideDrawableImageViewTarget(imageView) {
////            @Override
////            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
////                super.onResourceReady(resource, animation);
////                listener.onLoadSuccess(resource, imageView);
////            }
////        });
////    }
//
//    public void loadImage(final Context context, String path, final int defaultImg, final ImageView imageView, final long delay, final ImageLoadListener listener){
//        RequestListener reqListener = new RequestListener() {
//
//            @Override
//            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
//                if (e != null)
//                    Log.e(context.getClass().getSimpleName() + " in imageload", e.toString());
//                if(defaultImg == View.NO_ID)
//                    imageView.setBackgroundColor(context.getResources().getColor(R.color.gray_bg));
//                else
//                    imageView.setBackgroundResource(defaultImg);
//                if (listener != null)
//                    listener.onLoadFailed(imageView);
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
//                return false;
//            }
//
//        };
//
//        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(listener == null) {
//                            imageView.setImageBitmap(resource);
//                            imageView.setBackground(null);
//                        }
//                        else
//                            listener.onLoadSuccess(resource, imageView);
//                    }
//                }, delay);
//            }
//        };
//
//        BitmapRequestBuilder request = Glide.with(context).load(path).asBitmap().skipMemoryCache(true).listener(reqListener);
//        if(defaultImg != View.NO_ID){
//            request.error(defaultImg).placeholder(defaultImg).into(target);
//        }else{
//            request.into(target);
//        }
//    }
//
//    public void loadImage(final Context context, String path, final ImageView imageView, final long delay, final ImageLoadListener listener) {
//        loadImage(context, path, View.NO_ID, imageView, delay, listener);
//    }
//
//    public void loadImage(final Context context, String path, int defaultImg, final ImageView imageView, final long delay) {
//        loadImage(context, path, defaultImg, imageView, delay, null);
//    }
//
//    public void loadImageWithBitmap(Context context, String path, int defaultRes, ImageView imageView) {
//        Glide.with(context).load(path).asBitmap().listener(new RequestListener<String, Bitmap>() {
//            @Override
//            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
//
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                return false;
//            }
//        }).into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//
//            }
//        });
//
////        Glide.with(context).load(path).skipMemoryCache(true).listener(listener).into(target);
//    }
//
//    public void loadImageWithBitmap(Context context, String path, ImageView imageView) {
//        Glide.with(context).load(path).asBitmap().skipMemoryCache(true).into(imageView);
//    }
//
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public void loadLocalImage(Context context, Uri uri, int placeholder, Size reSize, ImageView target){
//        if(reSize != null && (reSize.getHeight() >0 && reSize.getWidth() > 0))
//            Glide.with(context).load(uri).asBitmap().placeholder(placeholder).override(reSize.getWidth(), reSize.getHeight()).centerCrop().into(target);
//        else
//            Glide.with(context).load(uri).fitCenter().into(target);
//    }
//
//    public void loadLocalImage(Context context, Uri uri, int[] reSize, int placeholder, ImageView target){
//        if(reSize != null && (reSize.length == 2) && (reSize[0] > 0) && (reSize[1] > 0))
//            Glide.with(context).load(uri).asBitmap().placeholder(placeholder).override(reSize[0], reSize[1]).centerCrop().into(target);
//        else
//            Glide.with(context).load(uri).fitCenter().into(target);
//    }
//
//    public void loadLocalImage(Context context, Uri uri, int placeholder, ImageView target){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            loadLocalImage(context, uri, placeholder, null, target);
//        else
//            loadLocalImage(context, uri, null, placeholder, target);
//    }
//
//    public static GlideImageLoader getInstance() {
//        return Nested.instance;
//    }
//
//    private static class Nested {
//        private static final GlideImageLoader instance = new GlideImageLoader();
//    }
//}
