package com.smg.mkframe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.smg.mkframe.R;
import com.smg.mkframe.base.BaseActivity;
import com.smg.mkframe.base.Constants;
import com.uilib.mxgallery.listeners.GalleryTabListener;
import com.uilib.mxgallery.listeners.OnBottomBarListener;
import com.uilib.mxgallery.models.ItemModel;
import com.uilib.mxgallery.models.MimeType;
import com.uilib.mxgallery.models.ReportResModel;
import com.uilib.mxgallery.utils.CameraGalleryUtils;
import com.uilib.mxgallery.widgets.GalleryTabGroup;
import com.uilib.mxgallery.widgets.MXGallery;
import com.uilib.titlebar.TitleBar;
import com.uilib.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;

/**
 * Created by Mikiller on 2017/5/18.
 */

public class GalleryActivity extends BaseActivity {
    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.gallery)
    MXGallery gallery;
    private CameraGalleryUtils cgUtils;

    private String title;
    private boolean isMultiple = false, isPreview = false;
    private Bundle savedBundle;
    private List<String> selectedPath = new ArrayList<>();
    private float cropX = 1, cropY = 1;
    private String fileName;
    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        title = getIntent().getStringExtra("listType");
        isMultiple = getIntent().getBooleanExtra("isMultiple", false);
        cropX = getIntent().getFloatExtra("cropX", 1f);
        cropY = getIntent().getFloatExtra("cropY", 1f);
        savedBundle = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
    }

    @Override
    protected void initView() {
        cgUtils = CameraGalleryUtils.getInstance(this);
        titleBar.setTitle(title);
        titleBar.setTitleListener(new TitleBar.TitleListener() {
            @Override
            protected void onBackClicked() {
                setResult(RESULT_CANCELED);
                back();
            }
        });
        gallery.setIsMultiple(isMultiple);
        if(getString(R.string.gallery_pic).equals(title)){
            gallery.setMimeType(MimeType.ofImage());
        }else if(getString(R.string.gallery_video).equals(title)){
            gallery.setMimeType(MimeType.ofVideo());
        }else{
            gallery.setMimeType(MimeType.ofAudio());
            gallery.setColumnNum(3);
        }

        gallery.setSelectListener(new OnBottomBarListener() {
            @Override
            public void onPreView(boolean isPreview) {
                titleBar.setVisibility(isPreview ? View.GONE : View.VISIBLE);
                GalleryActivity.this.isPreview = isPreview;
            }

            @Override
            public void onConfirm(List<ItemModel> modelList) {

                if (title.equals(getString(R.string.gallery_pic)) && !isMultiple) {
                    fileName = StringUtils.getDateStr("yyyy-MM-dd_hh:mm:ss", System.currentTimeMillis());
                    path = modelList.get(0).getPath();
                    cgUtils.cropPhoto(GalleryActivity.this,
                            modelList.get(0).uri, fileName, cropX, cropY);
                } else if(title.equals(getString(R.string.gallery_video))){
                    Map<String,Object> args = new HashMap<>();
                    args.put(Constants.VIDEO_MODEL, modelList.get(0));
                   // ActivityManager.startActivityforResult(GalleryActivity.this, VideoEditorActivity.class, Constants.VIDEO_EDITOR, args);
                }else if(title.equals("音频")){
                    Intent intent = new Intent();
                    intent.putExtra(CameraGalleryUtils.THUMB_FILE, modelList.get(0));
                    intent.putExtra("listType", title);
                    setResult(RESULT_OK, intent);
                    back();
                }
            }
        });
        gallery.onCreate(savedBundle);
        gallery.setSelectedPaths(getSelectPath());
        String[] tabNames;
        if(isMultiple){
            tabNames = new String[]{String.format("未选择(%1$d)", 0), String.format("全部(%1$d)", 0)};
        }else{
            tabNames = new String[]{String.format("全部(%1$d)", 0)};
        }
        gallery.setTabNames(new GalleryTabListener() {
            @Override
            public void onTabChecked(RadioButton tab, int id) {
                gallery.updateItems(tab.getText().toString().contains("全部"));
            }

            @Override
            public void onTabUpdated(GalleryTabGroup galleryTab, int tabId, int itemCount) {
                if(isMultiple) {
                    galleryTab.setTabName(tabId == 0 ? tabId : 0, String.format("未选择(%1$d)", itemCount - gallery.getSelectedItemCount() - selectedPath.size()));
                    galleryTab.setTabName(tabId == 0 ? tabId + 1 : tabId, String.format("全部(%1$d)", itemCount));
                }else{
                    galleryTab.setTabName(tabId, String.format("全部(%1$d)", itemCount));
                }
            }
        }, tabNames);
    }

    private List<String> getSelectPath() {
        CameraGalleryUtils cgUtils = CameraGalleryUtils.getInstance();
        for (ReportResModel file : cgUtils.getThumbList()) {
            if (selectedPath.contains(file.getResFile().getAbsolutePath()))
                continue;
            selectedPath.add(file.getResFile().getAbsolutePath());
//            if (isPic) {
//                if (MimeType.isPic(cgUtils.getFileMimeType(file.getResFile().getPath())))
//                    selectedPath.add(file.getResFile().getAbsolutePath());
//            } else {
//                if (MimeType.isVideo(cgUtils.getFileMimeType(file.getResFile().getPath())))
//                    selectedPath.add(file.getResFile().getAbsolutePath());
//            }
        }
        return selectedPath;
    }

    @Override
    protected void initData() {
        gallery.postDelayed(new Runnable() {
            @Override
            public void run() {
                gallery.checkTab(0);
            }
        }, 100l);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            if(requestCode == CameraGalleryUtils.CROP_PIC){
//                Log.e(TAG, "dir:" + path);
                uploadImage(path);
            }
            return;
        }
        switch (requestCode){
            case CameraGalleryUtils.CROP_PIC:
                uploadImage(CameraGalleryUtils.cropTmpFile + fileName + ".jpg");

                break;
//            case Constants.VIDEO_EDITOR:
//                ItemModel video = data.getParcelableExtra(Constants.VIDEO_MODEL);
//                uploadVideo(video.getPath());
//                break;
        }
    }

    private void uploadImage( String filePath){
        final File file = new File(filePath);
        ItemModel model = new ItemModel(0, file.getPath(), MimeType.JPEG.toString(), file.length(), file.lastModified(), 0);
//                List<ItemModel> models = new ArrayList<>();
//                models.add(model);
        Intent data = new Intent();
        data.putExtra(CameraGalleryUtils.THUMB_FILE, model);
        data.putExtra("listType", title);
        setResult(RESULT_OK, data);
        CameraGalleryUtils.getInstance().updateGallery(this, file);
        back();
    }

    private void uploadVideo(String path){
        File file = new File(path);
        ItemModel model = new ItemModel(0, path, MimeType.MP4.toString(), file.length(), file.lastModified(), 0);
        Intent intent = new Intent();
        intent.putExtra(CameraGalleryUtils.THUMB_FILE, model);
        intent.putExtra("listType", title);
        setResult(RESULT_OK, intent);
        back();
    }

    @Override
    public void back() {
        if(isPreview){
            gallery.closePreview();
        }else
            super.back();
    }
}
