package com.mtk.view.pulltorefresh;

import com.ruanan.btnotification.R;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class PullToRefreshListViewFooter extends LinearLayout {
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;

	private Context mContext;

	private String pullTip;
	private String readyTip;
	
	private View mContentView;
	private View mProgressBar;
	private TextView mHintView;
	
	public PullToRefreshListViewFooter(Context context) {
		super(context);
		initView(context);
	}
	
	public PullToRefreshListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	
	public void setState(int state) {
		mHintView.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);
		mHintView.setVisibility(View.INVISIBLE);
		if (state == STATE_READY) {
			mHintView.setVisibility(View.VISIBLE);
			if(TextUtils.isEmpty(readyTip))
				mHintView.setText(R.string.xlistview_footer_hint_ready);
			else
				mHintView.setText(readyTip);
		} else if (state == STATE_LOADING) {
			mProgressBar.setVisibility(View.VISIBLE);
		} else {
			mHintView.setVisibility(View.VISIBLE);
			if(TextUtils.isEmpty(pullTip))
				mHintView.setText(R.string.xlistview_footer_hint_normal);
			else
				mHintView.setText(pullTip);
		}
	}
	
	public void setBottomMargin(int height) {
		if (height < 0) return ;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		lp.bottomMargin = height;
		mContentView.setLayoutParams(lp);
	}
	
	public int getBottomMargin() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		return lp.bottomMargin;
	}
	
	
	/**
	 * normal status
	 */
	public void normal() {
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}
	
	
	/**
	 * loading status 
	 */
	public void loading() {
		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	/**
	 * hide footer when disable pull load more
	 */
	public void hide() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		lp.height = 0;
		mContentView.setLayoutParams(lp);
	}
	
	/**
	 * show footer
	 */
	public void show() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);
	}
	
	private void initView(Context context) {
		mContext = context;
		LinearLayout moreView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.xlistview_footer, null);
		addView(moreView);
		moreView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		mContentView = moreView.findViewById(R.id.xlistview_footer_content);
		mProgressBar = moreView.findViewById(R.id.xlistview_footer_progressbar);
		mHintView = (TextView)moreView.findViewById(R.id.xlistview_footer_hint_textview);
	}
	
	public void setFooterTipLabel(String pullTip, String readyTip) {
		this.pullTip = pullTip;
		this.readyTip = readyTip;
	}
	
	
}
