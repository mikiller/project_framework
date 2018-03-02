package com.smg.mkframe.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mikiller.mkglidelib.imageloader.GlideImageLoader;
import com.smg.mkframe.R;
import com.smg.mkframe.base.BaseActivity;
import com.uilib.mxgallery.utils.GalleryMediaUtils;
import com.uilib.mxgallery.widgets.MXGallery;

import java.io.File;
import java.util.List;

public class WelcomeActivity extends BaseActivity {

    private ImageView iv_preview;
    private Button btn_gallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }

    @Override
    protected void initView() {
        iv_preview = (ImageView) findViewById(R.id.iv_preview);
        btn_gallery = (Button) findViewById(R.id.btn_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, GalleryActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;
        switch(requestCode){
            case 1:
                List<File> path = (List<File>) data.getSerializableExtra(GalleryMediaUtils.THUMB_FILE);
                GlideImageLoader.getInstance().loadLocalImage(this, Uri.fromFile(path.get(0)),R.mipmap.placeholder, iv_preview);
                break;
        }
    }
}
