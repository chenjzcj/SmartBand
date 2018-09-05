package com.mtk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruanan.btnotification.R;

import java.util.ArrayList;

public class HomeAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mItemTexts;
    private ArrayList<Integer> mImgResId;

    public HomeAdapter(Context context, String[] itemTexts, ArrayList<Integer> imgResId) {
        this.mContext = context;
        this.mItemTexts = itemTexts;
        this.mImgResId = imgResId;
    }

    @Override
    public int getCount() {
        return mImgResId.size();
    }

    @Override
    public Object getItem(int position) {
        return mImgResId.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_home_gridview_item, null);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_item);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.iv.setImageResource(mImgResId.get(position));
        holder.tv.setText(mItemTexts[position]);
        return convertView;
    }

    class ViewHolder {
        ImageView iv;
        TextView tv;
    }
}
