package com.mtk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2015/5/28.
 * 边界
 */
public class BoundDragView extends LinearLayout implements View.OnClickListener {
    public BoundDragView(Context context) {
        super(context);
        this.setOnClickListener(this);
    }

    public BoundDragView(Context context, AttributeSet set) {
        super(context, set);
        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.setOnClickListener(null);
    }
}