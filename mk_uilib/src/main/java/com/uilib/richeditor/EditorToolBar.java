package com.uilib.richeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.uilib.R;

/**
 * Created by Mikiller on 2018/8/22.
 */

public class EditorToolBar extends LinearLayout{
    public static int DEFAULT = 0x3777, BOLD = 0X01, ITALIC = 0X10, UNDERLINE = 0X100, BULLET = 0X1000, NUMBER = 0X02, TXTSIZE = 0X20, TXTCOLOR = 0X200, LINK = 0X2000, IMG = 0X4, VIDEO = 0X40;
    private LinearLayout ll_toolbar;
    private ImageView iv_img, iv_bold, iv_italic, iv_underline, iv_txtsize, iv_txtcolor, iv_bullets, iv_numbers, iv_link, iv_video;
    private RadioGroup rdg_selector;
    private RadioButton[] rdbTxtSizes = new RadioButton[3], rdbTxtColors = new RadioButton[6];
    private boolean isTxtColorSelected = false, isTxtSizeSelected = false;
    private int currentTxtSize = 3, currentTxtColor = 0x222222;
    private RadioGroup.OnCheckedChangeListener selectorChangeListener;
    private OnTxtSelectorListener selectorListener;
    private OnClickListener clickListener;
    public EditorToolBar(Context context) {
        this(context, null, 0);
    }

    public EditorToolBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditorToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @SuppressLint("ResourceType")
    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.layout_editor_toolbar, this, true);
        ll_toolbar = findViewById(R.id.ll_toolbar);
        iv_img = (ImageView) findViewById(R.id.iv_img);
        iv_bold = (ImageView) findViewById(R.id.iv_bold);
        iv_italic = (ImageView) findViewById(R.id.iv_italic);
        iv_underline = (ImageView) findViewById(R.id.iv_underline);
        iv_txtsize = findViewById(R.id.iv_txtsize);
        iv_txtcolor = findViewById(R.id.iv_txtcolor);
        iv_bullets = (ImageView) findViewById(R.id.iv_bullets);
        iv_numbers = (ImageView) findViewById(R.id.iv_numbers);
        iv_link = (ImageView) findViewById(R.id.iv_link);
        iv_video = (ImageView) findViewById(R.id.iv_video);
        rdg_selector = findViewById(R.id.rdg_selector);

        rdbTxtSizes[0] = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.rdb_editor_txtsize_small, null);
        rdbTxtSizes[0].setId(2);
        rdbTxtSizes[1] = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.rdb_editor_txtsize_normal, null);
        rdbTxtSizes[1].setId(3);
        rdbTxtSizes[2] = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.rdb_editor_txtsize_big, null);
        rdbTxtSizes[2].setId(4);
        rdbTxtColors[0] = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.rdb_editor_txtcolor_black, null);
        rdbTxtColors[0].setId(0x222222);
        rdbTxtColors[1] = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.rdb_editor_txtcolor_gray, null);
        rdbTxtColors[1].setId(0x666666);
        rdbTxtColors[2] = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.rdb_editor_txtcolor_red, null);
        rdbTxtColors[2].setId(0xff4165);
        rdbTxtColors[3] = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.rdb_editor_txtcolor_yellow, null);
        rdbTxtColors[3].setId(0xffb82f);
        rdbTxtColors[4] = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.rdb_editor_txtcolor_blue, null);
        rdbTxtColors[4].setId(0x4391ff);
        rdbTxtColors[5] = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.rdb_editor_txtcolor_green, null);
        rdbTxtColors[5].setId(0x5dd7ae);
        selectorChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == group.getCheckedRadioButtonId()){
                    if(isTxtSizeSelected) {
                        currentTxtSize = checkedId;
                        if(selectorListener != null)
                            selectorListener.onTxtSizeSelected(checkedId);
                    }else if(isTxtColorSelected) {
                        currentTxtColor = checkedId;
                        if(selectorListener != null)
                            selectorListener.onTxtColorSelected(checkedId);
                    }
                }
            }
        };
        iv_txtsize.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTxtColorSelected){
                    toggleTxtColor(!isTxtColorSelected);
                }
                toggleTxtSize(!isTxtSizeSelected);
            }
        });
        iv_txtcolor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTxtSizeSelected)
                    toggleTxtSize(!isTxtSizeSelected);
                toggleTxtColor(!isTxtColorSelected);
            }
        });
    }

    public void setToolbarIcon(int toolbarIcon){
        iv_img.setVisibility(hasIcon(toolbarIcon, IMG) ? VISIBLE : GONE);
        iv_bold.setVisibility(hasIcon(toolbarIcon, BOLD) ? VISIBLE : GONE);
        iv_italic.setVisibility(hasIcon(toolbarIcon, ITALIC) ? VISIBLE : GONE);
        iv_underline.setVisibility(hasIcon(toolbarIcon, UNDERLINE) ? VISIBLE : GONE);
        iv_bullets.setVisibility(hasIcon(toolbarIcon, BULLET) ? VISIBLE : GONE);
        iv_numbers.setVisibility(hasIcon(toolbarIcon, NUMBER) ? VISIBLE : GONE);
        iv_txtsize.setVisibility(hasIcon(toolbarIcon, TXTSIZE) ? VISIBLE : GONE);
        iv_txtcolor.setVisibility(hasIcon(toolbarIcon, TXTCOLOR) ? VISIBLE : GONE);
        iv_link.setVisibility(hasIcon(toolbarIcon, LINK) ? VISIBLE : GONE);
        iv_video.setVisibility(hasIcon(toolbarIcon, VIDEO) ? VISIBLE : GONE);
    }

    private boolean hasIcon(int toolbarIcon, int icon){
        return (toolbarIcon | icon) == toolbarIcon;
    }

    public boolean isSelectorTxtSize(){
        return isTxtSizeSelected;
    }

    public boolean isSelectorTxtColor(){
        return isTxtColorSelected;
    }

    public void setClickListener(OnClickListener listener){
        if(listener != null){
            iv_img.setOnClickListener(listener);
            iv_bold.setOnClickListener(listener);
            iv_italic.setOnClickListener(listener);
            iv_underline.setOnClickListener(listener);
//            iv_txtsize.setOnClickListener(listener);
//            iv_txtcolor.setOnClickListener(listener);
            iv_bullets.setOnClickListener(listener);
            iv_numbers.setOnClickListener(listener);
            iv_link.setOnClickListener(listener);
            iv_video.setOnClickListener(listener);
        }
    }

    public void setTxtSelectorListener(OnTxtSelectorListener listener){
        selectorListener = listener;
    }

    public void toggleBold(boolean isBold){
        iv_bold. setImageResource(isBold ? R.mipmap.bold_enabled : R.mipmap.bold);
    }

    public void toggleItalic(boolean isItalic){
        iv_italic.setImageResource(isItalic ? R.mipmap.italic_enabled : R.mipmap.italic);
    }

    public void toggleUnderline(boolean isUnderline){
        iv_underline.setImageResource(isUnderline ? R.mipmap.underline_enabled : R.mipmap.underline);
    }

    public void toggleTxtSize(boolean isSelected){
        iv_txtsize.setImageResource(isSelected ? R.mipmap.ic_txtsize_selected : R.mipmap.ic_txtsize);
        isTxtSizeSelected = isSelected;
        if(isSelected)
            showTxtSizeSelector();
        else
            hideRdgSelector();
    }

    public void showTxtSizeSelector(){
        for(RadioButton rdb : rdbTxtSizes){
            rdg_selector.addView(rdb);
        }
        rdg_selector.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),  MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        rdg_selector.setX(iv_txtsize.getX() - rdg_selector.getMeasuredWidth() / 2 + iv_txtsize.getWidth() / 2);
        showRdgSelector(currentTxtSize);
    }

    public void toggleTxtColor(boolean isSelected){
        iv_txtcolor.setImageResource(isSelected ? R.mipmap.ic_color_selected : R.mipmap.ic_color);
        isTxtColorSelected = isSelected;
        if(isSelected)
            showTxtColorSelector();
        else
            hideRdgSelector();
    }

    public void showTxtColorSelector(){
        for(RadioButton rdb : rdbTxtColors){
            rdg_selector.addView(rdb);
        }
        rdg_selector.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),  MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        rdg_selector.setX(iv_txtcolor.getX() - rdg_selector.getMeasuredWidth() / 2 + iv_txtcolor.getWidth() / 2);
        showRdgSelector(currentTxtColor);
    }

    private void showRdgSelector(int currentCheck){
        rdg_selector.check(currentCheck);
        rdg_selector.setOnCheckedChangeListener(selectorChangeListener);
        rdg_selector.setVisibility(VISIBLE);
    }

    private void hideRdgSelector(){
        rdg_selector.removeAllViews();
        rdg_selector.setOnCheckedChangeListener(null);
        rdg_selector.check(View.NO_ID);
        rdg_selector.setVisibility(GONE);
    }

    public void toggleUnOrderedList(boolean isUnOrderedList){
        iv_bullets.setImageResource(isUnOrderedList ? R.mipmap.bullets_enabled : R.mipmap.bullets);
    }

    public void toggleOrderedList(boolean isOrderedList){
        iv_numbers.setImageResource(isOrderedList ? R.mipmap.numbers_enabled : R.mipmap.numbers);
    }

    public void toggleLink(boolean hasLink){
        iv_link.setImageResource(hasLink ? R.mipmap.insert_link_enabled : R.mipmap.insert_link);
    }

    public void toggleImage(boolean hasImg){
        iv_img.setImageResource(hasImg ? R.mipmap.insert_image_enabled : R.mipmap.insert_image);
    }

    public void toggleVideo(boolean hasVideo){
        iv_video.setImageResource(hasVideo ? R.mipmap.insert_video_enabled : R.mipmap.insert_video);
    }

    public void setToolbarPosition(int posY){
        ((MarginLayoutParams)ll_toolbar.getLayoutParams()).setMargins(0,0,0,posY);
    }

    public interface OnTxtSelectorListener{
        void onTxtColorSelected(int color);
        void onTxtSizeSelected(int txtSize);
    }
}
