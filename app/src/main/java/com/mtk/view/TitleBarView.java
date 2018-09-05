package com.mtk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruanan.btnotification.R;

/**
* 顶部导航栏
* @author YouGaoHui  839939978@qq.com
* Create at 2016/6/7 16:34
*/
public class TitleBarView extends RelativeLayout {
    private Context mContext;
    private TextView mTitle;
    private ImageView titleIcon;
    private RelativeLayout titleLeft;
    private RelativeLayout titleRight;
    private View leftDivider;
    private View rightDivider;
    private LinearLayout titleDefault;
    private LinearLayout titleExt;
    private LinearLayout itembg;

    public TitleBarView(Context context) {
        super(context);
        this.mContext = context;
        this.init();
    }

    public TitleBarView(Context context, AttributeSet set) {
        super(context, set);
        this.mContext = context;
        this.init();
    }

    private void init() {
        LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_title_bar, this);//this参数相当于将此布局文件加入到此控件中
        this.itembg = (LinearLayout) view.findViewById(R.id.item_bg);
        this.mTitle = (TextView) view.findViewById(R.id.title);
        this.titleIcon = (ImageView) view.findViewById(R.id.title_icon);
        this.leftDivider = view.findViewById(R.id.item_left_divider);
        this.rightDivider = view.findViewById(R.id.item_right_divider);
        this.titleLeft = (RelativeLayout) view.findViewById(R.id.title_left);
        this.titleRight = (RelativeLayout) view.findViewById(R.id.title_right);
        this.titleDefault = (LinearLayout) view.findViewById(R.id.layout_title_default);
        this.titleExt = (LinearLayout) view.findViewById(R.id.layout_title_ext);
    }

    public void titleHide() {
        this.titleDefault.setVisibility(View.GONE);
        this.titleExt.setVisibility(View.VISIBLE);
    }

    public void setLeftLayoutListener(View view,
                                      View.OnClickListener onClickListener) {
        this.titleLeft.removeAllViewsInLayout();
        RelativeLayout.LayoutParams relativeLayout = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        relativeLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
        view.setOnClickListener(onClickListener);
        this.titleLeft.addView(view, relativeLayout);
        this.titleLeft.setVisibility(View.VISIBLE);
    }

    public void setRightLayoutListener(View view,
                                       View.OnClickListener onClickListener,
                                       RelativeLayout.LayoutParams relativeLayout) {
        this.titleRight.removeAllViewsInLayout();
        view.setOnClickListener(onClickListener);
        this.titleRight.addView(view, relativeLayout);
        this.titleRight.setVisibility(View.VISIBLE);
    }

    public void showTitle() {
        this.titleDefault.setVisibility(View.VISIBLE);
        this.titleExt.setVisibility(View.GONE);
    }

    public void setRightLayoutListener(View view, View.OnClickListener listener) {
        RelativeLayout.LayoutParams relativeLayout = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        relativeLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
        this.setRightLayoutListener(view, listener, relativeLayout);
    }

    public RelativeLayout getLeftLayout() {
        return this.titleLeft;
    }

    public RelativeLayout getRightLayout() {
        return this.titleRight;
    }

    public CharSequence getTitle() {
        return this.mTitle.getText();
    }

    public void setTitle(CharSequence text) {
        this.mTitle.setText(text);
    }

    public TextView getTitleView() {
        return this.mTitle;
    }

    public void setLeftViewVisibility(int visibility) {
        this.titleLeft.setVisibility(visibility);
    }

    public void setRightViewVisibility(int visibility) {
        this.titleRight.setVisibility(visibility);
    }

    public void setTitle(int text) {
        this.mTitle.setText(text);
    }

    public void setTitleCenterView(View view) {
        final LinearLayout.LayoutParams linearLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.gravity = Gravity.CENTER;
        this.titleExt.removeAllViewsInLayout();
        this.titleExt.addView(view, linearLayout);
    }

    public void setTitleCenterView(View view, LinearLayout.LayoutParams layoutParams) {
        this.titleExt.removeAllViewsInLayout();
        this.titleExt.setLayoutParams(layoutParams);
        this.titleExt.addView(view, layoutParams);
    }

    public void setTitleCenterView1(View view, LinearLayout.LayoutParams layoutParams) {
        this.titleExt.removeAllViewsInLayout();
        this.titleExt.addView(view, layoutParams);
    }

    public void setTitleIcon(int imageResource) {
        this.titleIcon.setImageResource(imageResource);
    }

    public void setTitleIcon(Bitmap imageBitmap) {
        this.titleIcon.setImageBitmap(imageBitmap);
    }

    public void setTitleIcon(Drawable imageDrawable) {
        this.titleIcon.setImageDrawable(imageDrawable);
    }

    class bx implements View.OnClickListener {
        View.OnClickListener a;
        View b;
        TitleBarView c;

        bx(TitleBarView c, View.OnClickListener a, View b) {
            this.c = c;
            this.a = a;
            this.b = b;
        }

        public void onClick(View view) {
            if (this.a != null) {
                this.a.onClick(this.b);
            }

        }
    }

}