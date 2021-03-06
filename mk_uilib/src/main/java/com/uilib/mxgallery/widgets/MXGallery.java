package com.uilib.mxgallery.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.uilib.mxgallery.listeners.OnBottomBarListener;
import com.uilib.mxgallery.models.ItemModel;
import com.uilib.mxgallery.models.MimeType;
import com.uilib.R;
import com.uilib.mxgallery.adapters.GalleryItemsAdapter;
import com.uilib.mxgallery.defaultloaders.MediaLoader;
import com.uilib.mxgallery.listeners.GalleryTabListener;
import com.uilib.mxgallery.listeners.OnBottomBtnClickListener;
import com.uilib.mxgallery.listeners.OnMediaItemClickListener;
import com.uilib.mxgallery.utils.GalleryLoaderUtils;

import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Mikiller on 2017/5/11.
 */

public class MXGallery extends RelativeLayout implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = this.getClass().getSimpleName();
    public static final String ISMULTIPLE = "is_multiple";
    public static final String MAX_SELECT = "max_select";
    /**头部标签栏
     * 可根据需求显示或隐藏
     * 通过属性{@code R.styleable.MXGallery_needTab}设置
     * @see #setTabNames(GalleryTabListener listener, String... names)
     * */
    private GalleryTabGroup tabGroup;
    /**媒体列表*/
    private RecyclerView rcv_gallery;
    /**底部操作栏*/
    private BottomBar bottomBar;

    private GalleryItemsAdapter itemsAdapter;
    private OnBottomBarListener selectListener;
    private MediaCollection mediaCollection = null;
    @NonNull
    private FragmentManager fgmtMgr;
    private PreviewFragment fragment;
    private Loader<Cursor> contentLoader;

    private int mimeType = 0;
    private int columnNum = 4, maxSelectionCount = 9;
    private float itemMargin = 8;
    private boolean needEdge = false, isMultiple = true;
    /**目录id*/
    private String bucketId = null;

    public MXGallery(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public MXGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public MXGallery(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(final Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_mxgallery, this, true);
        tabGroup = (GalleryTabGroup) findViewById(R.id.tabGroup);
        rcv_gallery = (RecyclerView) findViewById(R.id.rcv_gallery);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MXGallery);
            setColumnNum(ta.getInt(R.styleable.MXGallery_columnNum, 4));
            setItemMargin(ta.getDimension(R.styleable.MXGallery_itemMargin, 16f));
            setMaxSelectionCount(ta.getInt(R.styleable.MXGallery_maxSltCount, 9));
            setNeedEdge(ta.getBoolean(R.styleable.MXGallery_needEdge, false));
            setIsMultiple(ta.getBoolean(R.styleable.MXGallery_isMultiple, true));
//            needCapture = ta.getBoolean(R.styleable.MXGallery_needCapture, false);
            tabGroup.setVisibility(ta.getBoolean(R.styleable.MXGallery_needTab, true) ? VISIBLE : GONE);
            ta.recycle();
        }
        fgmtMgr = ((FragmentActivity)getContext()).getSupportFragmentManager();
    }

    public void setColumnNum(int num){
        this.columnNum = num;
    }

    public void setItemMargin(float margin){
        this.itemMargin = margin;
    }

    public void setNeedEdge(boolean isNeed){
        this.needEdge = isNeed;
    }

    public void setIsMultiple(boolean isMultiple){
        this.isMultiple = isMultiple;
    }

    public void setMaxSelectionCount(int max) {
        this.maxSelectionCount = max;
    }

    public void setMimeType(Set<MimeType> mimeTypeSet) {
        for (MimeType type : mimeTypeSet) {
            this.mimeType ^= type.getMimeTypeId();
        }
        //bottomBar.setVisibility(MimeType.isPic(this.mimeType) ? VISIBLE : GONE);
    }

    public int getMimeType(){
        return mimeType;
    }

    /**相册被构建后通过调用{@code onCreate}初始化数据集
     * 若有参数需要动态设置，需在此函数之前调用
     * @see #isMultiple
     * @see #maxSelectionCount
     * @see #columnNum
     * @see #itemMargin
     * @see #needEdge
     * @see #mimeType
     * */
    public void onCreate(Bundle bundle) {
        if(bundle != null)
            mediaCollection = bundle.getParcelable(MediaCollection.SET_KEY);
        else if(mediaCollection == null)
            mediaCollection = new MediaCollection(isMultiple, maxSelectionCount);

        rcv_gallery.setLayoutManager(new GridLayoutManager(getContext(), columnNum));
        rcv_gallery.setHasFixedSize(true);
        rcv_gallery.addItemDecoration(new MediaItemDecoration(columnNum, itemMargin, needEdge));

        itemsAdapter = new GalleryItemsAdapter(getContext(),mediaCollection, columnNum, itemMargin);
        itemsAdapter.setNeedFirstItem(true);
        itemsAdapter.setMimeType(this.mimeType);
        itemsAdapter.setItemClickeListener(new OnMediaItemClickListener() {
            @Override
            public void onItemChecked(ItemModel item, boolean isChecked) {
                if (isChecked)
                    checkItem(item);
                else
                    unCheckItem(item);
                updateCollectionNum(mediaCollection.getSelectedCount());
            }
        });
        rcv_gallery.setAdapter(itemsAdapter);

        bottomBar.setBtnListener(new OnBottomBtnClickListener() {
            @Override
            public void onLeftClick() {
                openPreviewPage(null, true);
            }

            @Override
            public void onRightClick() {
                if(selectListener != null)
                    selectListener.onConfirm(mediaCollection.getModels());
            }
        });
        GalleryLoaderUtils.initLoaderManager(getContext(), MediaLoader.LOADER_ID, this);
    }

    public void onSaveInstanceState(Bundle outState) {
        mediaCollection.onSaveInstanceState(outState);
    }

    public void setSelectedPaths(List<String > paths){
        mediaCollection.setSelectedModelPath(paths);
    }

    private void checkItem(ItemModel item) {
        if (mediaCollection.isMultiple) {
            mediaCollection.addMedia(item);
        } else {
            mediaCollection.removeLastMedia();
            mediaCollection.addMedia(item);
        }
    }

    private void unCheckItem(ItemModel item) {
        if (mediaCollection.isMultiple) {
            mediaCollection.removeMedia(item);
        } else
            mediaCollection.removeLastMedia();
    }

    private void updateCollectionNum(int selectedNum) {
        tabGroup.updateTab(selectedNum);
        bottomBar.updateBtnState(selectedNum > 0);
        bottomBar.updateNum(selectedNum);
    }

    private void openPreviewPage(final ItemModel item, boolean isCheck) {
        final PreviewFragment fragment = PreviewFragment.newInstance(mediaCollection, item, isCheck);
        fragment.setOnSureListener(new PreviewFragment.OnSureListener() {
            @Override
            public void addNewMedia(ItemModel model) {
                if (mediaCollection.canSelectModel(model)) {
                    checkItem(model);
                    updateCollectionNum(mediaCollection.getSelectedCount());
                    //if(MimeType.isPic(mimeType))
                        itemsAdapter.notifyDataSetChanged();
                    /*else*/ if(selectListener != null) {
                        selectListener.onPreView(false);
                        selectListener.onConfirm(mediaCollection.getModels());
                    }
                }
            }

            @Override
            public void confirmSelectedMedia() {
                if(selectListener != null) {
                    selectListener.onPreView(false);
                    selectListener.onConfirm(mediaCollection.getModels());
                }
            }

            @Override
            public void onBack(){
                if(selectListener != null){
                    selectListener.onPreView(false);
                }
            }
        });
        fgmtMgr.beginTransaction().add(R.id.fl_preview, fragment, "preview").commitAllowingStateLoss();
        if(selectListener != null)
            selectListener.onPreView(true);
    }



    public void setTabNames(GalleryTabListener listener, String... names) {
        tabGroup.setTabNames(listener, names);
    }

    public void closePreview(){
        fgmtMgr.beginTransaction().remove(fragment).commit();
        if(selectListener != null)
            selectListener.onPreView(false);
    }

    public void setContentLoader(Loader<Cursor> loader) {
        contentLoader = loader;
    }

    public void setSelectListener(OnBottomBarListener selectListener) {
        this.selectListener = selectListener;
    }

    public void checkTab(int tab){
        tabGroup.checkTab(tab);
    }

    public void updateItems(boolean isNeedShowSelected) {
        mediaCollection.isNeedShowSelected = isNeedShowSelected;
        GalleryLoaderUtils.initLoaderManager(getContext(), MediaLoader.LOADER_ID, this);
    }

    public int getSelectedItemCount() {
        return mediaCollection.getSelectedCount();
    }

    public boolean onBackClicked() {
        return fgmtMgr == null ? true : !(fgmtMgr.getFragments().size() > 0);
    }

    public void setBucketId(String id){
        bucketId = id;
        contentLoader = null;
        GalleryLoaderUtils.destoryLoaderManager(MediaLoader.LOADER_ID);
        GalleryLoaderUtils.initLoaderManager(getContext(), MediaLoader.LOADER_ID, this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int type = MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
        if (MimeType.isPic(mimeType))
            type = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        else if (MimeType.isVideo(mimeType))
            type = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        if(contentLoader == null)
            contentLoader = MediaLoader.newInstance(getContext(),
                    type,
                    mediaCollection.isNeedShowSelected ? null : mediaCollection.getSelectedModelPath(), bucketId);
        return contentLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mediaCollection.isNeedShowSelected)
            mediaCollection.totalImage = data.getCount();
        updateCollectionNum(mediaCollection.getSelectedCount());
        itemsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        itemsAdapter.swapCursor(null);
    }

}
