package com.uilib.richeditor;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uilib.R;
import com.uilib.customdialog.CustomDialog;
import com.uilib.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mikiller on 2017/7/20.
 */

public class MKEditView extends RelativeLayout implements View.OnClickListener{
    private RichEditor richEditor;
//    private LinearLayout ll_toolbar;
    private RelativeLayout rootView;
    private TextView tv_hint;
    private EditorToolBar toolBar;
    private int toolbarIcon;
//    private ImageView iv_img, iv_bold, iv_italic, iv_underline, iv_bullets, iv_numbers, iv_link, iv_video;
    List<RichEditor.Type> currentTypes = new ArrayList<>();
    onMediaBtnClickListener listener;

    private String imgUrl, link;

    public MKEditView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public MKEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public MKEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(final Context context, AttributeSet attrs, int defStyleAttr){
        LayoutInflater.from(context).inflate(R.layout.layout_mkedit_view, this, true);
        rootView = (RelativeLayout) findViewById(R.id.rl_root);
        richEditor = (RichEditor) findViewById(R.id.richeditor);
        if(attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MKEditView);
            toolbarIcon = ta.getInt(R.styleable.MKEditView_toolbarIcon, EditorToolBar.DEFAULT);
            ta.recycle();
        }
        toolBar = new EditorToolBar(context, attrs, defStyleAttr);
        toolBar.setToolbarIcon(toolbarIcon);
        toolBar.setClickListener(this);
        toolBar.setTxtSelectorListener(new EditorToolBar.OnTxtSelectorListener() {
            @Override
            public void onTxtColorSelected(int color) {
                richEditor.setTextColor(color);
            }

            @Override
            public void onTxtSizeSelected(int txtSize) {
                richEditor.setFontSize(txtSize);
            }
        });

        tv_hint = (TextView) findViewById(R.id.tv_hint);

        richEditor.setOnDecorationChangeListener(new RichEditor.OnDecorationStateListener() {
            @Override
            public void onStateChangeListener(String text, List<RichEditor.Type> types) {
                currentTypes.clear();
                currentTypes.addAll(types);
                toolBar.toggleBold(currentTypes.contains(RichEditor.Type.BOLD));
                toolBar.toggleItalic(currentTypes.contains(RichEditor.Type.ITALIC));
                toolBar.toggleUnderline(currentTypes.contains(RichEditor.Type.UNDERLINE));
                toolBar.toggleUnOrderedList(currentTypes.contains(RichEditor.Type.UNORDEREDLIST));
                toolBar.toggleOrderedList(currentTypes.contains(RichEditor.Type.ORDEREDLIST) && !currentTypes.contains(RichEditor.Type.UNORDEREDLIST));
                toolBar.toggleImage(currentTypes.contains(RichEditor.Type.HASIMAGE));
                toolBar.toggleVideo(currentTypes.contains(RichEditor.Type.HASVIDEO));
                MKEditView.this.requestLayout();
                Log.e("mkeditor", "on touch");
            }
        });
        richEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                int start = text.indexOf("<video id"), end = text.indexOf("</video>") + 8;
                if(!text.contains("</video><br>") && (start >= 0 && end > start)){
                    String subStr = text.substring(start, end);
                    richEditor.setHtml(text.replace(subStr, ""));
                }
                tv_hint.setVisibility(TextUtils.isEmpty(text) ? VISIBLE : GONE);
                toolBar.toggleTxtSize(false);
                toolBar.toggleTxtColor(false);
            }
        });

//        mediaWidth = richEditor.getWidth() - richEditor.getPaddingLeft() - richEditor.getPaddingRight();
//        mediaWidth = DisplayUtil.px2dip(getContext(), mediaWidth) ;
//        mediaHeight = mediaWidth * 9 / 16 ;
    }

    public void showToolbar(ViewGroup root, int posY){
        if(toolBar.getParent() == null) {
            root.addView(toolBar);
            toolBar.setToolbarPosition(posY);
        }
    }

    public void hideToolbar(final ViewGroup root){
        root.post(new Runnable() {
            @Override
            public void run() {
                root.removeView(toolBar);
            }
        });

    }

    public void pauseVideo(){
        richEditor.pauseVideo();
    }

    public void setToolbarBtnCallback(onMediaBtnClickListener listener){
        this.listener = listener;
    }

    public void insertImage(String path){
        richEditor.insertImage(String.valueOf(System.currentTimeMillis()), path);
    }

    public void insertImage(String path, int width, int height){
        richEditor.insertImage(String.valueOf(System.currentTimeMillis()), path);
    }

    public void insertVideo(String path){
        richEditor.insertVideo(String.valueOf(System.currentTimeMillis()), path);
    }

    public void setHtml(String html){
        richEditor.setHtml(html);
        tv_hint.setVisibility(TextUtils.isEmpty(html) ? VISIBLE : GONE);
    }

    public String getHtml(){
        return richEditor.getHtml();
    }

    public String getLink(){
        return link;
    }

    public void setLink(String link){
        if(TextUtils.isEmpty(link)) {
            this.link = link;
//            iv_link.setImageResource(R.mipmap.insert_link);
        }else {
            this.link = link.contains("http://") ? link : "http://".concat(link);
//            iv_link.setImageResource(R.mipmap.insert_link_enabled);
        }
        toolBar.toggleLink(!TextUtils.isEmpty(link));
    }

    public String getImgUrl(){
        return imgUrl;
    }

    public void setEditable(boolean editable){
        richEditor.setInputEnabled(editable);
    }

    public int getToolbarHeight(){
        return toolBar.getHeight();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.iv_img){
            if(listener != null)
                listener.onInsertImage();
//            richEditor.insertImage(String.valueOf(System.currentTimeMillis()), "http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG", "img");
        }else if(id == R.id.iv_bold){
            richEditor.setBold();
        }else if(id == R.id.iv_italic){
            richEditor.setItalic();
        }else if(id == R.id.iv_underline){
            richEditor.setUnderline();
        }else if(id == R.id.iv_bullets){
            richEditor.setBullets();
        }else if(id == R.id.iv_numbers){
            richEditor.setNumbers();
        }else if(id == R.id.iv_link){
            final CustomDialog dlg = new CustomDialog(getContext());
            dlg.setLayoutRes(R.layout.layout_editable_dlg).setTitle("阅读原文").setDlgButtonListener(new CustomDialog.onButtonClickListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onSure() {
                    setLink(((EditText)dlg.getContent(R.id.edt_content)).getText().toString());
                }
            }).show();
        }else if(id == R.id.iv_video){
            int width = richEditor.getWidth() - richEditor.getPaddingLeft() - richEditor.getPaddingRight();
            width = DisplayUtil.px2dip(getContext(), width);
            if(listener != null)
                listener.onInsertVideo();
//            richEditor.insertVideo(String.valueOf(System.currentTimeMillis()), "/storage/emulated/0/Movies/vr.mp4", width, (width ) *9 / 16 );
        }
    }

    public interface onMediaBtnClickListener{
        void onInsertImage();
        void onInsertVideo();
    }
}
