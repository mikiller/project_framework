package com.uilib.mxgallery.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.uilib.R;
import com.uilib.mxgallery.models.ItemModel;
import com.uilib.mxgallery.models.MimeType;
import com.uilib.mxgallery.models.ReportResModel;
import com.uilib.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Mikiller on 2017/4/24.
 */

public class CameraGalleryUtils {
    private final static String TAG = CameraGalleryUtils.class.getSimpleName();
    public final static int CAMERA_CAPTURE = 0x100, CROP_PIC = 0x200, VIDEO = 0x300, FILES = 0x400, CROP_VIDEO = 0x500;
    public final static String THUMB_FILE = "thumbfile";
    //private Context mContext;
    public static String thumbDir = Environment.getExternalStorageDirectory().getPath().concat(File.separator).concat("@Radio/camera/");
    public static String cropTmpFile = thumbDir.concat("interview_crop_");
    private List<ReportResModel> thumbList;
    public ReportResModel tmpFile;
    public String cropFileName = "";

    public List<ReportResModel> getThumbList() {
        return thumbList;
    }

    public String getCropFileName(){
        return cropFileName;
    }

    public void clearCropFileName(){
        cropFileName = "";
    }

    public void addThumbModel(Context context, ReportResModel model) {
        thumbList.add(model);
        updateGallery(context, model.getResFile());
    }

    public void addAllThumbModels(List<ItemModel> models) {
        for (ItemModel model : models) {
            thumbList.add(new ReportResModel(model));
        }
    }

//    public ReportResModel createReportResModel(File file) {
//        String type = getFileMimeType(file.getPath());
//        ReportResModel model = new ReportResModel(createUUID(), MimeType.getMimeTypeWithTypeName(type), file);
//        return model;
//
//    }

    public String createUUID() {
        String id = UUID.randomUUID().toString();
        return id.replaceAll("-", "");
    }

    private CameraGalleryUtils() {
        thumbList = new ArrayList<>();
    }

    private static class CameraGalleryUtilsFactory {
        private static CameraGalleryUtils instance = new CameraGalleryUtils();
    }

    public static CameraGalleryUtils getInstance(Context context) {
        //CameraGalleryUtilsFactory.instance.mContext = context;
        return getInstance();
    }

    public static CameraGalleryUtils getInstance() {
        return CameraGalleryUtilsFactory.instance;
    }

    public void openVideoEditor(ReportResModel file) {
        Map<String, Object> args = new HashMap<>();
        args.put(THUMB_FILE, file);
//        ActivityManager.startActivityforResult((Activity) mContext, VideoEditorActivity.class, CROP_VIDEO, args);
    }

    public void openSysCamera(Context context) {
        startSysActivity(context, MediaStore.ACTION_IMAGE_CAPTURE);
    }

    public void openSysVideo(Context context) {
        startSysActivity(context, MediaStore.ACTION_VIDEO_CAPTURE);
    }

    private void startSysActivity(Context context, String action) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String fileName = "interview_" + StringUtils.getDateStr("yyyy-MM-dd_hh:mm:ss", System.currentTimeMillis());
            int requestCode = 0;
            if (action.equals(MediaStore.ACTION_IMAGE_CAPTURE)) {
                fileName = fileName.concat(".jpg");
                requestCode = CAMERA_CAPTURE;
                tmpFile = new ReportResModel(context, new File(thumbDir, fileName), MimeType.JPEG.toString(), 0);
            } else if (action.equals(MediaStore.ACTION_VIDEO_CAPTURE)) {
                fileName = fileName.concat(".mp4");
                requestCode = VIDEO;
                File file = new File(thumbDir, fileName);
                tmpFile = new ReportResModel(context, file, MimeType.THREEGPP.toString(), 0);
            }
            try {
                if (!tmpFile.getResFile().getParentFile().exists())
                    tmpFile.getResFile().getParentFile().mkdirs();
                tmpFile.getResFile().createNewFile();

                Intent intent = new Intent(action);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tmpFile.getFileModel().getContentUri());
                Log.e(TAG, "output path: " + tmpFile.getFileModel().getContentUri());
                ((Activity) context).startActivityForResult(intent, requestCode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "cannot get photo save path", Toast.LENGTH_SHORT).show();
        }
    }

    public void cropPhoto(Context context, Uri thumbUri, String filename, float cropX, float cropY) {
        Log.e(TAG, "crop file name: " + filename);
        File file = new File(cropFileName = (CameraGalleryUtils.cropTmpFile + filename + ".jpg"));
        if (!file.exists())
            try {
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(thumbUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", cropX);
        intent.putExtra("aspectY", cropY);
        intent.putExtra("scale", true);
//        if (cropY != 1 && cropX != 1) {
//            intent.putExtra("outputX", 180);
//            intent.putExtra("outputY", 100);
//        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        ((Activity) context).startActivityForResult(intent, CROP_PIC);

    }

    public String getFileMimeType(String path) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path.trim()));
    }

    public void openSysGallery(Context context) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT, null);
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*");
        ((Activity) context).startActivityForResult(i, FILES);
    }

    public void updateGallery(Context context, final File file) {
        Log.e(TAG, "update file path: " + file.getAbsolutePath());
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, GalleryLoaderUtils.getFileUri(context, file)));
    }

    public long getMediaDuration(String filePath){
        MediaMetadataRetriever mdr = new MediaMetadataRetriever();
        mdr.setDataSource(filePath);
        String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mdr.release();
        if(!TextUtils.isEmpty(duration))
            return Long.parseLong(duration);
        else
            return 0;
    }

    public void release() {
        thumbList.clear();
    }

    public Bitmap getThumbImg(Context context, Uri uri) throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), new Rect(), options);
        Log.e(TAG, "height: " + options.outHeight + ", width: " + options.outWidth);
        int scaleHeight = Math.round(options.outHeight / 210);
        int scaleWidth = Math.round(options.outWidth / 280);
        int scale = scaleHeight < scaleWidth ? scaleHeight : scaleWidth;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), new Rect(), options).copy(Bitmap.Config.RGB_565, false);
        return bmp;
    }
}
