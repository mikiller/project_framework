package com.uilib.mxgallery.models;

import android.content.Context;
import android.util.Log;

import com.netlib.mkokhttp.uploader.UploadInfo;
import com.uilib.mxgallery.utils.CameraGalleryUtils;
import com.uilib.mxgallery.utils.GalleryLoaderUtils;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Mikiller on 2017/5/25.
 */

public class ReportResModel implements Serializable {
    private static final long serialVersionUID = -7654326998116848154L;
    //    private String id;
//    private String reportId;
    private File resFile;
    //    private MimeType mimeType;
//    private long duration;
    private ItemModel fileModel;
    private UploadInfo info;

    public ReportResModel(ItemModel fileModel) {
        this.fileModel = fileModel;
        resFile = new File(fileModel.getPath());
    }

    public ReportResModel(Context context, File file, String mimeType, long duration){
        resFile = file;
        fileModel = new ItemModel(file.getPath(), GalleryLoaderUtils.getFileUri(context, file), mimeType, file.length(), file.lastModified(), duration);
    }

    //    public ReportResModel(String id, MimeType mimeType, File resFile) {
//        this.mimeType = mimeType;
//        this.resFile = resFile;
//        this.id = id;
//        Log.e("res model", "id: " + id);
//    }

    //    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getReportId() {
//        return reportId;
//    }
//
//    public void setReportId(String reportId) {
//        this.reportId = reportId;
//    }
//
//    public long getDuration() {
//        return duration;
//    }
//
//    public void setDuration(long duration) {
//        this.duration = duration;
//    }
//
//    public MimeType getMimeType() {
//        return mimeType;
//    }
//
//    public void setMimeType(MimeType mimeType) {
//        this.mimeType = mimeType;
//    }
//
    public File getResFile() {
        return resFile;
    }

    public void setResFile(File resFile) {
        this.resFile = resFile;
    }

    public void createUploadInfo(String taskKey, String url, int state){
        info = new UploadInfo();
        info.setTaskKey(taskKey);
        info.setUrl(url /*+ resFile.getName()*/);
        info.setUploadLength(resFile.length());
        info.setState(state);
    }

    public ItemModel getFileModel() {
        return fileModel;
    }

    public void setFileModel(ItemModel fileModel) {
        this.fileModel = fileModel;
    }

    public UploadInfo getUploadInfo() {
        return info;
    }

    public void setUploadInfo(UploadInfo info) {
        this.info = info;
    }

    public int getProgress(){
        return (int) info.getProgress();
    }

    public void setProgress(int progress){
        info.setProgress(progress);
    }

    public int getState(){
        return info.getState();
    }

    public void setState(int state){
        info.setState(state);
    }
}
