/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uilib.mxgallery.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.mikiller.mkglidelib.imageloader.GlideImageLoader;
import com.uilib.mxgallery.models.ItemModel;
import com.uilib.R;
import com.uilib.mxgallery.models.MimeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PreviewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<ItemModel> mItems = new ArrayList<>();

//    private int mimeType;
    private PageHolder lastHolder;
    private Timer timer;

    public PreviewPagerAdapter(Context context, List<ItemModel> items) {
        mContext = context;
        if (items != null)
            mItems.addAll(items);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View page = LayoutInflater.from(mContext).inflate(R.layout.layout_preview_page, null, false);
//        page.setTag(mItems.get(position).getContentUri());
        ItemModel item = mItems.get(position);
        PageHolder holder = new PageHolder(page);

        boolean isPic = MimeType.isPic(item.mimeType);
        holder.setMineType(isPic);
        if(!isPic) {
            holder.setVideoPreview(item);
        }else {
            holder.setImgPreview(item.getContentUri());
        }
        page.setTag(holder);
        container.addView(page);
        return page;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public void onPageSelected(int pos){
        if(lastHolder != null)
            lastHolder.stopVideo();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((PageHolder)((View)object).getTag()).stopVideo();
        container.removeView((View) object);
    }



    public void release(){
        mItems.clear();
        mItems = null;
        mContext = null;
        System.gc();
    }

//    public ItemModel getMediaItem(int position) {
//        return mItems.get(position);
//    }
//
//    public void addAll(List<ItemModel> items) {
//        mItems.addAll(items);
//    }

    private class PageHolder{
        private ImageView iv_preview;
        private RelativeLayout rl_vieoPreview;
        private VideoView viewVideo;
        private ImageView btn_start;
        private TextView tv_time;
        private ProgressBar pgs;
        private MediaPlayer mediaPlayer;
        private int state = -1;
//        private Timer timer;
        public PageHolder(View page) {
            iv_preview = (ImageView) page.findViewById(R.id.iv_preview);
            rl_vieoPreview = (RelativeLayout) page.findViewById(R.id.rl_vieoPreview);
            viewVideo = (VideoView) page.findViewById(R.id.viewVideo);
            btn_start = (ImageView) page.findViewById(R.id.btn_start);
            tv_time = (TextView) page.findViewById(R.id.tv_time);
            pgs = (ProgressBar) page.findViewById(R.id.pgs);
        }

        public void setMineType(boolean isPic){
            iv_preview.setVisibility(isPic ? View.VISIBLE : View.GONE);
            rl_vieoPreview.setVisibility(isPic ? View.GONE : View.VISIBLE);
        }

        public void setImgPreview(Uri uri){
            GlideImageLoader.getInstance().loadLocalImage(mContext, uri, R.mipmap.placeholder, iv_preview);
        }

        public void setVideoPreview(final ItemModel item){
            viewVideo.setVideoPath(item.getPath());
            viewVideo.seekTo(1);
            viewVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopVideo();
//                    btn_start.setVisibility(View.VISIBLE);
                }
            });

            viewVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer = mp;
                    pgs.setMax(mp.getDuration()/1000);
                    tv_time.setText(DateUtils.formatElapsedTime(mp.getDuration()/1000));
                }
            });

            btn_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(state == -1){
                        //停止状态
                        //重新播放
                        startVideo();
                    }else if(state == 1){
                        //播放状态
                        //暂停播放
                        viewVideo.pause();
                        state = 0;
                    }else if(state == 0){
                        //暂停状态
                        //继续播放
                        viewVideo.start();
                        state = 1;
                    }

                }
            });
        }

        private void updateTime(final long time){
            if(tv_time == null)
                return;
            tv_time.post(new Runnable() {
                @Override
                public void run() {
                    tv_time.setText(DateUtils.formatElapsedTime(time));
                    pgs.setProgress((int) time);
                }
            });
        }

        public void startVideo(){
            lastHolder = PageHolder.this;
            viewVideo.seekTo(0);
            viewVideo.resume();
            viewVideo.start();
            state = 1;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                long time = 0;
                @Override
                public void run() {
                    if(state == 1)
                        updateTime(time++);
                }
            }, 0, 1000);

        }

        public void stopVideo(){
            if(timer != null){
                viewVideo.stopPlayback();
                viewVideo.seekTo(1);
                if(timer != null) {
                    timer.cancel();
                    timer = null;
                }
                pgs.setProgress(0);
                state = -1;
                Log.e(this.getClass().getSimpleName(), "stop video");
            }
        }
    }

    interface OnPrimaryItemSetListener {

        void onPrimaryItemSet(int position);
    }

}
