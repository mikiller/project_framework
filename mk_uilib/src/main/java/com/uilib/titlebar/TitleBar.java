package com.uilib.titlebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikiller.mkglidelib.imageloader.GlideImageLoader;
import com.uilib.R;
import com.uilib.joooonho.SelectableRoundedImageView;
import com.uilib.utils.AnimUtils;

/**
 * Created by Mikiller on 2017/9/12.
 */

public class TitleBar extends RelativeLayout {
    private View view_bg;
    private ImageButton btn_back, btn_more;
    private TextView tv_act_title, tv_act_sure;
    private int txtColor;
    private Drawable subIcon;
    private TitleListener listener;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr){
        LayoutInflater.from(context).inflate(R.layout.layout_titlebar, this, true);
        view_bg = findViewById(R.id.view_bg);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_more = (ImageButton) findViewById(R.id.btn_more);
        tv_act_title = (TextView) findViewById(R.id.tv_act_title);
        tv_act_sure = (TextView) findViewById(R.id.tv_act_sure);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        if(ta != null){
            setBgColor(ta.getColor(R.styleable.TitleBar_titleBgColor, getResources().getColor(R.color.colorPrimary)));
            setTxtColor(ta.getColor(R.styleable.TitleBar_barTxtColor, Color.WHITE));
            setTitle(ta.getString(R.styleable.TitleBar_titleTxt));
            setSubTxt(ta.getString(R.styleable.TitleBar_subTxt));
            setSubIcon(ta.getDrawable(R.styleable.TitleBar_subIcon));
            setSubImg(ta.getResourceId(R.styleable.TitleBar_subImg, NO_ID));
            ta.recycle();
        }

        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onBackClicked();
            }
        });

        btn_more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onMoreClicked();
            }
        });

        tv_act_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onSubClicked();
            }
        });
    }

    public void setTxtColor(int color){
        txtColor = color;
        btn_back.setImageResource(color == Color.WHITE ? R.mipmap.ic_back_white : R.mipmap.ic_back_black);
        tv_act_title.setTextColor(color);
        tv_act_sure.setTextColor(color);
    }

    public void setTitle(String title){
        if(!TextUtils.isEmpty(title))
            tv_act_title.setText(title);
    }

    public void setSubTxt(String subTxt){
        if(!TextUtils.isEmpty(subTxt))
            tv_act_sure.setText(subTxt);
    }

    public void setSubIcon(Drawable drawable){
        if(drawable != null){
            subIcon = drawable;
            subIcon.setBounds(new Rect(0,0, drawable.getMinimumWidth(), drawable.getMinimumHeight()));
            tv_act_sure.setCompoundDrawables(subIcon, null, null, null);
        }
    }

    public void setBackImg(int resId){
        btn_back.setImageResource(resId);
    }

    public void setSubImg(int resId){
        if(resId != NO_ID) {
            btn_more.setImageResource(resId);
            btn_more.setVisibility(VISIBLE);
        }else{
            btn_more.setVisibility(GONE);
        }
    }

    public void setSubTxtEnabled(boolean enabled){
        tv_act_sure.setEnabled(enabled);
    }

    public void setBgColor(int color){
        view_bg.setBackgroundColor(color);
    }

    public void setBgAlpha(float alpha){
        view_bg.setAlpha(alpha);
    }

    public float getBgAlpha(){
        return view_bg.getAlpha();
    }

    public View getTitleBg(){
        return view_bg;
    }

    public void setTitleListener(TitleListener listener) {
        this.listener = listener;
    }

    public static abstract class TitleListener{
        protected void onBackClicked(){}
        protected void onSubClicked(){}
        protected void onMoreClicked(){}
    }
}
