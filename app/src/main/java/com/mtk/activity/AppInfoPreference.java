package com.mtk.activity;

import com.mtk.data.Log;
import com.ruanan.btnotification.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This class is used for showing application info preference with a icon. 
 */
public class AppInfoPreference extends Preference {
    // Debugging
    private static final String LOG_TAG = "AppInfoPreference";
    
    // App info icon
    private Drawable mItemDrawable = null;

    public AppInfoPreference(Context context) {        
        super(context);
        
        Log.i(LOG_TAG, "AppInfoPreference(), AppInfoPreference constructioned!");
    }

    public AppInfoPreference(Context context, AttributeSet attr) {
        super(context, attr);
        
        // Create item drawable
        TypedArray array = context.obtainStyledAttributes(attr, R.styleable.ImagePreference);
        int icon = array.getResourceId(R.styleable.ImagePreference_image, 0);
        mItemDrawable = context.getResources().getDrawable(icon);
        array.recycle();
        
        Log.i(LOG_TAG, "AppInfoPreference(), AppInfoPreference constructioned!");
    }

    public AppInfoPreference(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        
        // Create item drawable
        TypedArray array = context.obtainStyledAttributes(attr, R.styleable.ImagePreference, defStyle, 0);
        int icon = array.getResourceId(R.styleable.ImagePreference_image, 0);
        mItemDrawable = context.getResources().getDrawable(icon);
        array.recycle();
        
        Log.i(LOG_TAG, "AppInfoPreference(), AppInfoPreference constructioned!");
    }

    
    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        
        Log.i(LOG_TAG, "onBindView(), Create AppInfoPreference view!");
        
        // Set item icon
        ImageView icon = (ImageView) view.findViewById(R.id.item_image);
        icon.setImageDrawable(mItemDrawable);
        
        // Set item summary
        TextView summary = (TextView) view.findViewById(R.id.item_summary);
        summary.setText(R.string.app_info_preference_sumarry);
        
        // Set preference unclickable
        setSelectable(false);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected View onCreateView(ViewGroup parent) {
        Log.i(LOG_TAG, "onCreateView()");
        
        return LayoutInflater.from(getContext()).inflate(R.layout.app_info_preference_layout, parent, false);
    }
}