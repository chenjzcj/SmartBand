package com.mtk.view.pulltorefresh;

public interface IPullToRefresh {
	
	/**
	 * enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable);
	
	/**
	 * @param pullTip 例如，下拉刷新
	 * @param readyTip 例如，松开刷新
	 */
	public void setHeaderTipLabel(String pullTip, String readyTip);
	
	/**
	 * @param pullTip 例如，查看更多
	 * @param readyTip 例如，松开加载更多
	 */
	public void setFooterTipLabel(String pullTip, String readyTip);
	
	/**
	 * enable or disable pull up load more feature.
	 * 
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable);
	
	/**
	 * display the 'refreshing' view, and notify the listener to ‘onRefresh’.
	 */
	public void startRefreshing();
	
	public void startRefreshingAndScrollBack();
	
	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh();
	
	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore();
	
	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface OnRefreshListener {
		public void onRefresh();

		public void onLoadMore();
	}

}
